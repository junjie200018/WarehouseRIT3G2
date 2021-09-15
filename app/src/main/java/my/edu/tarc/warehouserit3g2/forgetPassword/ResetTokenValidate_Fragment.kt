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
import my.edu.tarc.warehouserit3g2.databinding.FragmentForgetPasswordEmailBinding
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

        val args = ResetTokenValidate_FragmentArgs.fromBundle(requireArguments())
        val db = Firebase.firestore

        db.collection("Employees").document(args.username)
            .get()
            .addOnSuccessListener { result ->
                if(binding.fgtoken.text.toString() == result.data?.get("resetToken")) {
//                    val action = ResetTokenValidate_FragmentDirections.actionResetTokenValidateFragmentToResetPasswordFragment(args.username)
//                    Navigation.findNavController(it).navigate(action)

                    Toast.makeText(
                        context,
                        "Token validate Successful",
                        Toast.LENGTH_LONG
                    ).show()
                }else {
                    Toast.makeText(
                        context,
                        "Reset token not match, please check again ",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        return binding.root
    }

}