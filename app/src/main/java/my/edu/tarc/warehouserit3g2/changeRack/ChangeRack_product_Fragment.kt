package my.edu.tarc.warehouserit3g2.changeRack

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
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackProductBinding

class changeRack_product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentChangeRackProductBinding
    private val navController by lazy { NavHostFragment.findNavController(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_change_rack_product, container, false
        )

        //button for scan received product QR code
        binding.ChangeRackProductScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }

    // run the scan QR code function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //get the scaned result
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        //connect to data base
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents

                // get the received product data from database
                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.data == null) {
                            Toast.makeText(
                                context,
                                "Invalid QR code. Please try again !!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            //check the status of the received product
                            if (result.data?.get("Status")
                                    .toString() != "Scrap" && result.data?.get("Status")
                                    .toString() != "Transit"
                            ) {
                                if (result.data?.get("RackID").toString() != "") {

                                    //move to another page call changeRack_Rack Fragment
                                    val action: NavDirections =
                                        changeRack_product_FragmentDirections.actionChangeRackProductFragmentToChangeRackRackFragment(
                                            scannedResult
                                        )
                                    navController.navigate(action)

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Product not in rack. Please try again !!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Product already become scrap or already transit",
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