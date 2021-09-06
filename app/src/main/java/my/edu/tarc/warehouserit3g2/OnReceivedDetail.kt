package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import my.edu.tarc.warehouserit3g2.databinding.ActivityOnReceivedDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class OnReceivedDetail : AppCompatActivity() {
    private lateinit var binding: ActivityOnReceivedDetailBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_received_detail)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_received_detail)

        var partNumberDatabase = ""
        var quantityDataBase = ""
        var serialNumber = ""
        val myArray3 = arrayOf<String>("SE","RE","SA","YO","RA","KE")
        val bundle = intent.extras
        val barcode = bundle?.getString("BarcodeNumber").toString()
        val db = Firebase.firestore
        Log.w(ContentValues.TAG, "barcode 2 = ${barcode}")

        db.collection("Barcode").document(barcode)
            .get()
            .addOnSuccessListener { result ->
                Log.d(ContentValues.TAG, "Abaaba")
                if(result.data != null){

                    Log.d(ContentValues.TAG, "DocumentSnapshot result data: ${result.data}")
                    val randomInt = (0..5).random()

                    val rnd = Random()
                    val number: Int = rnd.nextInt(999999999)
                    var no = String.format("%07d", number)
                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    val currentDate = sdf.format(Date())
                    val receivedBy = "Data Kang"

                    partNumberDatabase = result.data?.get("partNo").toString()
                    quantityDataBase = result.data?.get("quantity").toString()
                    serialNumber = myArray3[randomInt] + no

                    Log.d(ContentValues.TAG, "DocumentSnapshot part data: ${partNumberDatabase}")
                    Log.d(ContentValues.TAG, "DocumentSnapshot qty data: ${quantityDataBase}")




                    Log.d(ContentValues.TAG, "Serial number: ${serialNumber}")
                    val bitmap = generateQRCode(serialNumber)
                    binding.imagePreview.setImageBitmap(bitmap)

                    binding.tvtPartNo.text = partNumberDatabase.toString()
                    binding.tvtQuantity.text = quantityDataBase.toString()
                    binding.tvtSerialNo.text = serialNumber.toString()
                    binding.tvtStatus.text = "received"
                    binding.tvtReceivedDate.text = currentDate
                    binding.tvtReceivedBy.text = receivedBy
                    saveData(partNumberDatabase, quantityDataBase, serialNumber, "received", currentDate,receivedBy)

                    binding.btnOk.setOnClickListener{
                        val intent:Intent = Intent(this, On_Received::class.java)
                        startActivity(intent)
                    }

                }



            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "getfailedwith ", exception)
            }
    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 400
        val height = 400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) { Log.d(ContentValues.TAG, "generateQRCode: ${e.message}") }
        return bitmap
    }

    private fun saveData(partNo: String, quantity: String, serialNo: String, Status :String, Date :String, ReceivedBy :String){

        val db = Firebase.firestore
        val barcodeValue = hashMapOf(
            "PartNo" to partNo,
            "quantity" to quantity.toInt(),
            "serialNo" to serialNo,
            "Status" to Status,
            "ReceivedDate" to Date,
            "ReceivedBy" to ReceivedBy
        )

        db.collection("ReceivedProduct").document(serialNo).set(barcodeValue)
    }
}