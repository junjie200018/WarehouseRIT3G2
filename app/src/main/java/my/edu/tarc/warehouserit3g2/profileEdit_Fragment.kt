package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.person.Person
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackRackBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentProfileEditBinding
import java.util.regex.Pattern


class profileEdit_Fragment : Fragment() {


    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var Person: PersonViewModel
    private  var duplicate = 0
    private var duplicatePhone = 0
    private val navController by lazy { NavHostFragment.findNavController(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val db = Firebase.firestore

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false)
        binding.EditUsername.isEnabled = false
        binding.Name.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false

        Person = PersonViewModel.getInstance()
        binding.EditUsername.setText(Person.getPerson().username)
        binding.Name.setText(Person.getPerson().fullName)
        binding.email.setText(Person.getPerson().email)
        binding.phone.setText(Person.getPerson().phoneNo)

        binding.btnProfileEdit.setOnClickListener {
            Log.w(ContentValues.TAG, "partNo 2 ")

            binding.Name.setEnabled(true)
            binding.email.setEnabled(true)
            binding.phone.setEnabled(true)

            binding.btnProfileEdit.setVisibility(INVISIBLE)
            binding.btnProfileSubmit.setVisibility(VISIBLE)

        }

        setupListener()

        binding.btnChangePass.setOnClickListener {
            if(Person.getPerson().role == "worker") {
                navController.navigate(R.id.action_profileEdit_Fragment_to_changePass_Fragment)
            } else if(Person.getPerson().role == "manager") {
                navController.navigate(R.id.action_profileEdit_Fragment2_to_changePass_Fragment2)
            }
        }

        binding.btnProfileSubmit.setOnClickListener {
            var fullname = binding.Name.text.toString()
            var email = binding.email.text.toString()
            var phone = binding.phone.text.toString()

            if(isValidate()){
                db.collection("Employees").document(binding.EditUsername.text.toString())
                    .update(
                        mapOf(
                           "fullName" to binding.Name.text.toString(),
                            "phoneNo" to binding.phone.text.toString(),
                            "email" to binding.email.text.toString()
                        )
                    )
                binding.EditUsername.setEnabled(false)
                binding.Name.setEnabled(false)
                binding.email.setEnabled(false)
                binding.phone.setEnabled(false)

                binding.btnProfileEdit.setVisibility(VISIBLE)
                binding.btnProfileSubmit.setVisibility(INVISIBLE)
                binding.Name.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.email.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.phone.onEditorAction(EditorInfo.IME_ACTION_DONE)
                Toast.makeText(
                    context,
                    "Edit successful",
                    Toast.LENGTH_LONG
                ).show()


            }else{
                Toast.makeText(
                    context,
                    "Edit Unsuccessful. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }


            }

       return binding.root
    }
    private fun setupListener() {
        binding.Name .addTextChangedListener(TextFieldValidation(binding.Name))
        binding.email.addTextChangedListener(TextFieldValidation(binding.email))
        binding.phone.addTextChangedListener(TextFieldValidation(binding.phone))

    }

    private fun isValidate(): Boolean =
        validateFullName() && validateEmail() && validatePhoneNo()


    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.Name -> {
                    validateFullName()
                }
                R.id.email -> {
                    validateEmail()
                }
                R.id.phone -> {
                    validatePhoneNo()
                }
            }
        }
    }


    private fun validateFullName(): Boolean {



        if (binding.Name.text.toString().trim().isEmpty()) {
            binding.nameLayout.error = "Required Field!"
            binding.Name.requestFocus()
            return false
        } else {
            binding.nameLayout.isErrorEnabled = false
        }

        return true
    }

    private fun validateEmail(): Boolean {
        val db = Firebase.firestore

        var trueFalse = true
        duplicate = 0



        db.collection("Employees").whereNotEqualTo("username", binding.EditUsername.text.toString())
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    if(binding.email.text?.contains(document.data.get("email").toString()) == true){
                        duplicate = 1
                    }
                }

                if (binding.email.text.toString().trim().isEmpty()) {
                    binding.emailLayout.error = "Required Field!"
                    binding.email.requestFocus()
                    trueFalse = false
                } else if (!validEmail(binding.email.text.toString())) {
                    binding.emailLayout.error = "Invalid Email! e.g abc@xxx.com"
                    binding.email.requestFocus()
                    trueFalse = false
                } else {
                    binding.emailLayout.isErrorEnabled = false
                }

                if(duplicate == 1){
                    binding.emailLayout.error = "Duplicate Field!"
                    binding.email.requestFocus()
                    trueFalse = false
                }
            }

        if(trueFalse == false){
            return false
        }
        return true
    }

    private fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun validatePhoneNo(): Boolean {
        val REG = "(01)[0-9]-[0-9]{7,8}"
        var trueFalse = true
        val db = Firebase.firestore
        duplicatePhone = 0

        db.collection("Employees").whereNotEqualTo("username", binding.EditUsername.text.toString())
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (binding.phone.text?.contains(document.data.get("phoneNo").toString()) == true) {
                        duplicatePhone = 1
                        Log.w(ContentValues.TAG, "chackemail")
                    }
                }

                if (binding.phone.text.toString().trim().isEmpty()) {
                    binding.phoneLayout.error = "Required Field!"
                    binding.phone.requestFocus()
                    trueFalse = false
                } else if (!Pattern.compile(REG).matcher(binding.phone.text.toString()).matches()) {
                    binding.phoneLayout.error = "Invalid Phone Number! e.g 01X-XXXXXXXX"
                    binding.phone.requestFocus()
                    trueFalse = false
                } else {
                    binding.phoneLayout.isErrorEnabled = false
                }

                if(duplicatePhone == 1){
                    binding.phoneLayout.error = "Duplicate Field!"
                    binding.phone.requestFocus()
                    trueFalse = false
                }
            }
        if(trueFalse == false){
            return false
        }
        return true
    }



}