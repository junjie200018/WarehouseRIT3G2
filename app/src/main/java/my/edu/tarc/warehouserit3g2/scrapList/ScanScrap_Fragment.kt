package my.edu.tarc.warehouserit3g2.scrapList

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_scrap, container, false)

        // button for scan the QR code
        binding.ScrapScan.setOnClickListener {

            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }

    // scan function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents


                // get the received product detail
                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid Bar code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{

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


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // dialog function
    fun basicAlert(serialNo: String) {

        val db = Firebase.firestore
        var sdf = SimpleDateFormat("dd/M/yyyy")
        var currentDate = sdf.format(Date())


        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())

        // title of the dialog
        builder.setTitle("Scrap Meterial")

        // show the message in the dialog
        builder.setMessage("Are you sure put product ${serialNo} to scrap ?? ")

        builder.setPositiveButton("Save") { dialog, which ->

            // get the receivedProduct detail
            db.collection("ReceivedProduct").document(serialNo)
                .get()
                .addOnSuccessListener { result ->

                    // update database
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