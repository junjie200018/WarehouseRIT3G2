package my.edu.tarc.warehouserit3g2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.edu.tarc.warehouserit3g2.Data.Transit
import my.edu.tarc.warehouserit3g2.Data.TransitAdapter
import java.util.ArrayList

class PickupListFragment : Fragment(), TransitAdapter.OnItemClickListener {

    lateinit var myRecyclerView: RecyclerView
    var transitList: MutableList<Transit> = ArrayList()
    val db = Firebase.firestore

    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //declaration
        val view = inflater.inflate(R.layout.fragment_pickup_list, container, false)
        myRecyclerView = view.findViewById(R.id.rvPickup)
        val load : ProgressBar = view.findViewById(R.id.loadingIndi)


        //=================== get data ===================
        db.collection("Transfer").get()
            .addOnSuccessListener { data ->
                for (item in data) {
                    //if item is in transit status
                    if (item.data.get("status").toString() == "ready") {
                        var t = Transit(
                            item.id,
                            item.data.get("from").toString(),
                            item.data.get("to").toString()
                        )
                        //push into arraylist if doesnt exist
                        if (!transitList.contains(t)){
                            transitList.add(t)
                        }

//                        Log.d(ContentValues.TAG, "t= ${t}")
                    }
                }
//                Log.d(ContentValues.TAG, "list= ${transitList}")
                myRecyclerView.adapter = TransitAdapter(transitList, this)
                load.visibility = View.INVISIBLE
            }
        return view
    }

    override fun onItemClick(position: Int) {

        val clickedItem: Transit = transitList[position]
        GlobalScope.launch(Dispatchers.IO){
            db.collection("Transfer").document(clickedItem.id)
                .update("status", "inTransit")
            withContext(Main){
                val action: NavDirections = PickupListFragmentDirections.actionPickupListFragmentToTrackingFragment(clickedItem.id)
                navController.navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        transitList.clear()
    }
}