package my.edu.tarc.warehouserit3g2.inRack

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
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class OnRack_Detail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnRackDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_rack_detail, container, false)

        // get the data from the previous page
        val args = OnRack_Detail_FragmentArgs.fromBundle(requireArguments())

        // connect to the database
        val db = Firebase.firestore
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val rackId = args.rackId
        val serialNo = args.serialNo
        val rackStatus = "In Rack"
        val rackInDate = sdf.format(Date())

        //get received product detail
        db.collection("ReceivedProduct").document(serialNo)
            .get()
            .addOnSuccessListener { result ->
                val partNo = result.data?.get("PartNo").toString()

                binding.tvtPartN.text = partNo
                binding.tvtRackId.text = rackId
                binding.tvtRackInDate.text = rackInDate
                binding.RackStatus.text = rackStatus
                binding.tvtSerialN.text = serialNo
                binding.tvtRackOutDate.text = "-"

                // update the received product database
                db.collection("ReceivedProduct").document(serialNo)
                    .update(
                        mapOf(
                            "RackID" to rackId,
                            "RackInDate" to rackInDate,
                            "Status" to rackStatus
                        )
                    )

            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "getfailedwith ", exception)
            }

        // button for move to onRack Product page
        binding.BtnOk.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_onRack_Detail_Fragment_to_onRack_Product_Fragment)
        }

        return binding.root
    }


}