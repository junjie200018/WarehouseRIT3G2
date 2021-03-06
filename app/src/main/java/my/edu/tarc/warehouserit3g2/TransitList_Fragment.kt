package my.edu.tarc.warehouserit3g2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.Transit
import my.edu.tarc.warehouserit3g2.Data.TransitAdapter
import java.util.ArrayList

class TransitList_Fragment : Fragment(), TransitAdapter.OnItemClickListener {

    lateinit var myRecyclerView: RecyclerView
    var transitList: MutableList<Transit> = ArrayList()

    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //declaration
        val view = inflater.inflate(R.layout.fragment_transit_list_, container, false)
        myRecyclerView = view.findViewById(R.id.rvTransit)
        val load: ProgressBar = view.findViewById(R.id.loadingIndi)
        val db = Firebase.firestore

        //=================== get data ===================
        db.collection("Transfer").get()
            .addOnSuccessListener { data ->
                for (item in data) {
                    //if item is in transit status
                    if (item.data.get("status").toString() == "inTransit") {
                        var t = Transit(
                            item.id,
                            item.data.get("from").toString(),
                            item.data.get("to").toString()
                        )
                        //push into arraylist if doesnt exist
                        if (!transitList.contains(t)) {
                            transitList.add(t)
                        }

                    }
                }
                myRecyclerView.adapter = TransitAdapter(transitList, this)
                load.visibility = View.INVISIBLE
            }
        return view
    }

    override fun onItemClick(position: Int) {
        val clickedItem: Transit = transitList[position]
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        val action: NavDirections = TransitList_FragmentDirections.actionTransitListFragmentToMapFragment(clickedItem.id)
        navController.navigate(action)
    }

    override fun onResume() {
        super.onResume()
        transitList.clear()
    }

}