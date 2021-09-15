package my.edu.tarc.warehouserit3g2.forgetPassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentForgetPasswordEmailBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentResetPasswordBinding
import java.util.regex.Pattern

class ResetPassword_Fragment : Fragment() {
    private lateinit var binding : FragmentResetPasswordBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password_, container, false)
        val db = Firebase.firestore

        binding.btnResetPasword.setOnClickListener {

            var newPassword = binding.fgnewPassword.text.toString()
            var retypePassword = binding.fgretypePassword.text.toString()

//user input
//                    val passHash = BCrypt.withDefaults().hashToString(12, "12345".toCharArray())


//            if(newPassword.equals(retypePassword)){
//                val passHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
//
//                db.collection("Employees").document(userName)
//                    .update(
//                        mapOf(
//                            "password" to passHash
//                        )
//                    )
//                Toast.makeText(
//                    context,
//                    "Change password completed",
//                    Toast.LENGTH_LONG
//                ).show()
//
//                binding.oldPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
//                aPerson.password = passHash
//                Person.setaPerson(aPerson)
//
//                if(Person.getPerson().role == "worker") {
//                    navController.navigate(R.id.action_changePass_Fragment_to_profileEdit_Fragment)
//                } else if(Person.getPerson().role == "manager") {
//                    navController.navigate(R.id.action_changePass_Fragment2_to_profileEdit_Fragment2)
//                }
//            }else{
//                    Toast.makeText(
//                        context,
//                        "Retype Password not Correct. Please try again !!",
//                        Toast.LENGTH_LONG
//                    ).show()
//            }

        }

        return binding.root
    }

    private fun setupListener() {

//        binding.newPassword.addTextChangedListener(TextFieldValidation(binding.newPassword))
//        binding.retypePassword.addTextChangedListener(TextFieldValidation(binding.retypePassword))

    }

//    inner class TextFieldValidation(private val view: View) : TextWatcher {
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//        override fun afterTextChanged(s: Editable?) {}
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            when (view.id) {
//
//                R.id.newPassword -> {
//                    newcheckPass()
//                }
//                R.id.retypePassword -> {
//                    retypecheckPass()
//                }
//            }
//        }
//    }
//
//    private fun isValidate(): Boolean =
//        newcheckPass() && retypecheckPass()
//
//    private fun newcheckPass ():Boolean{
//
//        var truefalse = true
//        var REG = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=.,;:'|*_!`~])(?=\\S+$).{6,20}$"
//        if(!Pattern.compile(REG).matcher(binding.newPassword.text.toString()).matches()){
//            binding.newPasswordLayout.error = "Retype Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol"
//            binding.newPassword.requestFocus()
//            truefalse = false
//        }else if (binding.newPassword.text.toString().trim().isEmpty()){
//            binding.newPasswordLayout.error = "Required Field!"
//            binding.newPassword.requestFocus()
//            truefalse = false
//        }else{
//            binding.newPasswordLayout.isErrorEnabled = false
//        }
//        return truefalse
//    }
//
//    private fun retypecheckPass ():Boolean{
//
//        var truefalse = true
//        var REG = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=.,;:'|*_!`~])(?=\\S+$).{6,20}$"
//        if(!Pattern.compile(REG).matcher(binding.retypePassword.text.toString()).matches()){
//            binding.retypePasswordLayout.error = "New Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol"
//            binding.retypePassword.requestFocus()
//            truefalse = false
//        }else if(binding.retypePassword.text.toString().trim().isEmpty()){
//            binding.retypePasswordLayout.error = "Required Field!"
//            binding.retypePassword.requestFocus()
//            truefalse = false
//        }else{
//            binding.retypePasswordLayout.isErrorEnabled = false
//        }
//
//        return truefalse
//    }

}