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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedBinding


class OnReceived_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentOnReceivedBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_received_, container, false)
        // Inflate the layout for this fragment

        binding.btnScan.setOnClickListener {
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
                binding.txtValue.text = scannedResult
                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                val valueBarcode : String = scannedResult



                db.collection("Barcode").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid Bar code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{
//                            Toast.makeText(context, "Valid Bar code", Toast.LENGTH_LONG).show()
                            val action : NavDirections = OnReceived_FragmentDirections.actionOnReceivedFragmentToOnReceivedDetailFragment(valueBarcode, "receive" , "0")

                            navController.navigate(action)
//                            val intent: Intent = Intent(this, OnReceivedDetail::class.java)
//                            intent.putExtra("BarcodeNumber", scannedResult)
//                            Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
//                            startActivity(intent)
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


//    override fun onSaveInstanceState(outState: Bundle) {
//
//        outState?.putString("scannedResult", scannedResult)
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        savedInstanceState?.let {
//            scannedResult = it.getString("scannedResult").toString()
//            binding.txtValue.text = scannedResult
//            Log.w(ContentValues.TAG, "partNo 3 = ${scannedResult}")
//        }
//    }



}