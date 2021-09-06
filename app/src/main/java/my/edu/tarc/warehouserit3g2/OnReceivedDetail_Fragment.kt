package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class OnReceivedDetail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnReceivedDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_received_detail_, container, false)



        val args = OnReceivedDetail_FragmentArgs.fromBundle(requireArguments())
        var partNumberDatabase = ""
        var quantityDataBase = ""
        var serialNumber = ""
        val myArray3 = arrayOf<String>("SE","RE","SA","YO","RA","KE")
        val db = Firebase.firestore
        val place = args.place
        val seNo = args.serialNo


      //  binding.tvtPartNo.text = args.barcodeValue
        Log.w(ContentValues.TAG, "partNo 4 = ${args.barcodeValue}")

        if(place == "receive") {


            db.collection("Barcode").document(args.barcodeValue)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(ContentValues.TAG, "Abaaba")
                    if (result.data != null) {

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

                        Log.d(
                            ContentValues.TAG,
                            "DocumentSnapshot part data: ${partNumberDatabase}"
                        )
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
                        saveData(
                            partNumberDatabase,
                            quantityDataBase,
                            serialNumber,
                            "received",
                            currentDate,
                            receivedBy
                        )

                        binding.btnOk.setOnClickListener {
                            Navigation.findNavController(it)
                                .navigate(R.id.action_onReceivedDetail_Fragment_to_onReceived_Fragment)

                        }

                    }


                }.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "getfailedwith ", exception)
                }
        } else if(place == "view"){
            db.collection("ReceivedProduct").document(seNo)
                .get()
                .addOnSuccessListener { result ->

                    binding.tvtPartNo.text = result.data?.get("PartNo").toString()
                    binding.tvtQuantity.text = result.data?.get("quantity").toString()
                    binding.tvtSerialNo.text = result.id
                    binding.tvtStatus.text = result.data?.get("Status").toString()
                    binding.tvtReceivedDate.text = result.data?.get("ReceivedDate").toString()
                    binding.tvtReceivedBy.text = result.data?.get("ReceivedBy").toString()

                    val bitmap = generateQRCode(result.id)
                    binding.imagePreview.setImageBitmap(bitmap)

                    binding.btnOk.setOnClickListener {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_onReceivedDetail_Fragment_to_display_Received_item_Fragment)

                    }

                }.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "getfailedwith ", exception)
                }

        }

        return binding.root
    }


    fun generateQRCode(text: String): Bitmap {
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