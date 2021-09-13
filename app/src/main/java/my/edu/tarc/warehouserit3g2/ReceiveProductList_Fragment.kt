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
import my.edu.tarc.warehouserit3g2.Data.ReceiveProductAdapter
import my.edu.tarc.warehouserit3g2.Data.newProductBarcode
import my.edu.tarc.warehouserit3g2.databinding.FragmentReceiveProductListBinding


class ReceiveProductList_Fragment : Fragment(), ReceiveProductAdapter.OnItemClickListener {


    private lateinit var binding: FragmentReceiveProductListBinding
    var receiveProduct = ArrayList<newProductBarcode>()
    lateinit var adapter: ReceiveProductAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_receive_product_list_, container, false)
        val db = Firebase.firestore
        receiveProduct.clear()
        myRecyclerView = binding.ReceiveProductRecycleView

        db.collection("Barcode").orderBy("partNo")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    val p = newProductBarcode(
                        "${document.id}",
                        "${document.data.get("partNo").toString()}",
                        "${document.data.get("quantity").toString()}"
                    )
                    receiveProduct.add(p)
                }

                adapter = ReceiveProductAdapter(receiveProduct,this)
                myRecyclerView.adapter = adapter

                binding.receiveProductsearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
//                    val myRecyclerView : RecyclerView = binding


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
        val clickedItem  = receiveProduct[position]
        Log.d(ContentValues.TAG, "DocumentSnapshot qty data: ${clickedItem}")
        val action : NavDirections = ReceiveProductList_FragmentDirections.actionReceiveProductListFragmentToDisplayBarcodeFragment(clickedItem.barodeNo)
        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }
}