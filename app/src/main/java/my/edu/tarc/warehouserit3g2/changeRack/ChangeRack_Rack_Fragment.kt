package my.edu.tarc.warehouserit3g2.changeRack

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackRackBinding

class changeRack_Rack_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentChangeRackRackBinding
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_rack_rack, container, false)

        //button for scan rack's R code
        binding.changeRackrackScan.setOnClickListener {
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

        // get data from the previos page
        val args = changeRack_Rack_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNo
        var previosRackId = ""

        // connect to database
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents

                // get the received product data from databasse
                db.collection("ReceivedProduct").document(serialNo)
                    .get()
                    .addOnSuccessListener { result ->
                        // use to check the product save to rack or not
                        if (result.data?.get("RackID").toString() == scannedResult) {

                            Toast.makeText(
                                context,
                                "Product already in the rack ${scannedResult}",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            previosRackId = result.data?.get("RackID").toString()
                            db.collection("Rack").document(scannedResult)
                                .get()
                                .addOnSuccessListener { document ->

                                    // use to check the rack id exist in the database or not
                                    if (document.data == null) {

                                        Toast.makeText(
                                            context,
                                            "Invalid QR code. Please try again !!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    } else {
                                        Toast.makeText(context, "Valid Bar code", Toast.LENGTH_LONG)
                                            .show()
                                        // use to call the dialog
                                        basicAlert(scannedResult, serialNo, previosRackId)

                                    }
                                }
                        }
                    }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // dialog function
    fun basicAlert(RackId: String, serialNo: String, previosRackID: String) {

        val db = Firebase.firestore

        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())

        //title of the dialog
        builder.setTitle("Change Rack")

        // message of the dialog
        builder.setMessage("Are you sure change product ${serialNo} from ${previosRackID} to ${RackId} ")

        //save button of the dialog
        builder.setPositiveButton("Save") { dialog, which ->
            db.collection("ReceivedProduct").document(serialNo)
                .update(
                    mapOf(
                        "RackID" to RackId
                    )
                )
            Toast.makeText(
                context,
                "Change Successful",
                Toast.LENGTH_LONG
            ).show()

            navController.navigate(R.id.action_changeRack_Rack_Fragment_to_changeRack_product_Fragment)

        }

        // cancel button of the dialog
        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }


}