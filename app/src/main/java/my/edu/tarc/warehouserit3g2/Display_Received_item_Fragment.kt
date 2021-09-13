package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.common.collect.Collections2.filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.Product
import my.edu.tarc.warehouserit3g2.Data.ProductAdapter
import my.edu.tarc.warehouserit3g2.databinding.ActivityOnReceivedBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import java.util.Locale.filter
import androidx.appcompat.widget.SearchView
import com.google.common.collect.Sets.filter
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import java.util.*
import kotlin.collections.ArrayList


class Display_Received_item_Fragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentDisplayReceivedItemBinding
     var productList: MutableList<Product> = ArrayList()
    lateinit var adapter: ProductAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}
    lateinit var searchValue : ArrayList<Product>
    private lateinit var Person: PersonViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display__received_item_, container, false)
        val db = Firebase.firestore
        productList.clear()


        myRecyclerView = binding.productRecycleView



        searchValue = arrayListOf<Product>()




        val partNumber : Array<String?> = arrayOfNulls<String>(100)
        val serialNumber : Array<String?> = arrayOfNulls<String>(100)
        Person = PersonViewModel.getInstance()
        var fullname = Person.getUsername().fullName
        Log.w(ContentValues.TAG, "name = ${fullname}")
        db.collection("ReceivedProduct").whereEqualTo("ReceivedBy", fullname)
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    if(document.data?.get("Status").toString() != "Scrap" && document.data?.get("Status").toString() != "Transit") {

                        val p = Product("${document.data.get("PartNo").toString()}", "${document.id}", "${document.data?.get("ReceivedDate").toString()}", "${document.data?.get("Quantity").toString()}")
                        productList.add(p)

                        Log.w(ContentValues.TAG, "name2 = ${productList}")
                    }
                }

                productList.sortBy { it.partNo }

                adapter = ProductAdapter(productList, this)
                myRecyclerView.adapter = adapter

                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.productRecycleView


                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.w(ContentValues.TAG, "get value 3 = ${query}")

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.w(ContentValues.TAG, "get value 3 = ${newText}")

                       adapter.filter.filter(newText)


                        return false
                    }
                })
            }




      return binding.root
    }


    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem : Product = productList[position]
        Log.d(ContentValues.TAG, "DocumentSnapshot qty data: ${clickedItem}")
        val action : NavDirections = Display_Received_item_FragmentDirections.actionDisplayReceivedItemFragmentToOnReceivedDetailFragment("0", "view", clickedItem.SerialNo)

        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }

//    override fun onQueryTextSubmit(query: String?): Boolean {
//return false
//    }
//
//    override fun onQueryTextChange(newText: String?): Boolean {
////        val myRecyclerView : RecyclerView = binding.productRecycleView
////        if(newText!!.isNotEmpty()){
////            searchValue.clear()
////            val search = newText
////            Log.w(ContentValues.TAG, "search value 3 = ${newText}")
////            productList.forEach{
////                if (it.partNo.contains(search)){
////                    Log.w(ContentValues.TAG, "get value 3 = ${it}")
////                    searchValue.add(it)
////                }
////            }
////            myRecyclerView.adapter!!.notifyDataSetChanged()
////        }else{
////
////            searchValue.clear()
////            searchValue.addAll(productList)
////            myRecyclerView.adapter!!.notifyDataSetChanged()
////        }
//        return true
//    }

}