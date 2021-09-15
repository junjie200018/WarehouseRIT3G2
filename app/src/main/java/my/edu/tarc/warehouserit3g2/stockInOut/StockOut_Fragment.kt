package my.edu.tarc.warehouserit3g2.stockInOut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockOutBinding

class StockOut_Fragment : Fragment() {

    private lateinit var binding: FragmentStockOutBinding
    private lateinit var myRecyclerView : RecyclerView
    private var SendProductList = ArrayList<Stock>()
    private val sortBy = arrayOf("Part No", "Reason")
    private var select: String = "Part No"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_out_, container, false)
        //spinner
        var arrayAdapter = ArrayAdapter(this.requireContext(),
            R.layout.support_simple_spinner_dropdown_item, sortBy)
        binding.sortBySpinner2.adapter = arrayAdapter

        SendProductList.clear()

        //connect firebase
        val db = Firebase.firestore
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("ReceivedProduct")
                .orderBy("PartNo")
                .get()
                .addOnSuccessListener { result ->
                    for (sendPro in result) {
                        //show product out of store
                        if (sendPro.data?.get("Status") != "In Rack" && sendPro.data?.get("Status") != "Received") {
                            val recP = Stock(
                                sendPro.data?.get("PartNo").toString(),
                                sendPro.data?.get("ReceivedBy").toString(),
                                sendPro.data?.get("ReceivedDate").toString(),
                                sendPro.data?.get("Quantity").toString(),
                                sendPro.data?.get("SerialNo").toString(),
                                sendPro.data?.get("Status").toString(),
                                sendPro.data?.get("RackID").toString(),
                                sendPro.data?.get("RackInDate").toString(),
                                sendPro.data?.get("RackOutDate").toString(),
                            )
                            SendProductList.add(recP)
                        }
                    }
                    myRecyclerView = binding.stockOutRecyclerView
                    myRecyclerView.adapter = SendProductAdapter(SendProductList)
                    myRecyclerView.setHasFixedSize(true)

                }

            //sort by
            CoroutineScope(Dispatchers.Main).launch {
                binding.sortBySpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        select = p0?.getItemAtPosition(p2).toString()
                        binding.descending2.isVisible = false
                        binding.descending2.isClickable = false

                        binding.ascending2.isVisible = true
                        binding.ascending2.isClickable = true

                        if(select == "Part No") {
                            SendProductList.sortBy { it.PartNo }

                        } else if (select == "Reason") {
                            SendProductList.sortBy { it.Status }

                        }

                        myRecyclerView = binding.stockOutRecyclerView
                        myRecyclerView.adapter = SendProductAdapter(SendProductList)
                        myRecyclerView.setHasFixedSize(true)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }

                //sort ascending
                binding.ascending2.setOnClickListener() {
                    binding.descending2.isVisible = true
                    binding.descending2.isClickable = true

                    binding.ascending2.isVisible = false
                    binding.ascending2.isClickable = false

                    if(select == "Part No") {
                        SendProductList.sortByDescending { it.PartNo }

                    } else if (select == "Reason") {
                        SendProductList.sortByDescending { it.Status}

                    }

                    myRecyclerView = binding.stockOutRecyclerView
                    myRecyclerView.adapter = SendProductAdapter(SendProductList)
                    myRecyclerView.setHasFixedSize(true)
                }

                //sort descending
                binding.descending2.setOnClickListener() {
                    binding.descending2.isVisible = false
                    binding.descending2.isClickable = false

                    binding.ascending2.isVisible = true
                    binding.ascending2.isVisible = true
                    binding.ascending2.isClickable = true

                    if(select == "Part No") {
                        SendProductList.sortBy { it.PartNo }

                    } else if (select == "Reason") {
                        SendProductList.sortBy { it.Status }

                    }

                    myRecyclerView = binding.stockOutRecyclerView
                    myRecyclerView.adapter = SendProductAdapter(SendProductList)
                    myRecyclerView.setHasFixedSize(true)
                }

            }
        }

        return binding.root
    }

}