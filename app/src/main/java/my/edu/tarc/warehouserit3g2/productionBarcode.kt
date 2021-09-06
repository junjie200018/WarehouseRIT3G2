package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import my.edu.tarc.warehouserit3g2.databinding.ActivityProductionBarcodeBinding
import org.w3c.dom.Text
import java.util.*

class productionBarcode : AppCompatActivity() {

    private lateinit var binding: ActivityProductionBarcodeBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_production_barcode)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_production_barcode)
        binding.submitBtn.setOnClickListener {

            val productID = binding.EnterProductID.text.toString()
            val qty = binding.EnterQty.text.toString()
            val db = Firebase.firestore

            val rnd = Random()
            val number: Int = rnd.nextInt(999999999)
            var no = String.format("%07d", number)
            var pdID = ""
            var partNumberDatabase = ""
            var quantityDataBase = ""
            var checkExist = 0

            if (productID.isEmpty() || qty.isEmpty()) {

                Toast.makeText(
                    applicationContext,
                    "Please Enter the part number and quantity !!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                db.collection("Barcode")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                            pdID = document.id
                            partNumberDatabase = document.data.get("partNo").toString()
                            quantityDataBase = document.data.get("quantity").toString()


                            if (productID == partNumberDatabase && qty == quantityDataBase) {
                                Log.w(ContentValues.TAG, "Error .")
                                Toast.makeText(
                                    applicationContext,
                                    "The part number and quantity is existed. Please try another!",
                                    Toast.LENGTH_LONG
                                ).show()
                                checkExist = 1
                                break
                            } else {
                                Log.w(ContentValues.TAG, "partNo 2 = ${partNumberDatabase}")
                                Log.w(ContentValues.TAG, "quantity 3 = ${quantityDataBase}")
                            }

//

                        }

                        if(checkExist != 1) {
                            while (pdID == no) {
                                no = rnd.nextInt(999999999).toString().format("%06d", number)
                                Log.w(ContentValues.TAG, "repeat")
                            }
                            Log.w(ContentValues.TAG, "productID 1 = ${no}")


                            val barcodeValue = hashMapOf(
                                "partNo" to productID,
                                "quantity" to qty.toString().toInt()
                            )

                            db.collection("Barcode").document(no).set(barcodeValue)

                            displayBitmap(no)
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents.", exception)
                    }


            }


        }
    }


    private fun displayBitmap(value: String) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_production_barcode)
        val widthPixels = resources.getDimensionPixelSize(R.dimen.width_barcode)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.height_barcode)

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