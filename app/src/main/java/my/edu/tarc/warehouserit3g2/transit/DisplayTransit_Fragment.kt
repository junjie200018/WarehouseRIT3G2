package my.edu.tarc.warehouserit3g2.transit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.DisplayTransit
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayTransitBinding

class DisplayTransit_Fragment : Fragment() {

    private lateinit var binding: FragmentDisplayTransitBinding
    var transitProduct: MutableList<DisplayTransit> = ArrayList()
    lateinit var adapter: DisplayTransitAdapter
    lateinit var myRecyclerView: RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_display_transit, container, false)

        //connect to database
        val db = Firebase.firestore

        //clear the mutableList
        transitProduct.clear()
        myRecyclerView = binding.TransitRecycleView

        //get the transfer detail from database
        db.collection("Transfer")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //check the status of the tarnsfer detail
                    if (document.data?.get("status").toString() == "pending") {
                        val p = DisplayTransit(
                            "${document.data.get("partNo").toString()}",
                            "${document.data.get("quantity").toString()}",
                            "${document.data.get("from").toString()}",
                            "${document.data.get("to").toString()}"
                        )
                        transitProduct.add(p)
                    }
                }

                // sort the detail in te list
                transitProduct.sortBy { it.partNo }

                adapter = DisplayTransitAdapter(transitProduct, this)
                myRecyclerView.adapter = adapter

                // connect to the search search function
                binding.TransitSearchView.setOnQueryTextListener(object :
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
}