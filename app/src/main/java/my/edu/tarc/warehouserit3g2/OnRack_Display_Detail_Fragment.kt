package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.databinding.FragmentDisplayReceivedItemBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDetailBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDisplayDetailBinding


class OnRack_Display_Detail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnRackDisplayDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_rack_display_detail, container, false)

        val args = OnRack_Display_Detail_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNo
        val db = Firebase.firestore

        Log.d(ContentValues.TAG, "DocumentSnapshot result data: ${serialNo}")

        db.collection("ReceivedProduct").document(serialNo)
            .get()
            .addOnSuccessListener { result ->
                if(result.data != null){
                    Log.d(ContentValues.TAG, "DocumentSnapshot result data: ${result.data?.get("PartNo").toString()}")


                    var InDate = result.data?.get("RackInDate").toString()
                    var OutDate = result.data?.get("RackOutDate").toString()
                    if(InDate == ""){
                        InDate = "-"
                    }
                    if(OutDate == ""){
                        OutDate = "-"
                    }
                    binding.tvtPN.text = result.data?.get("PartNo").toString()
                    binding.tvtQ.text  = result.data?.get("quantity").toString()
                    binding.tvtRN.text = result.data?.get("RackID").toString()
                    binding.tvtRND.text= InDate
                    binding.tvtROD.text= OutDate
                    binding.tvtRB.text = result.data?.get("ReceivedBy").toString()
                    binding.tvtRD.text = result.data?.get("ReceivedDate").toString()
                    binding.tvtS.text  = result.data?.get("Status").toString()
                    binding.tvtSN.text = result.data?.get("serialNo").toString()
                }
            }

        binding.ButtonOk.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_onRack_Display_Detail_Fragment_to_onRack_Display_Fragment)
        }




        // Inflate the layout for this fragment
      return binding.root
    }
}