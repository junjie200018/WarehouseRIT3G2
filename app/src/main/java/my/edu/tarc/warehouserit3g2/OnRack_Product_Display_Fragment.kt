package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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
import my.edu.tarc.warehouserit3g2.Data.Product
import my.edu.tarc.warehouserit3g2.Data.ProductAdapter
import my.edu.tarc.warehouserit3g2.Data.RackProductAdapter
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductDisplayBinding


class OnRack_Product_Display_Fragment : Fragment(), ProductAdapter.OnItemClickListener  {

    private lateinit var binding: FragmentOnRackProductDisplayBinding
    var productList: MutableList<Product> = ArrayList()
    lateinit var adapter: RackProductAdapter
    lateinit var myRecyclerView : RecyclerView

    private val navController by lazy { NavHostFragment.findNavController(this)}

    lateinit var searchValue : ArrayList<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_rack_product_display, container, false)
        val db = Firebase.firestore
        val args = OnRack_Product_Display_FragmentArgs.fromBundle(requireArguments())
        val rackId = args.rackId
        productList.clear()
        myRecyclerView = binding.OnRackproductRecycleView
        Log.w(ContentValues.TAG, "get value 3 = ${rackId}")

        db.collection("ReceivedProduct")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {

                    if(document.data.get("Status") != "scrap") {
                        if (rackId == (document.data.get("RackID").toString())) {
                            val p = Product(
                                "${document.data.get("PartNo").toString()}",
                                "${document.id}"
                            )
                            productList.add(p)
                        }
                    }
                }

                adapter = RackProductAdapter(productList, this)
                myRecyclerView.adapter = adapter

                binding.searchOnRackProductView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.OnRackproductRecycleView


                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.w(ContentValues.TAG, "get value 3 = ${query}")

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.w(ContentValues.TAG, "get value 3 = ${newText}")

                        adapter.filter.filter(newText)
//

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
        val action : NavDirections = OnRack_Product_Display_FragmentDirections.actionOnRackProductDisplayFragmentToOnRackDisplayDetailFragment(clickedItem.SerialNo)

        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }


}