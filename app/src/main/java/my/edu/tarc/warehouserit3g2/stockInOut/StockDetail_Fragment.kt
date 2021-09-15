package my.edu.tarc.warehouserit3g2.stockInOut

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockDetailBinding
import my.edu.tarc.warehouserit3g2.person.EmployeeProfile_Fragment

class StockDetail_Fragment : Fragment() {
    private lateinit var binding: FragmentStockDetailBinding
    private lateinit var recProduct : Stock
    private lateinit var person: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_detail_, container, false)

        //get safe args
        val args = StockDetail_FragmentArgs.fromBundle(requireArguments())

        //connect firebase
        val db = Firebase.firestore

        //get view model
        person = ViewModel.getInstance()

        //display product detail
        db.collection("ReceivedProduct").document(args.serialNo)
            .get()
            .addOnSuccessListener { recPro ->
                recProduct = Stock(
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

                person.setfullName(recProduct.RecBy)
                Log.d("full", "${recProduct.RecBy}")
                binding.stockDetail = recProduct

                //check empty, if not will show
                if(recProduct.RackId != "-"){
                    binding.rackid.isVisible = true
                }
                if (recProduct.RackInDate != "-") {
                    binding.rackindate.isVisible = true
                }
                if (recProduct.RackOutDate != "-") {
                    binding.rackoutdate.isVisible = true
                }

                //check item status, if transit then show from and to location
                if (recProduct.Status == "Transit") {
                    Log.d("knn", "${recProduct.Status}")
                    db.collection("Transfer")
                        .whereEqualTo("serialNo", recProduct.SerialNo)
                        .get()
                        .addOnSuccessListener { result ->
                            for (re in result) {
                                binding.tvto.text = re.data?.get("to").toString()
                                binding.tvfrom.text = re.data?.get("from").toString()
                            }
                        }
                    binding.from.isVisible = true
                    binding.to.isVisible = true
                }
            }

        //open dialog
        binding.tvrecBy.setOnClickListener() {

            var dialog = EmployeeProfile_Fragment()
            dialog.show(parentFragmentManager,"personalInfo")
        }

        return binding.root
    }

}