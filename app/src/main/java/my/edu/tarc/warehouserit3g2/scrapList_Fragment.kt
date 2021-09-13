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
import my.edu.tarc.warehouserit3g2.Data.ScrapAdapter
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentScrapListBinding


class scrapList_Fragment : Fragment(), ScrapAdapter.OnItemClickListener {

    private lateinit var binding: FragmentScrapListBinding
    var productList: MutableList<Product> = ArrayList()
    lateinit var adapter: ScrapAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}
    lateinit var searchValue : ArrayList<Product>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_scrap_list_, container, false)
        val db = Firebase.firestore
        productList.clear()
        myRecyclerView = binding.scrapproductRecycleView

        db.collection("ReceivedProduct").orderBy("PartNo")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    if(document.data?.get("Status").toString() == "Scrap") {

                        val p = Product("${document.data.get("PartNo").toString()}", "${document.id}","${document.data.get("RackOutDate").toString()}","")
                        productList.add(p)

                        Log.w(ContentValues.TAG, "name2 = ${productList}")
                    }
                }

                productList.sortBy { it.partNo }

                adapter = ScrapAdapter(productList, this)
                myRecyclerView.adapter = adapter

                binding.scrapsearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.scrapproductRecycleView


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
        val action : NavDirections = scrapList_FragmentDirections.actionScrapListFragmentToDisplayScrapFragment(clickedItem.SerialNo)

        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }
}