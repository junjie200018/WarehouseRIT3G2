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
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackDetailBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class OnRack_Detail_Fragment : Fragment() {

    private lateinit var binding: FragmentOnRackDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_rack_detail, container, false)
        val args = OnRack_Detail_FragmentArgs.fromBundle(requireArguments())
        Log.w(ContentValues.TAG, "partNo 4 = ${args.rackId}")
        Log.w(ContentValues.TAG, "partNo 4 = ${args.serialNo}")

        val db = Firebase.firestore
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val rackId = args.rackId
        val serialNo = args.serialNo
        val rackStatus = "In Rack"
        val rackInDate = sdf.format(Date())

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

                val RackInValue = hashMapOf(
                    "PartNo" to result.data?.get("PartNo").toString(),
                    "ReceivedBy" to result.data?.get("ReceivedBy").toString(),
                    "ReceivedDate" to result.data?.get("ReceivedDate").toString(),
                    "Status" to rackStatus,
                    "quantity" to result.data?.get("quantity").toString(),
                    "serialNo" to result.data?.get("serialNo").toString(),
                    "RackID" to rackId,
                    "RackInDate" to rackInDate,
                    "RackOutDate" to "",
                )

                //db.collection("RackIn").document("").update(RackInValue as Map<String, Any>)
                db.collection("ReceivedProduct").document(serialNo).set(RackInValue)



            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "getfailedwith ", exception)
            }

        binding.BtnOk.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_onRack_Detail_Fragment_to_onRack_Product_Fragment)
        }



        return binding.root
    }


}