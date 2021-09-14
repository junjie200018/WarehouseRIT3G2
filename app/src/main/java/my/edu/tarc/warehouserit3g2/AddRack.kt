package my.edu.tarc.warehouserit3g2

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import my.edu.tarc.warehouserit3g2.databinding.FragmentAddRackBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentReceiveProductBinding


class AddRack : Fragment() {

    private lateinit var binding: FragmentAddRackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_rack, container, false)

        binding.addR.setOnClickListener {
            val rackId = binding.ID.text.toString()
            if(rackId.isEmpty() || rackId.isBlank()){

                Toast.makeText(context, "Please Enter the new Rack Number !!", Toast.LENGTH_SHORT).show()

            }else{

                val db = Firebase.firestore
                var exist = 0


                db.collection("Rack")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result){
                            if(document.id == rackId){
                                exist = 1
                                Toast.makeText(context, "${rackId} already exist !!", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }

                        if(exist == 0){
                            Toast.makeText(context, "Got Value", Toast.LENGTH_SHORT).show()
                            val bitmap = generateQRCode(rackId)

                            binding.imageQRcode.setImageBitmap(bitmap)

                            binding.ID.onEditorAction(EditorInfo.IME_ACTION_DONE)

                            val barcodeValue = hashMapOf(
                                "Rack ID" to rackId
                            )
                            db.collection("Rack").document(rackId).set(barcodeValue)
                        }

                    }



            }
        }

        return binding.root
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



}