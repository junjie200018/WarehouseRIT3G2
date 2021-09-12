package my.edu.tarc.warehouserit3g2

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.TrackingFragment

/*
 * Triggered by the Geofence.  Since we only have one active Geofence at once, we pull the request
 * ID from the first Geofence, and locate it within the registered landmark data in our
 * GeofencingConstants within GeofenceUtils, which is a linear string search. If we had  very large
 * numbers of Geofence possibilities, it might make sense to use a different data structure.  We
 * then pass the Geofence index into the notification, which allows us to have a custom "found"
 * message associated with each Geofence.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    lateinit var id :String

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofenceTransition = geofencingEvent.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                if (intent != null){
                    id = intent.getStringExtra("id")!!
                }

                Log.d(ContentValues.TAG, "geofence id= ${id}")

                GlobalScope.launch(Dispatchers.IO) {
                    Firebase.firestore.collection("Transfer").document(id)
                        .update("status", "complete")
                    Log.d(ContentValues.TAG, "geofence in")
                }
            }
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            //TrackingFragment.removeGeofences(context, triggeringGeofences)
        }
    }
}
