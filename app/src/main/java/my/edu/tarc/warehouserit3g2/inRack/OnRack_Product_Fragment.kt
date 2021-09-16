package my.edu.tarc.warehouserit3g2.inRack

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
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductBinding


class OnRack_Product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentOnRackProductBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}
    private  var checkExist = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_rack__product_, container, false)

        // run the scan QR code function
        binding.RackProductScan.setOnClickListener{
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }


    // scan QR code function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        //connect database
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents


                // get the receivedProduct detail
                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.data == null) {
                            Toast.makeText(
                                context,
                                "Invalid product QR code. Please try again !!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            // check the status of the received product
                            if((documents.data?.get("Status").toString() == "Received" ) || (documents.data?.get("Status").toString() == "In Rack") ){

                                // check the Rack id in teh received product exist or not
                                if(documents.data?.get("RackID").toString() != "-" ){
                                    checkExist = 1
                                }

                                if(checkExist == 0){
                                    val action : NavDirections = OnRack_Product_FragmentDirections.actionOnRackProductFragmentToOnRackRackFragment(scannedResult)
                                    navController.navigate(action)

                                }else{

                                    Toast.makeText(
                                        context,
                                        "The Product already exist in a rack.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    checkExist = 0
                                }
                            }else{
                                Toast.makeText(
                                    context,
                                    "The Product already become scrap or already transit. Please try again!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}