package my.edu.tarc.warehouserit3g2

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
import my.edu.tarc.warehouserit3g2.databinding.FragmentScanScrapBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentScanTransitProductBinding


class scanTransit_Product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentScanTransitProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_scan_transit_product, container, false)

        binding.btnTransitProductScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val db = Firebase.firestore

        if (result != null) {
            Log.w(ContentValues.TAG, "partNo 2 ")
            if (result.contents != null) {
                scannedResult = result.contents

                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                val valueBarcode : String = scannedResult



                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid product QR code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{

                            var correct = 0
                            var transferID = ""
                            db.collection("Transfer")
                                .get()
                                .addOnSuccessListener { documents ->
                                    for(transferProduct in documents){
                                        if(result.data?.get("PartNo").toString() == transferProduct.data?.get("partNo") && result.data?.get("Quantity").toString() == transferProduct.data?.get("quantity")){
                                            correct = 1
                                            transferID = transferProduct.id
                                            break
                                        }
                                    }

                                    if(correct == 1){
                                        db.collection("Transfer").document(transferID)
                                            .update(
                                                mapOf(
                                                    "status" to "inTransit"
                                                )
                                            )
                                        db.collection("ReceivedProduct").document(transferID)
                                            .update(
                                                mapOf(
                                                    "Status" to "Transit"
                                                //add serial no
                                                )
                                            )
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