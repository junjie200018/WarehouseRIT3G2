package my.edu.tarc.warehouserit3g2.currectQty

import android.nfc.Tag
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentCurrentQtyBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockInBinding
import my.edu.tarc.warehouserit3g2.stockInOut.RecProductAdapter
import my.edu.tarc.warehouserit3g2.stockInOut.Stock
import kotlin.concurrent.fixedRateTimer
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


class CurrentQty_Fragment : Fragment() {
    private lateinit var binding: FragmentCurrentQtyBinding
    private lateinit var myRecyclerView : RecyclerView
    private var currentQtyList = ArrayList<CurrentQty>()
    private var currentTotalQtyList = ArrayList<CurrentQty>()
    private lateinit var PartNo :Array<String?>
    private var total : Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_qty_, container, false)

        //connect firebase
        val db = Firebase.firestore
        db.collection("ReceivedProduct")
            .orderBy("PartNo")
            .get() //retrieve data
            .addOnSuccessListener { result ->
                var count = 0

                for (currentQty in result) {
                    //filter Stock In
                    if (currentQty.data?.get("Status") == "In Rack" || currentQty.data?.get("Status") == "Received") {
                        val curQty = CurrentQty (
                            currentQty.data?.get("PartNo").toString(),
                            currentQty.data?.get("Quantity").toString(),
                            0.00,
                            "-"
                        )
                        count++
                        total += currentQty.data?.get("Quantity").toString().toInt()
                        currentQtyList.add(curQty)
                    }
                    PartNo = arrayOfNulls(count)
                    for((x,cql) in currentQtyList.withIndex()){
                        PartNo[x] = cql.PartNo
                    }
                }
                var partNo = PartNo.distinct() // remove duplicate
                db.collection("StockQuantity")
                    .orderBy("PartNo")
                    .get()
                    .addOnSuccessListener { result ->
                        for(p in partNo) {
                            var qty = 0
                            var progress = 0.00
                            var belowMin = "no"
                            var count = 0
                            for(cq in currentQtyList) {
                                if(p == cq.PartNo) {
                                    qty += cq.Qty.toInt()
                                }
                            }
                            for(min in result) {
                                if(p == min.data?.get("PartNo").toString()) {
                                    //check minimum quantity
                                    if(qty < min.data?.get("MinQty").toString().toInt()) {
                                        belowMin = "yes"
                                    }

                                }else {
                                    count++
                                }
                                //add new product to minimum quantity list
                                if(count == result.size()) {
                                    val stockMin = hashMapOf(
                                        "MinQty" to 0,
                                        "PartNo" to p.toString()

                                    )
                                    belowMin = "null"
                                    db.collection("StockQuantity").document(p.toString()).set(stockMin)

                                }
                                if(p == min.data?.get("PartNo").toString() && min.data?.get("MinQty").toString().toInt() == 0) {
                                    belowMin = "null"
                                }
                            }
                            progress = ((qty.toDouble() / total.toDouble()) * 100)
                            var Etotal = CurrentQty (
                                p.toString(),
                                qty.toString(),
                                progress,
                                belowMin
                            )
                            currentTotalQtyList.add(Etotal)
                        }

                        val activity = context as FragmentActivity
                        val fm: FragmentManager = activity.supportFragmentManager

                        myRecyclerView = binding.currentQtyRecyclerView
                        myRecyclerView.adapter = CurrentQtyAdapter(currentTotalQtyList, fm)
                        myRecyclerView.setHasFixedSize(true)
                    }
            }
        return binding.root
    }

}