package my.edu.tarc.warehouserit3g2

import android.app.Activity
import android.content.ContentValues
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await
import my.edu.tarc.warehouserit3g2.databinding.FragmentProductMovementBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ProductMovement_Fragment : Fragment() {

    private lateinit var binding :FragmentProductMovementBinding
    private lateinit var partNo :Array<String?>
    private lateinit var quantity :Array<Number?>
    private lateinit var warehouse :Array<String?>
    private lateinit var factory :Array<String?>
    private lateinit var warehouseLoc :Array<GeoPoint?>
    private lateinit var factoryLoc :Array<GeoPoint?>

    private lateinit var selectedPart :String
    private lateinit var selectedQuantity :Number


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_movement_, container, false)
        val db = Firebase.firestore

        val sprPart = binding.sprPart
        val sprQuan = binding.sprQuan
        val sprFrom = binding.sprFrom
        val sprTo = binding.sprTo
        var selectedFrom :String = ""
        var selectedTo :String = " "

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
                }.await()

            //===================get warehouse===================
            db.collection("Warehouse").get()
                .addOnSuccessListener { documents ->
                    warehouse = arrayOfNulls(documents.size())
                    warehouseLoc = arrayOfNulls(documents.size())
                    for ((x,doc) in documents.withIndex()){
                        warehouse[x] = doc.id
                        warehouseLoc[x] = doc.getGeoPoint("location")
                    }
                }.await()

            //===================get factory===================
            db.collection("Factory").get()
                .addOnSuccessListener { documents ->
                    factory = arrayOfNulls(documents.size())
                    factoryLoc = arrayOfNulls(documents.size())
                    for ((x,doc) in documents.withIndex()){
                        factory[x] = doc.id
                        factoryLoc[x] = doc.getGeoPoint("location")
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
                val adapterFrom: ArrayAdapter<String> = ArrayAdapter<String>(
                    activity?.applicationContext!!,
                    android.R.layout.simple_spinner_item,
                    (warehouse+factory)
                )
                adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sprFrom.adapter = adapterFrom

                //==================== generate factory dropdownlist ====================
                val adapterTo: ArrayAdapter<String> = ArrayAdapter<String>(
                    activity?.applicationContext!!,
                    android.R.layout.simple_spinner_item,
                    (factory+warehouse)
                )
                adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sprTo.adapter = adapterTo
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

            //==================== on from click ====================
            sprFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    if (duplicateCheck( parent?.getItemAtPosition(position).toString() ,selectedTo))
                        selectedFrom = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //empty
                }
            }

            //==================== on to click ====================
            sprTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    if (duplicateCheck(selectedFrom,parent?.getItemAtPosition(position).toString()))
                        selectedTo = parent?.getItemAtPosition(position).toString()

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //empty
                }
            }

            binding.btnSubmit.setOnClickListener(){

                if (duplicateCheck(selectedFrom,selectedTo)){
                    val wLoc = (warehouse+factory).indexOf(selectedFrom)
                    val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                    CoroutineScope(IO).launch{
                        val selectedProd = hashMapOf(
                            "partNo" to selectedPart,
                            "quantity" to selectedQuantity,
                            "from" to selectedFrom,
                            "to" to selectedTo,
                            "status" to "pending",
                            "location" to (warehouseLoc+factoryLoc)[wLoc]
                        )

                        db.collection("Transfer").document(currentTime).set(selectedProd)
                            .addOnSuccessListener {
                                Toast.makeText(context,"Sucessfully Requested",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context,"Action failed, Please Try Again",Toast.LENGTH_LONG).show()
                            }
                        selectedPart = ""
                        selectedQuantity = 0
                        selectedFrom = ""
                        selectedTo = ""
                    }
                }

            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun duplicateCheck(loc1 :String, loc2 :String) :Boolean{
        Log.d(ContentValues.TAG, "log= ${loc1}")
        Log.d(ContentValues.TAG, "log= ${loc2}")
        if (loc1 == loc2){
            Toast.makeText(context,"Departure and destination cannot be the same",Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}