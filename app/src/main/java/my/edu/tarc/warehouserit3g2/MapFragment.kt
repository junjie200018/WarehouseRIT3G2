package my.edu.tarc.warehouserit3g2

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MapFragment : Fragment() {

    lateinit var googleMap: GoogleMap
    lateinit var map: SupportMapFragment

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ActivityCompat.requestPermissions(
            this.requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
        )

        val db = Firebase.firestore
        val args = MapFragmentArgs.fromBundle(requireArguments())

//        Log.d(ContentValues.TAG, "id= ${id}")

        GlobalScope.launch(IO) {

            db.collection("Transfer").document(args.id)
                .addSnapshotListener { value, e ->

                    //time
                    val currentTime =
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    //null check
                    if (value != null && value.exists()) {
                        //get from db
                        val geoPoint = value.getGeoPoint("location")
                        var location = LatLng(geoPoint!!.latitude, geoPoint!!.longitude)

                        //map
                        map.getMapAsync {
                            googleMap = it
                            //marker and zoom
                            var marker = googleMap.addMarker(MarkerOptions()
                                .position(location)
                                .title(currentTime)
                            )
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(marker.position, 15f)
                            )
                            Log.d(ContentValues.TAG, "time= ${currentTime}")
                        }
                    } else {
                        Toast.makeText(context, "Error found is $e", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
//            withContext(Main){
//
//            }
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            googleMap.isMyLocationEnabled = false
        } else {
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = childFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
    }

}