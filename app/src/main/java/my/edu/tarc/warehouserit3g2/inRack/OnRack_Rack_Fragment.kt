package my.edu.tarc.warehouserit3g2.inRack

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackRackBinding


class OnRack_Rack_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentOnRackRackBinding
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_on_rack__rack_, container, false)

        binding.RackScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        val args = OnRack_Rack_FragmentArgs.fromBundle(requireArguments())
        val serialNo = args.serialNoOnRack

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents
//                binding.textView6.text = serialNo
//                binding.txtValue1.text = scannedResult

                val valueQRcode : String = scannedResult



                db.collection("Rack").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid Rack QR code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{
//
                            val action : NavDirections = OnRack_Rack_FragmentDirections.actionOnRackRackFragmentToOnRackDetailFragment(
                                    serialNo,
                                    valueQRcode
                                )

                            navController.navigate(action)

                        }
                    }


            }
//            else {
//                binding.textView6.text = "scan failed"
//                Log.w(ContentValues.TAG, "scan failed")
//            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

