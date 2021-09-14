package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.Product
import my.edu.tarc.warehouserit3g2.Data.ProductAdapter
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import androidx.appcompat.widget.SearchView
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import kotlin.collections.ArrayList


class Display_Received_item_Fragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentDisplayReceivedItemBinding
     var productList: MutableList<Product> = ArrayList()
    lateinit var adapter: ProductAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}
    lateinit var searchValue : ArrayList<Product>
    private lateinit var person: ViewModel



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
        person = ViewModel.getInstance()
        var fullname = person.getPerson().fullName

        db.collection("ReceivedProduct").whereEqualTo("ReceivedBy", fullname)
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {


                    if(document.data?.get("Status").toString() == "In Rack" || document.data?.get("Status").toString() != "Received") {

                        val p = Product("${document.data.get("PartNo").toString()}", "${document.id}", "${document.data?.get("ReceivedDate").toString()}", "${document.data?.get("Quantity").toString()}")
                        productList.add(p)

                    }
                }

                productList.sortBy { it.partNo }

                adapter = ProductAdapter(productList, this)
                myRecyclerView.adapter = adapter

                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.productRecycleView


                    override fun onQueryTextSubmit(query: String?): Boolean {


                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {

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
        val action : NavDirections = Display_Received_item_FragmentDirections.actionDisplayReceivedItemFragmentToOnReceivedDetailFragment("0", "view", clickedItem.SerialNo)

        navController.navigate(action)

    }

}