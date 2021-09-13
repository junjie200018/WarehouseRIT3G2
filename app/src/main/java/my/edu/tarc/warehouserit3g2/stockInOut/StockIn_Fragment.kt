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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockInBinding


class StockIn_Fragment : Fragment() {

    private lateinit var binding: FragmentStockInBinding
    private lateinit var myRecyclerView : RecyclerView
    private var RecProductList = ArrayList<ReceivedProduct>()
    private val sortBy = arrayOf("Part No", "Received By", "Received Date")
    private var select: String = "Part No"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_in_, container, false)
        var arrayAdapter = ArrayAdapter(this.requireContext(),
            R.layout.support_simple_spinner_dropdown_item, sortBy)
        binding.sortBySpinner.adapter = arrayAdapter



        val db = Firebase.firestore
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("ReceivedProduct")
                .orderBy("PartNo")
                .get()
                .addOnSuccessListener { result ->
                    for (recPro in result) {
                        if (recPro.data?.get("Status") == "In Rack" || recPro.data?.get("Status") == "Received") {
                            val recP = ReceivedProduct(
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
                            RecProductList.add(recP)
                        }
                    }
                    myRecyclerView = binding.stockInRecyclerView
                    myRecyclerView.adapter = RecProductAdapter(RecProductList)
                    myRecyclerView.setHasFixedSize(true)

                }

            CoroutineScope(Main).launch {
                binding.sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        select = p0?.getItemAtPosition(p2).toString()
                        binding.descending.isVisible = false
                        binding.descending.isClickable = false

                        binding.ascending.isVisible = true
                        binding.ascending.isClickable = true

                        if(select == "Part No") {
                            RecProductList.sortBy { it.PartNo }

                        } else if (select == "Received By") {
                            RecProductList.sortBy { it.RecBy }

                        } else if (select == "Received Date") {
                            RecProductList.sortBy { it.RecDate}

                        }


                        myRecyclerView = binding.stockInRecyclerView
                        myRecyclerView.adapter = RecProductAdapter(RecProductList)
                        myRecyclerView.setHasFixedSize(true)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }

                binding.ascending.setOnClickListener() {
                    binding.descending.isVisible = true
                    binding.descending.isClickable = true

                    binding.ascending.isVisible = false
                    binding.ascending.isClickable = false

                    if(select == "Part No") {
                        RecProductList.sortByDescending { it.PartNo }

                    } else if (select == "Received By") {
                        RecProductList.sortByDescending { it.RecBy }

                    } else if (select == "Received Date") {
                        RecProductList.sortByDescending { it.RecDate}

                    }
                    myRecyclerView = binding.stockInRecyclerView
                    myRecyclerView.adapter = RecProductAdapter(RecProductList)
                    myRecyclerView.setHasFixedSize(true)
                }

                binding.descending.setOnClickListener() {
                    binding.descending.isVisible = false
                    binding.descending.isClickable = false

                    binding.ascending.isVisible = true
                    binding.ascending.isClickable = true

                    if(select == "Part No") {
                        RecProductList.sortBy { it.PartNo }

                    } else if (select == "Received By") {
                        RecProductList.sortBy { it.RecBy }

                    } else if (select == "Received Date") {
                        RecProductList.sortBy { it.RecDate}

                    }
                    myRecyclerView = binding.stockInRecyclerView
                    myRecyclerView.adapter = RecProductAdapter(RecProductList)
                    myRecyclerView.setHasFixedSize(true)
                }

            }
        }

        return binding.root
    }
}