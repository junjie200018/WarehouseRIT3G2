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
import my.edu.tarc.warehouserit3g2.Data.RackAdapter
import my.edu.tarc.warehouserit3g2.R
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
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_on_rack_display, container, false)
        val db = Firebase.firestore
        rack.clear()

        myRecyclerView = binding.RackRecycleView

        db.collection("Rack").orderBy("Rack ID")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {

                    rack.add(document.id)
                }

                adapter = RackAdapter(rack,this)
                myRecyclerView.adapter = adapter

                binding.rackSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.RackRecycleView


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

        val clickedItem  = rack[position]

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)

        val action : NavDirections = OnRack_Display_FragmentDirections.actionOnRackDisplayFragmentToOnRackProductDisplayFragment(
                clickedItem
            )
        navController.navigate(action)
    }
}