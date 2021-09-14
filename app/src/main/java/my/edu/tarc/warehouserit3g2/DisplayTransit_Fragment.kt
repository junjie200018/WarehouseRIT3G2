package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.DisplayTransit
import my.edu.tarc.warehouserit3g2.Data.DisplayTransitAdapter
import my.edu.tarc.warehouserit3g2.Data.Product
import my.edu.tarc.warehouserit3g2.Data.ProductAdapter
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayTransitBinding

class DisplayTransit_Fragment : Fragment(), DisplayTransitAdapter.OnItemClickListener {

    private lateinit var binding: FragmentDisplayTransitBinding
    var transitProduct: MutableList<DisplayTransit> = ArrayList()
    lateinit var adapter: DisplayTransitAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_transit, container, false)
        val db = Firebase.firestore
        transitProduct.clear()
        myRecyclerView = binding.TransitRecycleView


        db.collection("Transfer")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {


//                    if(document.data?.get("Status").toString() != "inTransit" && document.data?.get("Status").toString() != "complete") {
                    if(document.data?.get("status").toString() == "pending" ) {
                        val p = DisplayTransit("${document.data.get("partNo").toString()}", "${document.data.get("quantity").toString()}",
                        "${document.data.get("from").toString()}", "${document.data.get("to").toString()}")
                        transitProduct.add(p)
                    }
                }

                transitProduct.sortBy { it.partNo }

                adapter = DisplayTransitAdapter(transitProduct, this)
                myRecyclerView.adapter = adapter

                binding.TransitSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.TransitRecycleView


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
      //  Toast.makeText(context, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem : DisplayTransit = transitProduct[position]

//        val action : NavDirections = Display_Received_item_FragmentDirections.actionDisplayReceivedItemFragmentToOnReceivedDetailFragment("0", "view", clickedItem.SerialNo)
//
//        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }

}