package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnRackProductBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentOnReceivedBinding


class OnRack_Product_Fragment : Fragment() {

    var scannedResult: String = ""
    private lateinit var binding: FragmentOnRackProductBinding
    private val navController by lazy { NavHostFragment.findNavController(this)}
    private  var checkExist = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_on_rack__product_, container, false)

        binding.RackProductScan.setOnClickListener{
            run {
                val intentIntegrator = IntentIntegrator.forSupportFragment(this)
                intentIntegrator.initiateScan()
            }
        }

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val db = Firebase.firestore

        if (result != null) {
            Log.w(ContentValues.TAG, "partNo 2 ")
            if (result.contents != null) {
                scannedResult = result.contents
                binding.textView6.text = scannedResult
                binding.txtValue1.text = scannedResult
                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                val valueBarcode: String = scannedResult



                db.collection("ReceivedProduct").document(scannedResult)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.data == null) {
                            Toast.makeText(
                                context,
                                "Invalid product QR code. Please try again !!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {

                            if(documents.data?.get("Status").toString() != "scrap"){
                                db.collection("ReceivedProduct")
                                    .get()
                                    .addOnSuccessListener { result ->

                                        for(document in result){
                                            if(document.data.get("RackID").toString() != ""){
                                                checkExist = 1
                                                Log.w(ContentValues.TAG, "exist")
                                                break
                                            }
                                        }

                                        if(checkExist == 0){
                                            val action : NavDirections = OnRack_Product_FragmentDirections.actionOnRackProductFragmentToOnRackRackFragment(scannedResult)
                                            navController.navigate(action)

                                        }else{
                                            Log.w(ContentValues.TAG, "quantity 3 = ${checkExist}")
                                            Toast.makeText(
                                                context,
                                                "The Product already exist in a rack. Please try again!!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            checkExist = 0
                                        }
                                    }



                                Log.w(ContentValues.TAG, "partNo 2 = ${scannedResult}")
                            }else{
                                Toast.makeText(
                                    context,
                                    "The Product already become scrap. Please try again!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }



                        }
                    }
            } else {
                binding.textView6.text = "scan failed"
                Log.w(ContentValues.TAG, "scan failed")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}