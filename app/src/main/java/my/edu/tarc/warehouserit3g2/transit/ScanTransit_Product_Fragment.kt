package my.edu.tarc.warehouserit3g2.transit

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentScanTransitProductBinding
import java.text.SimpleDateFormat
import java.util.*


class scanTransit_Product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentScanTransitProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_scan_transit_product, container, false
        )

        // run the scan QR code function
        binding.btnTransitProductScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }


    // scan QR code function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // get the data from the previous page
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val sdf = SimpleDateFormat("dd/M/yyyy")
        var rackOutDate = sdf.format(Date())

        // connect to database
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents

                // get the received product detail
                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->

                        // check the data is empty or not
                        if (result.data == null) {
                            Toast.makeText(
                                context,
                                "Invalid product QR code. Please try again !!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            var correct = 0
                            var transferID = ""

                            // get the transfer detail
                            db.collection("Transfer")
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (transferProduct in documents) {
                                        // check the partno and quantity of the transfer detail and received product detail
                                        if (result.data?.get("PartNo").toString() == transferProduct.data?.get("partNo").toString()
                                            && result.data?.get("Quantity").toString() == transferProduct.data?.get("quantity").toString()
                                            && transferProduct.data?.get("status").toString() == "pending")
                                        {
                                            correct = 1
                                            transferID = transferProduct.id
                                            break
                                        }
                                    }

                                    if (correct == 1) {
                                        Toast.makeText(
                                            context,
                                            "Transit successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        Log.d(ContentValues.TAG, " abaaba ${transferID}")

                                        // update the database
                                        db.collection("Transfer").document(transferID)
                                            .update(
                                                mapOf(
                                                    "status" to "ready",
                                                    "serialNo" to result.data?.get("SerialNo").toString()
                                                )
                                            )

                                        if(result.data?.get("RackInDate").toString() == "-"){
                                            rackOutDate = "-"
                                        }
                                        Log.d(ContentValues.TAG, " abaaba ${rackOutDate}")
                                        //update the database
                                        db.collection("ReceivedProduct").document(scannedResult)
                                            .update(
                                                mapOf(
                                                    "Status" to "Transit",
                                                   "RackOutDate" to rackOutDate
                                                )
                                            )
                                    }else{
                                        Toast.makeText(
                                            context,
                                            "Transit unsuccessful, Plaese match the product ID and quantity!!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}