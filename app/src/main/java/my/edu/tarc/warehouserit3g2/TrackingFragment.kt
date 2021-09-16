package my.edu.tarc.warehouserit3g2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 15f
const val LOCATION_REQUEST_CODE = 123

class TrackingFragment : Fragment() {
    lateinit var mapFragment: SupportMapFragment
    lateinit var gMap: GoogleMap
    lateinit var id: String
    lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var destinationLoc: GeoPoint
    private val navController by lazy { NavHostFragment.findNavController(this) }
    lateinit var product :String

    lateinit var client :FusedLocationProviderClient
    val loopTrack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            //Get a reference to the database, so your app can perform read and write operations//
            db.collection("Transfer").document(id)
                .update("location", geoPoint)


            val latmin = destinationLoc.latitude - 0.0001
            val latmax = destinationLoc.latitude + 0.0001
            val longmin = destinationLoc.longitude - 0.0001
            val longmax = destinationLoc.longitude + 0.0001

            if ((location.latitude >= latmin) && (location.latitude <= latmax)){
                if ((location.longitude >= longmin) && (location.longitude <= longmax)){

                    db.collection("Transfer").document(id)
                        .update("status", "complete")

                    db.collection("ReceivedProduct").document(product)
                        .update("Status", "Complete")

                    val builder = AlertDialog.Builder(this@TrackingFragment.requireContext())
                    builder.setTitle("Arrived!")
                    builder.setMessage("You have arrived the destination\nSelect \"Ok\" to back to homepage")

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        val action :NavDirections = TrackingFragmentDirections.actionTrackingFragmentToPickupListFragment()
                        navController.navigate(action)
                    }
                    builder.show()
                    requestLocationUpdates(false)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        gMap.uiSettings.isMyLocationButtonEnabled = true
        gMap.uiSettings.isZoomControlsEnabled = true

        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                permissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        } else {
            this.gMap.isMyLocationEnabled = true

            // Zoom to last known location
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    with(gMap) {
                        val latLng = LatLng(it.latitude, it.longitude)
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL))
                    }
                } else {
                    with(gMap) {
                        moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(65.01355297927051, 25.464019811372978),
                                CAMERA_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }
        }
        requestLocationUpdates(true)
    }

    val db = Firebase.firestore
//================================================================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        id = MapFragmentArgs.fromBundle(requireArguments()).id
        Log.d(ContentValues.TAG, "id= ${id}")

        GlobalScope.launch(IO) {
            var dest = ""
            var collection = ""
            db.collection("Transfer").document(id).get()
                .addOnSuccessListener { data ->
                    Log.d(ContentValues.TAG, "line 74")
                    dest = data?.get("to").toString()
                    product = data?.get("serialNo").toString()

                    if (dest.contains("Warehouse"))
                        collection = "Warehouse"
                    else
                        collection = "Factory"

                }.await()

            Log.d(ContentValues.TAG, "destination= ${dest}")
            Log.d(ContentValues.TAG, "col= ${collection}")

            db.collection(collection).document(dest).get()
                .addOnSuccessListener { data ->
                    destinationLoc = data.getGeoPoint("location")!!
                    Log.d(ContentValues.TAG, "destination1= ${destinationLoc}")
                }.await()


            withContext(Main) {

                var location = LatLng(destinationLoc!!.latitude, destinationLoc!!.longitude)

                gMap.addMarker(
                    MarkerOptions().position(location)
                        .title("Destination")
                ).showInfoWindow()

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, CAMERA_ZOOM_LEVEL))

            }
        }
        //empty
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.tMap) as SupportMapFragment
        mapFragment?.getMapAsync(callback)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(this.requireActivity())

        Log.d(ContentValues.TAG, "1234")
    }


    private fun requestLocationUpdates(track: Boolean) {
        val request = LocationRequest()

        //Specify how often your app should request the device’s location//
        request.setInterval(1000 * 10)

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        //variable
        client = LocationServices.getFusedLocationProviderClient(this.activity)

        val permission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (track) {
            //If the app currently has access to the location permission...//
            if (permission == PackageManager.PERMISSION_GRANTED) {
                //...then request location updates//
                client.requestLocationUpdates(request, loopTrack, null)
            }
        } else
            client.removeLocationUpdates(loopTrack)

    }

    override fun onDestroy() {
        super.onDestroy()
        requestLocationUpdates(false)
    }


    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == GEOFENCE_LOCATION_REQUEST_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this.requireContext(),
                    "This application needs background location to work on Android 10 and higher",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(ContentValues.TAG, "line 350")
            }
        }
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (
                grantResults.isNotEmpty() && (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                if (ActivityCompat.checkSelfPermission(
                        this.requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this.requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                gMap.isMyLocationEnabled = true
                callback
            } else {
                Toast.makeText(
                    this.context,
                    "The app needs location permission to function",
                    Toast.LENGTH_LONG
                ).show()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (grantResults.isNotEmpty() && grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this.context,
                        "This application needs background location to work on Android 10 and higher",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(ContentValues.TAG, "line 386")
                }
            }
        }
    }

}