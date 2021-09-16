package my.edu.tarc.warehouserit3g2.productBarcode

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentReceiveProductBinding
import java.util.*


class receiveProduct_Fragment : Fragment() {


    private lateinit var binding: FragmentReceiveProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receive_product_, container, false)

        // button for generate new product
        binding.submitBtn.setOnClickListener {

            val productID = binding.EnterProductID.text.toString().uppercase(Locale.getDefault())
            val qty = binding.EnterQty.text.toString()
            val db = Firebase.firestore

            val rnd = Random()
            val number: Int = rnd.nextInt(999999999)
            var no = String.format("%07d", number)
            var pdID = ""
            var partNumberDatabase = ""
            var quantityDataBase = ""
            var checkExist = 0

            if (productID.isEmpty() || qty.isEmpty() || productID.isBlank() || qty.isBlank()) {

                Toast.makeText(
                    context,
                    "Please Enter the part number and quantity !!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.EnterProductID.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.EnterQty.onEditorAction(EditorInfo.IME_ACTION_DONE)

                // get the barcode data from the databse
                db.collection("Barcode")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {

                            pdID = document.id
                            partNumberDatabase = document.data.get("partNo").toString()
                            quantityDataBase = document.data.get("quantity").toString()



                            if (productID == partNumberDatabase && qty == quantityDataBase) {

                                Toast.makeText(
                                    context,
                                    "The part number and quantity is existed. Please try another!",
                                    Toast.LENGTH_LONG
                                ).show()
                                checkExist = 1
                                break
                            }

                            // check if barcode number is same, generate another barcode number
                            if(pdID == no){
                                no = rnd.nextInt(999999999).toString().format("%06d", number)
                            }
                        }

                        // check the barcode exist or not
                        if (checkExist != 1) {

                            val barcodeValue = hashMapOf(
                                "partNo" to productID,
                                "quantity" to qty.toString().toInt()
                            )

                            // save data to databse
                            db.collection("Barcode").document(no).set(barcodeValue)

                            // generate barcode
                            displayBitmap(no)
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents.", exception)
                    }
            }
        }
        return binding.root
    }

    // display barcode function
    private fun displayBitmap(value: String) {

        val widthPixels = 450
        val heightPixels = 100

        val white = "#ffffff"
        val purple = "#ffbb86fc"
        val borderInt: Int = Color.parseColor(purple)
        val backgroundInt: Int = Color.parseColor(white)
        binding.imageBarcode.setImageBitmap(

            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = borderInt,
                backgroundColor = backgroundInt,
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
        binding.textBarcodeNumber.text = value
    }

    // generate barcode function
    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }


}