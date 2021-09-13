package my.edu.tarc.warehouserit3g2

import android.app.AlertDialog
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
import androidx.navigation.NavDirections
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentScanScrapBinding
import java.text.SimpleDateFormat
import java.util.*


class ScanScrap_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentScanScrapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_scan_scrap, container, false)
        // Inflate the layout for this fragment
        binding.ScrapScan.setOnClickListener {

//            val intent: Intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("BarcodeNumber", scannedResult)
//            Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
//            startActivity(intent)

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
                binding.textView6.text = scannedResult
                binding.textView6 .text = scannedResult
                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                val valueBarcode : String = scannedResult



                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid Bar code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{
//                            Toast.makeText(context, "Valid Bar code", Toast.LENGTH_LONG).show()

                            if(result.data?.get("Status").toString() != "Scrap" && result.data?.get("Status").toString() != "Transit"){

                                basicAlert(scannedResult)
                            }else{
                                Toast.makeText(
                                    context,
                                    "The Product already become scrap or already transit. Please try again!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }


                    }
                    }


            } else {
                binding.textView6.text = "scan failed"
                Log.w(ContentValues.TAG, "scan failed")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun basicAlert(serialNo: String) {

        val db = Firebase.firestore
        var sdf = SimpleDateFormat("dd/M/yyyy")
        var currentDate = sdf.format(Date())

        db.collection("ReceivedProduct").document(serialNo)
            .get()
            .addOnSuccessListener { result ->

            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Scrap Meterial")


        builder.setMessage("Are you sure put product ${serialNo} to scrap ?? ")

        builder.setPositiveButton("Save") { dialog, which ->

            db.collection("ReceivedProduct").document(serialNo)
                .get()
                .addOnSuccessListener { result ->

                    if(result.data?.get("RackID").toString() == ""){
                        currentDate = ""
                    }

                    db.collection("ReceivedProduct").document(serialNo)
                        .update(
                            mapOf(
                                "RackOutDate" to currentDate.toString(),
                                "Status" to "Scrap"
                            )
                        )
                }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }



}