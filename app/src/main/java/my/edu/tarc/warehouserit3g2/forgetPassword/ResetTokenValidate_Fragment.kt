package my.edu.tarc.warehouserit3g2.forgetPassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentResetTokenValidateBinding
import my.edu.tarc.warehouserit3g2.stockInOut.StockDetail_FragmentArgs


class ResetTokenValidate_Fragment : Fragment() {
    private lateinit var binding : FragmentResetTokenValidateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_token_validate_, container, false)

        //get safe args
        val args = ResetTokenValidate_FragmentArgs.fromBundle(requireArguments())

        //connect firebase
        val db = Firebase.firestore

        binding.btnfgtoken.setOnClickListener {

            //validate
           if(binding.fgtoken.text.toString().isEmpty() || binding.fgtoken.text.toString().isBlank()) {
               binding.fgtokenLayout.error = "This field is required !"
               binding.fgtokenLayout.requestFocus()

           } else {
               binding.fgtokenLayout.isErrorEnabled = false

               db.collection("Employees").document(args.username)
                   .get()
                   .addOnSuccessListener { result ->
                       //check reset token
                       if(binding.fgtoken.text.toString() == result.data?.get("resetToken").toString()) {
                           Toast.makeText(
                               context,
                               "Token validate successful",
                               Toast.LENGTH_LONG
                           ).show()
                           val action = ResetTokenValidate_FragmentDirections.actionResetTokenValidateFragmentToResetPasswordFragment(args.username)
                           Navigation.findNavController(it).navigate(action)
                       } else {
                           Toast.makeText(
                               context,
                               "Token not correct, please check your email again",
                               Toast.LENGTH_LONG
                           ).show()
                       }
                   }
           }

        }
        return binding.root
    }

}