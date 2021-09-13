package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.oned.Code128Writer
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayBarcodeBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductDisplayBinding


class displayBarcode_Fragment : Fragment() {

    private lateinit var binding: FragmentDisplayBarcodeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_barcode_, container, false)
        val args = displayBarcode_FragmentArgs.fromBundle(requireArguments())
        val barcode = args.barcodeNo

        displayBitmap(barcode)


        binding.btnBack.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_displayBarcode_Fragment_to_receiveProductList_Fragment)
        }

        return binding.root
    }
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