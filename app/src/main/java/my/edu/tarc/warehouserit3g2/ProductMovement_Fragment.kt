package my.edu.tarc.warehouserit3g2

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await
import my.edu.tarc.warehouserit3g2.databinding.FragmentProductMovementBinding


class ProductMovement_Fragment : Fragment() {

    private lateinit var binding :FragmentProductMovementBinding
    private lateinit var partNo :Array<String?>
    private lateinit var quantity :Array<Number?>
    private lateinit var warehouse :Array<String?>
    private lateinit var factory :Array<String?>

    private lateinit var selectedPart :String
    private lateinit var selectedQuantity :Number
    private lateinit var selectedWarehouse :String
    private lateinit var selectedFactory :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_movement_, container, false)
        val db = Firebase.firestore

        val sprPart = binding.sprPart
        val sprQuan = binding.sprQuan
        val sprWarehouse = binding.sprWarehouse
        val sprFactory = binding.sprFactory

        //firestore + coroutine IO
        GlobalScope.launch(IO) {
            //=================== get partNo and quantity ===================
            db.collection("Barcode").get()
                .addOnSuccessListener { data ->
                    partNo = arrayOfNulls(data.size())
                    quantity = arrayOfNulls(data.size())

                    for ((x,product) in data.withIndex()){
                        partNo[x] = product.data?.get("partNo").toString()
                        quantity[x] = product.data?.get("quantity").toString().toInt()
                    }

//                    data.forEach { product ->
//                        partNo[x] = product.data?.get("partNo").toString()
//                        quantity[x] = product.data?.get("quantity").toString().toInt()
//                        x++
//                    }
                    //Log.d(ContentValues.TAG, "partNo= ${partNo[0]}")
                }.await()

            //===================get warehouse===================
            db.collection("warehouse").get()
                .addOnSuccessListener { documents ->
                    warehouse = arrayOfNulls(documents.size())
                    for ((x,doc) in documents.withIndex()){
                        warehouse[x] = doc.id
                    }
                }.await()

            //===================get factory===================
            db.collection("factory").get()
                .addOnSuccessListener { documents ->
                    factory = arrayOfNulls(documents.size())
                    for ((x,doc) in documents.withIndex()){
                        factory[x] = doc.id
                    }
                }.await()

            binding.loadingIndi.visibility = View.INVISIBLE

            //===================coroutine UI===================
            withContext(Main) {
                //temp val for storing selected part's quanitiy
                var tempQuantity = arrayListOf<Number?>()

                //===================generate part dropdownlist===================
                val adapterPart: ArrayAdapter<String> = ArrayAdapter<String>(
                    activity?.applicationContext!!,
                    android.R.layout.simple_spinner_item,
                    partNo.distinct() //remove duplicate
                )
                adapterPart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sprPart.adapter = adapterPart

                //===================when click part===================
                sprPart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        //if selected part
                        override fun onItemSelected( parent: AdapterView<*>?,view: View?, position: Int,id: Long) {

                            selectedPart = parent?.getItemAtPosition(position).toString()

                            //clear existing
                            tempQuantity.clear()

                            //get value for quantity on selected part
                            for ((x, part) in partNo.withIndex()) {
                                if (part == selectedPart) {
                                    tempQuantity.add(quantity[x])
                                }
                            }
                            //convert list to array
                            var convert: Array<Number?> = tempQuantity.toTypedArray()

                            //==================== generate quantity dropdownlist ====================
                            val adapterQuan: ArrayAdapter<Number> = ArrayAdapter<Number>(
                                activity?.applicationContext!!,
                                android.R.layout.simple_spinner_item,
                                convert
                            )
                            adapterQuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sprQuan.adapter = adapterQuan
                            //=======================================================================
                        }

                        //if didnt select part
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // empty
                        }
                    }
                //==================== generate warehouse dropdownlist ====================
                val adapterWarehouse: ArrayAdapter<String> = ArrayAdapter<String>(
                    activity?.applicationContext!!,
                    android.R.layout.simple_spinner_item,
                    warehouse
                )
                adapterWarehouse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sprWarehouse.adapter = adapterWarehouse

                //==================== generate factory dropdownlist ====================
                val adapterFactory: ArrayAdapter<String> = ArrayAdapter<String>(
                    activity?.applicationContext!!,
                    android.R.layout.simple_spinner_item,
                    factory
                )
                adapterFactory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sprFactory.adapter = adapterFactory
            }

            //==================== on quantity click ====================
            sprQuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedQuantity = parent?.getItemAtPosition(position).toString().toInt()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //empty
                }
            }

            //==================== on warehouse click ====================
            sprWarehouse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedWarehouse = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //empty
                }
            }

            //==================== on factory click ====================
            sprFactory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedFactory = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //empty
                }
            }

            binding.btnSubmit.setOnClickListener(){
                CoroutineScope(IO).launch{
                    val selectedProd = hashMapOf(
                        "partNo" to selectedPart,
                        "quantity" to selectedQuantity,
                        "warehouse" to selectedWarehouse,
                        "factory" to selectedFactory
                    )

                    db.collection("Transfer").add(selectedProd)
                        .addOnSuccessListener {
                            Toast.makeText(context,"Sucessfully Requested",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context,"Action failed, Please Try Again",Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }



        // Inflate the layout for this fragment
        return binding.root
    }


}