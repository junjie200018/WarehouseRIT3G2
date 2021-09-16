package my.edu.tarc.warehouserit3g2.receiveProduct

import android.content.ContentValues
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
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class OnReceivedDetail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnReceivedDetailBinding
    private lateinit var person: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_on_received_detail_, container, false)



        // get the data from previous page
        val args = OnReceivedDetail_FragmentArgs.fromBundle(requireArguments())
        var partNumberDatabase = ""
        var quantityDataBase = ""
        var serialNumber = ""

        // use for generate serial number
        val myArray3 = arrayOf<String>("SE","RE","SA","YO","RA","KE")

        // connect database
        val db = Firebase.firestore
        val place = args.place
        val seNo = args.serialNo

        // view model of the user
        person = ViewModel.getInstance()


        if(place == "receive") {

            // get the barcode data from the database
            db.collection("Barcode").document(args.barcodeValue)
                .get()
                .addOnSuccessListener { result ->

                    if (result.data != null) {


                        val randomInt = (0..5).random()

                        val rnd = Random()
                        val number: Int = rnd.nextInt(999999999)
                        var no = String.format("%07d", number)
                        val sdf = SimpleDateFormat("dd/M/yyyy")
                        val currentDate = sdf.format(Date())
                        val receivedBy = person.getPerson().fullName

                        partNumberDatabase = result.data?.get("partNo").toString()
                        quantityDataBase = result.data?.get("quantity").toString()
                        serialNumber = myArray3[randomInt] + no



                        val bitmap = generateQRCode(serialNumber)
                        binding.imagePreview.setImageBitmap(bitmap)
                        binding.tvtPartNo.text = partNumberDatabase
                        binding.tvtQuantity.text = quantityDataBase
                        binding.tvtSerialNo.text = serialNumber
                        binding.tvtStatus.text = "Received"
                        binding.tvtReceivedDate.text = currentDate
                        binding.tvtReceivedBy.text = receivedBy

                        // save the data
                        saveData(
                            partNumberDatabase,
                            quantityDataBase,
                            serialNumber,
                            "Received",
                            currentDate,
                            receivedBy
                        )

                        // button for navigate to onReceived fragment
                        binding.btnOk.setOnClickListener {
                            Navigation.findNavController(it)
                                .navigate(R.id.action_onReceivedDetail_Fragment_to_onReceived_Fragment)

                        }

                    }


                }.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "getfailedwith ", exception)
                }

        } else if(place == "view"){

            //get the receivedproduct data from database
            db.collection("ReceivedProduct").document(seNo)
                .get()
                .addOnSuccessListener { result ->

                    binding.tvtPartNo.text = result.data?.get("PartNo").toString()
                    binding.tvtQuantity.text = result.data?.get("Quantity").toString()
                    binding.tvtSerialNo.text = result.id
                    binding.tvtStatus.text = result.data?.get("Status").toString()
                    binding.tvtReceivedDate.text = result.data?.get("ReceivedDate").toString()
                    binding.tvtReceivedBy.text = result.data?.get("ReceivedBy").toString()

                    val bitmap = generateQRCode(result.id)
                    binding.imagePreview.setImageBitmap(bitmap)

                    // button for navigate to display received item fragment
                    binding.btnOk.setOnClickListener {
                        Navigation.findNavController(it).navigate(R.id.action_onReceivedDetail_Fragment_to_display_Received_item_Fragment)
                    }

                }.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "getfailedwith ", exception)
                }

        }

        return binding.root
    }


    // generate QRcode function
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

    //save record to database
    private fun saveData(partNo: String, quantity: String, serialNo: String, Status :String, Date :String, ReceivedBy :String){

        val db = Firebase.firestore
        val barcodeValue = hashMapOf(
            "PartNo" to partNo,
            "Quantity" to quantity.toInt(),
            "SerialNo" to serialNo,
            "Status" to Status,
            "ReceivedDate" to Date,
            "ReceivedBy" to ReceivedBy,
            "RackID" to "-",
            "RackInDate" to "-",
            "RackOutDate" to "-",
        )

        db.collection("ReceivedProduct").document(serialNo).set(barcodeValue)
    }

}