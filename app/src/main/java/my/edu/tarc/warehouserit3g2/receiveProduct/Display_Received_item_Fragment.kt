package my.edu.tarc.warehouserit3g2.receiveProduct

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
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
import my.edu.tarc.warehouserit3g2.R
import kotlin.collections.ArrayList


class Display_Received_item_Fragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentDisplayReceivedItemBinding
     var productList: MutableList<Product> = ArrayList()
    lateinit var adapter: ProductAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}
    private lateinit var person: ViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_display__received_item_, container, false)
        val db = Firebase.firestore
        productList.clear()


        myRecyclerView = binding.productRecycleView

        // get the view model data
        person = ViewModel.getInstance()
        var fullname = person.getPerson().fullName

        // get the received product data from database
        db.collection("ReceivedProduct").whereEqualTo("ReceivedBy", fullname)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {

                    // check the status
                    if(document.data?.get("Status").toString() == "In Rack" || document.data?.get("Status").toString() == "Received" ) {

                        val p = Product("${document.data.get("PartNo").toString()}", "${document.id}", "${document.data?.get("ReceivedDate").toString()}", "${document.data?.get("Quantity").toString()}")
                        productList.add(p)

                    }
                }

                // sort the product list
                productList.sortBy { it.partNo }


                // connect to search function
                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {

                       adapter.filter.filter(newText)
                        return false
                    }
                })
                adapter = ProductAdapter(productList, this)
                myRecyclerView.adapter = adapter
            }




      return binding.root
    }

    // click function in recycleview
    override fun onItemClick(position: Int) {

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        val clickedItem : Product = productList[position]
        val action : NavDirections = Display_Received_item_FragmentDirections.actionDisplayReceivedItemFragmentToOnReceivedDetailFragment(
                "0",
                "view",
                clickedItem.SerialNo
            )
        navController.navigate(action)
    }

}