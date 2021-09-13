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
import my.edu.tarc.warehouserit3g2.Data.RackProduct
import my.edu.tarc.warehouserit3g2.Data.RackProductAdapter
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductDisplayBinding


class OnRack_Product_Display_Fragment : Fragment(), ProductAdapter.OnItemClickListener  {

    private lateinit var binding: FragmentOnRackProductDisplayBinding
    var productList: MutableList<RackProduct> = ArrayList()
    lateinit var adapter: RackProductAdapter
    lateinit var myRecyclerView : RecyclerView

    private val navController by lazy { NavHostFragment.findNavController(this)}

    lateinit var searchValue : ArrayList<RackProduct>
    private lateinit var Person: PersonViewModel


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
        Person = PersonViewModel.getInstance()
        var fullname = Person.getPerson().fullName
        Log.w(ContentValues.TAG, "get value 3 = ${rackId}")

        db.collection("ReceivedProduct").whereEqualTo("ReceivedBy", fullname)
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {

                    if(document.data.get("Status") != "Scrap" && document.data.get("Status") != "Transit") {
                        if (rackId == (document.data.get("RackID").toString())) {
                            val p = RackProduct(
                                "${document.data.get("PartNo").toString()}",
                                "${document.id}","${document.data.get("RackID").toString()}","${document.data.get("RackInDate").toString()}",
                                "${document.data.get("Quantity").toString()}"
                            )
                            productList.add(p)
                        }
                    }
                }

                productList.sortBy { it.partNo }
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
        val clickedItem : RackProduct = productList[position]
        Log.d(ContentValues.TAG, "DocumentSnapshot qty data: ${clickedItem}")
        val action : NavDirections = OnRack_Product_Display_FragmentDirections.actionOnRackProductDisplayFragmentToOnRackDisplayDetailFragment(clickedItem.SerialNo)

        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }


}