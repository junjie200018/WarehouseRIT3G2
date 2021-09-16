package my.edu.tarc.warehouserit3g2.productBarcode

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentReceiveProductListBinding


class ProductList_Fragment : Fragment(), ReceiveProductAdapter.OnItemClickListener {


    private lateinit var binding: FragmentReceiveProductListBinding
    var receiveProduct = ArrayList<newProductBarcode>()
    lateinit var adapter: ReceiveProductAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}
    private lateinit var person: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receive_product_list_, container, false)

        // connect to database
        val db = Firebase.firestore

        //clear the arraylist
        receiveProduct.clear()
        myRecyclerView = binding.ReceiveProductRecycleView

        // view model of the user
        person = ViewModel.getInstance()


        // get the barcode data from the database
        db.collection("Barcode").orderBy("partNo")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {

                    val p = newProductBarcode(
                        "${document.id}",
                        "${document.data.get("partNo").toString()}",
                        "${document.data.get("quantity").toString()}"
                    )
                    receiveProduct.add(p)
                }

                adapter = ReceiveProductAdapter(receiveProduct,this)
                myRecyclerView.adapter = adapter

                // connect to the search function in adapter
                binding.receiveProductsearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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

    // click function of the recycleview
    override fun onItemClick(position: Int) {

        val clickedItem  = receiveProduct[position]
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        person.setbarcode(clickedItem.barodeNo)

        if(person.getPerson().role == "worker") {
            // move in worker navigation (navigate)
            navController.navigate(R.id.action_receiveProductList_Fragment_to_displayBarcode_Fragment)
        } else if(person.getPerson().role == "manager") {
            //  move in manager navigation (navmanager)
            navController.navigate(R.id.action_receiveProductList_Fragment2_to_displayBarcode_Fragment2)
        }

    }
}