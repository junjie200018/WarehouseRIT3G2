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
import my.edu.tarc.warehouserit3g2.Data.RackAdapter
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDisplayBinding


class OnRack_Display_Fragment : Fragment(), RackAdapter.OnItemClickListener {


    private lateinit var binding: FragmentOnRackDisplayBinding
    var rack = ArrayList<String>()
    lateinit var adapter: RackAdapter
    lateinit var myRecyclerView : RecyclerView
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_rack_display, container, false)
        val db = Firebase.firestore
        rack.clear()

        myRecyclerView = binding.RackRecycleView

        db.collection("Rack")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    rack.add(document.id)
                }

                adapter = RackAdapter(rack,this)
                myRecyclerView.adapter = adapter

                binding.rackSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.RackRecycleView


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
        val clickedItem  = rack[position]
        Log.d(ContentValues.TAG, "DocumentSnapshot qty data: ${clickedItem}")
        val action : NavDirections = OnRack_Display_FragmentDirections.actionOnRackDisplayFragmentToOnRackProductDisplayFragment(clickedItem)
        navController.navigate(action)
//        ProductAdapter.notifyItemChanged(position)
    }


}