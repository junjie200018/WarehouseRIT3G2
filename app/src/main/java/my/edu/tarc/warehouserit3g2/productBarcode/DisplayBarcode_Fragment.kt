package my.edu.tarc.warehouserit3g2.productBarcode

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayBarcodeBinding


class displayBarcode_Fragment : Fragment() {

    private lateinit var binding: FragmentDisplayBarcodeBinding
    private lateinit var person: ViewModel
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // in this page is use by worker and manager

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_display_barcode_, container, false)

        person = ViewModel.getInstance()


        displayBitmap(person.getbarcode())

        if(person.getPerson().role == "manager"){
            binding.btnDelete.setVisibility(VISIBLE)
        }

        // button for move to another page
        binding.btnBack.setOnClickListener {
            if(person.getPerson().role == "worker") {
                //  move in worker navigation (navigate)
                Navigation.findNavController(it).navigate(R.id.action_displayBarcode_Fragment_to_receiveProductList_Fragment)
            } else {
                // move in manager navigation (navmanager)
                Navigation.findNavController(it).navigate(R.id.action_displayBarcode_Fragment2_to_receiveProductList_Fragment2)
            }

        }



        binding.btnDelete.setOnClickListener {
            basicAlert(person.getbarcode())
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

    //generate barcode function
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


    fun basicAlert(barcodenumber :String) {

        val db = Firebase.firestore

        person = ViewModel.getInstance()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())

        //title of the dialog
        builder.setTitle("Delete Product")

        // message of the dialog
        builder.setMessage("Are you sure want to delete ? ")

        //save button of the dialog
        builder.setPositiveButton("Submit") { dialog, which ->
            var cannotDelete = 0

            db.collection("Barcode").document(barcodenumber)
                .get()
                .addOnSuccessListener { newProductResult ->

                    db.collection("ReceivedProduct")
                        .get()
                        .addOnSuccessListener { result ->

                            for( document in result){
                                if(document.data?.get("PartNo").toString() == newProductResult.data?.get("partNo").toString()
                                    && document.data?.get("Quantity").toString()==newProductResult.data?.get("quantity").toString()
                                    && (document.data.get("Status").toString() == "In Rack" || document.data.get("Status").toString() == "Received")){


                                    cannotDelete = 1
                                    break
                                }

                            }

                            if(cannotDelete == 1){

                                Toast.makeText(
                                    context,
                                    "Still got product in the rack or received, cannot delete",
                                    Toast.LENGTH_LONG
                                ).show()
                            }else{
                                db.collection("Barcode").document(barcodenumber)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Delete Successful.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        if(person.getPerson().role == "worker") {
                                            //  move in worker navigation (navigate)
                                            navController.navigate(R.id.action_displayBarcode_Fragment_to_receiveProductList_Fragment)
                                        } else {
                                            // move in manager navigation (navmanager)
                                            navController.navigate(R.id.action_displayBarcode_Fragment2_to_receiveProductList_Fragment2)
                                        }
                                    }
                            }

                        }

                }
        }

        // cancel button of the dialog
        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }





}