package my.edu.tarc.warehouserit3g2.searchStock

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentSearchStockBinding
import my.edu.tarc.warehouserit3g2.stockInOut.Stock


class SearchStock_Fragment : Fragment() {
    private lateinit var binding: FragmentSearchStockBinding
    private lateinit var myRecyclerView : RecyclerView
    private var SearchStockList = ArrayList<Stock>()
    private lateinit var adapter: SearchStockAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_stock_, container, false)

        SearchStockList.clear()

        myRecyclerView = binding.stocksearchRecyclerView
        myRecyclerView.setHasFixedSize(true)

        val db = Firebase.firestore
        db.collection("ReceivedProduct")
            .orderBy("PartNo")
            .get()
            .addOnSuccessListener { result ->
                for (recPro in result) {
                    val recP = Stock(
                        recPro.data?.get("PartNo").toString(),
                        recPro.data?.get("ReceivedBy").toString(),
                        recPro.data?.get("ReceivedDate").toString(),
                        recPro.data?.get("Quantity").toString(),
                        recPro.data?.get("SerialNo").toString(),
                        recPro.data?.get("Status").toString(),
                        recPro.data?.get("RackID").toString(),
                        recPro.data?.get("RackInDate").toString(),
                        recPro.data?.get("RackOutDate").toString(),
                    )
                    SearchStockList.add(recP)
                }
                val searchIcon = binding.stockSearch.findViewById<ImageView>(R.id.search_mag_icon)
                searchIcon.setColorFilter(Color.WHITE)


                val cancelIcon = binding.stockSearch.findViewById<ImageView>(R.id.search_close_btn)
                cancelIcon.setColorFilter(Color.WHITE)

                val textView = binding.stockSearch.findViewById<TextView>(R.id.search_src_text)
                textView.setTextColor(Color.BLACK)

                binding.stockSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return false
                    }

                })

                Log.d("total", "$SearchStockList")
                adapter = SearchStockAdapter(SearchStockList)
                myRecyclerView.adapter = adapter


            }

        return binding.root
    }

}