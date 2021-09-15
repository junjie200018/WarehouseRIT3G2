package my.edu.tarc.warehouserit3g2.forgetPassword

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import my.edu.tarc.warehouserit3g2.MainActivity
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
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

        //connect firebase
        val db = Firebase.firestore

        //get safe args
        var args = ResetTokenValidate_FragmentArgs.fromBundle(requireArguments())

        setupListener()

        binding.btnResetPassword.setOnClickListener {
            var newPassword = binding.fgnewPassword.text.toString()
            var retypePassword = binding.fgretypePassword.text.toString()

            //validate
            if (isValidate()) {
                if (newPassword.equals(retypePassword)) {
                    val passHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

                    //update reset password
                    db.collection("Employees").document(args.username)
                        .update(
                            mapOf(
                                "password" to passHash,
                                "resetToken" to 0
                            )
                        )
                    Toast.makeText(
                        context,
                        "Change password completed",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        context,
                        "Retype Password not Correct. Please try again !!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        return binding.root
    }

    private fun setupListener() {

        binding.fgnewPassword.addTextChangedListener(TextFieldValidation(binding.fgnewPassword))
        binding.fgretypePassword.addTextChangedListener(TextFieldValidation(binding.fgretypePassword))
    }

    //real time checking
    inner class TextFieldValidation(private val view: View) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.fgnewPassword -> {
                    newcheckPass()
                }
                R.id.fgretypePassword -> {
                    retypecheckPass()
                }
            }
        }
    }

    private fun isValidate(): Boolean = newcheckPass() && retypecheckPass()

    private fun newcheckPass ():Boolean{
        var truefalse = true
        //password require format
        var REG = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=.,;:'|*_!`~])(?=\\S+$).{6,20}$"
        if(!Pattern.compile(REG).matcher(binding.fgnewPassword.text.toString()).matches()){
            binding.fgnewPasswordLayout.error = "Retype Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol"
            binding.fgnewPassword.requestFocus()
            truefalse = false
        }else if (binding.fgnewPassword.text.toString().isEmpty()){
            binding.fgnewPasswordLayout.error = "Required Field!"
            binding.fgnewPassword.requestFocus()
            truefalse = false
        }else{
            binding.fgnewPasswordLayout.isErrorEnabled = false
        }
        return truefalse
    }

    private fun retypecheckPass ():Boolean{
        var truefalse = true
        //password require format
        var REG = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=.,;:'|*_!`~])(?=\\S+$).{6,20}$"
        if(!Pattern.compile(REG).matcher(binding.fgretypePassword.text.toString()).matches()){
            binding.fgretypePasswordLayout.error = "New Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol"
            binding.fgretypePassword.requestFocus()
            truefalse = false
        }else if(binding.fgretypePassword.text.toString().isEmpty()){
            binding.fgretypePasswordLayout.error = "Required Field!"
            binding.fgretypePassword.requestFocus()
            truefalse = false
        }else{
            binding.fgretypePasswordLayout.isErrorEnabled = false
        }

        return truefalse
    }

}