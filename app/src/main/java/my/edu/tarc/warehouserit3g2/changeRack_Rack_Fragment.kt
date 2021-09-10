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
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackRackBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedBinding

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

        binding.changeRackrackScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val args = changeRack_Rack_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNo
        var previosRackId = ""

        val db = Firebase.firestore

        if (result != null) {
            Log.w(ContentValues.TAG, "partNo 2 ")
            if (result.contents != null) {
                scannedResult = result.contents
                binding.textView6.text = scannedResult
                binding.txtValue3.text = scannedResult
                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                val valueBarcode: String = scannedResult

                db.collection("ReceivedProduct").document(serialNo)
                    .get()
                    .addOnSuccessListener { result ->
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
                                    if (document.data == null) {
                                        Toast.makeText(
                                            context,
                                            "Invalid QR code. Please try again !!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(context, "Valid Bar code", Toast.LENGTH_LONG)
                                            .show()
                                        basicAlert(scannedResult, serialNo, previosRackId)

//                            // add pop up menu
                                    }
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

    fun basicAlert(RackId: String, serialNo: String, previosRackID :String) {

        val db = Firebase.firestore

        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Change Rack")


        builder.setMessage("Are you sure change product ${serialNo} from ${previosRackID} to ${RackId} ")

        builder.setPositiveButton("Save") { dialog, which ->
            db.collection("ReceivedProduct").document(serialNo)
                .update(
                    mapOf(
                        "RackID" to RackId
                    )
                )

           navController.navigate(R.id.action_changeRack_Rack_Fragment_to_changeRack_product_Fragment)

        }

        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }


}