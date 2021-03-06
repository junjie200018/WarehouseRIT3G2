package my.edu.tarc.warehouserit3g2.receiveProduct

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedBinding


class OnReceived_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentOnReceivedBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_received_, container, false)

        // button for scan QR function
        binding.btnScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }

        return binding.root
    }

    // scan QR function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        // get the scanned result
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        // connect to the database
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents


                val valueBarcode : String = scannedResult

                // get the barcode data from database
                db.collection("Barcode").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid Bar code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{

                            val action : NavDirections = OnReceived_FragmentDirections.actionOnReceivedFragmentToOnReceivedDetailFragment(
                                    valueBarcode,
                                    "receive",
                                    "0"
                                )

                            navController.navigate(action)

                        }
                    }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }






}