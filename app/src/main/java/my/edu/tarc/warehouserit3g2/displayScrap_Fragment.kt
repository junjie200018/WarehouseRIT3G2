package my.edu.tarc.warehouserit3g2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayScrapBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedDetailBinding


class displayScrap_Fragment : Fragment() {

    private lateinit var binding: FragmentDisplayScrapBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_display_scrap, container, false)
        val args = displayScrap_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNo
        val db = Firebase.firestore

        db.collection("ReceivedProduct").document(serialNo)
            .get()
            .addOnSuccessListener { result ->
                if (result.data != null) {
                    binding.tvtPartNo.text = result.data?.get("PartNo").toString()
                    binding.tvtQuantity.text = result.data?.get("Quantity").toString()
                    binding.tvtSerialNo.text = result.data?.get("SerialNo").toString()
                    binding.tvtStatus.text = result.data?.get("Status").toString()
                    binding.tvtReceivedDate.text = result.data?.get("ReceivedDate").toString()
                    binding.tvtReceivedBy.text = result.data?.get("ReceivedBy").toString()
                    binding.tvtRackID.text = result.data?.get("RackID").toString()
                    binding.tvtRackInD.text = result.data?.get("RackInDate").toString()
                    binding.tvtRackOutD.text = result.data?.get("RackOutDate").toString()

                }else{
                    Toast.makeText(context, "Cannot find scrap", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_displayScrap_Fragment_to_scrapList_Fragment)

                }

                binding.btnOk.setOnClickListener {
                    navController.navigate(R.id.action_displayScrap_Fragment_to_scrapList_Fragment)
                }
            }

       return binding.root
    }


}