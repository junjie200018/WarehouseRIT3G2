package my.edu.tarc.warehouserit3g2.displayRackItem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDisplayDetailBinding


class OnRack_Display_Detail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnRackDisplayDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_on_rack_display_detail, container, false)

        val args = OnRack_Display_Detail_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNo

        // connection of the database
        val db = Firebase.firestore


        //get the receivedProduct from the database
        db.collection("ReceivedProduct").document(serialNo)
            .get()
            .addOnSuccessListener { result ->
                if(result.data != null){

                    // set the value to the textView
                    binding.tvtPN.text = result.data?.get("PartNo").toString()
                    binding.tvtQ.text  = result.data?.get("Quantity").toString()
                    binding.tvtRN.text = result.data?.get("RackID").toString()
                    binding.tvtRND.text= result.data?.get("RackInDate").toString()
                    binding.tvtROD.text= result.data?.get("RackOutDate").toString()
                    binding.tvtRB.text = result.data?.get("ReceivedBy").toString()
                    binding.tvtRD.text = result.data?.get("ReceivedDate").toString()
                    binding.tvtS.text  = result.data?.get("Status").toString()
                    binding.tvtSN.text = result.data?.get("SerialNo").toString()
                }
            }

        // button for go to onRAck Display page
        binding.ButtonOk.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_onRack_Display_Detail_Fragment_to_onRack_Display_Fragment)
        }
      return binding.root
    }
}