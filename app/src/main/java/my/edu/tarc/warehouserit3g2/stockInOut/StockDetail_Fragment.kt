package my.edu.tarc.warehouserit3g2.stockInOut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockDetailBinding

class StockDetail_Fragment : Fragment() {
    private lateinit var binding: FragmentStockDetailBinding
    private lateinit var recProduct : Stock

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_detail_, container, false)

        val args = StockDetail_FragmentArgs.fromBundle(requireArguments())
        val db = Firebase.firestore

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

                binding.stockDetail = recProduct

                if(recProduct.RackId != ""){
                    binding.rackid.isVisible = true
                }
                if (recProduct.RackInDate != "") {
                    binding.rackindate.isVisible = true
                }
                if (recProduct.RackOutDate != "") {
                    binding.rackoutdate.isVisible = true
                }
            }




        return binding.root
    }

}