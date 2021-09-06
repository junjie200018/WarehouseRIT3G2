package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import my.edu.tarc.warehouserit3g2.databinding.ActivityOnReceivedBinding
import my.edu.tarc.warehouserit3g2.databinding.ActivityProductionBarcodeBinding

class On_Received : AppCompatActivity() {

    var scannedResult: String = ""
    private lateinit var binding: ActivityOnReceivedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_received)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_received)


        binding.btnScan.setOnClickListener {
            run {
                IntentIntegrator(this@On_Received).initiateScan();
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents
                binding.textView6.text = scannedResult
                binding.txtValue.text = scannedResult

                db.collection("Barcode").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(applicationContext, "Invalid Bar code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{
                            val intent: Intent = Intent(this, OnReceivedDetail::class.java)
                            intent.putExtra("BarcodeNumber", scannedResult)
                            Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                            startActivity(intent)
                        }
                    }


            } else {
                binding.textView6.text = "scan failed"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {

        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            scannedResult = it.getString("scannedResult").toString()
            binding.txtValue.text = scannedResult
            Log.w(ContentValues.TAG, "partNo 3 = ${scannedResult}")
        }
    }


}