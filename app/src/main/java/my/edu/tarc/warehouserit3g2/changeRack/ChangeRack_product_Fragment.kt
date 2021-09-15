package my.edu.tarc.warehouserit3g2.changeRack

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
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackProductBinding

class changeRack_product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentChangeRackProductBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_change_rack_product, container, false)

        binding.ChangeRackProductScan.setOnClickListener {
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val db = Firebase.firestore

        if (result != null) {

            if (result.contents != null) {
                scannedResult = result.contents
//                binding.textView6.text = scannedResult
//                binding.txtValue2 .text = scannedResult



                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.data == null){
                            Toast.makeText(context, "Invalid QR code. Please try again !!", Toast.LENGTH_LONG).show()
                        }else{

                            if(result.data?.get("Status").toString() != "Scrap" && result.data?.get("Status").toString() != "Transit"){
                                if(result.data?.get("RackID").toString() != ""){

                                    val action : NavDirections = changeRack_product_FragmentDirections.actionChangeRackProductFragmentToChangeRackRackFragment(
                                            scannedResult
                                        )
                                    navController.navigate(action)

                                }else {
                                    Toast.makeText(context, "Product not in rack. Please try again !!", Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toast.makeText(context, "Product already become scrap or already transit", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
//            else {
//                binding.textView6.text = "scan failed"
//            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}