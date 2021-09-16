package my.edu.tarc.warehouserit3g2.displayRackItem

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.ProductAdapter
import my.edu.tarc.warehouserit3g2.Data.RackProduct
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductDisplayBinding


class OnRack_Product_Display_Fragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentOnRackProductDisplayBinding
    var productList: MutableList<RackProduct> = ArrayList()
    lateinit var adapter: RackProductAdapter
    lateinit var myRecyclerView: RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this) }
    private lateinit var person: ViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_rack_product_display, container, false)
        val db = Firebase.firestore

        //get value from the previous page
        val args = OnRack_Product_Display_FragmentArgs.fromBundle(requireArguments())
        val rackId = args.rackId

        //clear the mutableList data
        productList.clear()

        myRecyclerView = binding.OnRackproductRecycleView

        // view model that store user detail
        person = ViewModel.getInstance()
        var fullname = person.getPerson().fullName


        //get the receivedProduct detail from database
        db.collection("ReceivedProduct")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {

                    if (document.data?.get("Status") == "In Rack" || document.data?.get("Status") == "Received") {
                        if (rackId == (document.data.get("RackID").toString())) {

                            val p = RackProduct(
                                "${document.data.get("PartNo").toString()}",
                                "${document.id}",
                                "${document.data.get("RackID").toString()}",
                                "${document.data.get("RackInDate").toString()}",
                                "${document.data.get("Quantity").toString()}"
                            )
                            productList.add(p)
                        }
                    }
                }

                // sort the detail in the product list
                productList.sortBy { it.partNo }
                adapter = RackProductAdapter(productList, this)
                myRecyclerView.adapter = adapter


                // connection of the search function in adapter
                binding.searchOnRackProductView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {

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

    //click function of the recycle view
    override fun onItemClick(position: Int) {

        // get the clicked product
        val clickedItem: RackProduct = productList[position]
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        val action: NavDirections =
            OnRack_Product_Display_FragmentDirections.actionOnRackProductDisplayFragmentToOnRackDisplayDetailFragment(
                clickedItem.SerialNo
            )

        navController.navigate(action)

    }


}