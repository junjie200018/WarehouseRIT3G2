package my.edu.tarc.warehouserit3g2.person

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
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentProfileEditBinding
import java.util.*
import java.util.regex.Pattern


class profileEdit_Fragment : Fragment() {


    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var person: ViewModel
    private lateinit var aPerson: Person
    private  var duplicate = 0
    private var duplicatePhone = 0
    private val navController by lazy { NavHostFragment.findNavController(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //connect database
        val db = Firebase.firestore

        //binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false)
        binding.EditUsername.isEnabled = false
        binding.Name.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false

        // view model
        person = ViewModel.getInstance()
        aPerson = person.getPerson()
        binding.EditUsername.setText(person.getPerson().username)
        binding.Name.setText(person.getPerson().fullName)
        binding.email.setText(person.getPerson().email)
        binding.phone.setText(person.getPerson().phoneNo)
        var oldName = person.getPerson().fullName

        //button for allow to edit
        binding.btnProfileEdit.setOnClickListener {
            Log.w(ContentValues.TAG, "partNo 2 ")

            binding.Name.setEnabled(true)
            binding.email.setEnabled(true)
            binding.phone.setEnabled(true)

            binding.btnProfileEdit.setVisibility(INVISIBLE)
            binding.btnProfileSubmit.setVisibility(VISIBLE)

        }

        // realtime checking input field
        setupListener()

        //button for change password
        binding.btnChangePass.setOnClickListener {
            if(person.getPerson().role == "worker") {
                navController.navigate(R.id.action_profileEdit_Fragment_to_changePass_Fragment)
            } else if(person.getPerson().role == "manager") {
                navController.navigate(R.id.action_profileEdit_Fragment2_to_changePass_Fragment2)
            } else if(person.getPerson().role == "driver") {
                navController.navigate(R.id.action_profileEdit_Fragment3_to_changePass_Fragment3)
            }
        }

        // button for edit profile
        binding.btnProfileSubmit.setOnClickListener {
            var fullname = binding.Name.text.toString()
            var email = binding.email.text.toString().lowercase(Locale.getDefault())
            var phone = binding.phone.text.toString()

            // check input field valid or not valid
            if(isValidate()){

                db.collection("Employees").document(binding.EditUsername.text.toString())
                    .update(
                        mapOf(
                           "fullName" to fullname,
                            "phoneNo" to phone ,
                            "email" to email
                        )
                    )

                db.collection("ReceivedProduct")
                    .get()
                    .addOnSuccessListener { result ->
                        for(document in  result){
                            if(oldName == document.data?.get("ReceivedBy").toString())
                            {
                                db.collection("ReceivedProduct").document(document.id)
                                    .update(
                                        mapOf(
                                            "ReceivedBy" to fullname
                                        )

                                    )
                            }
                        }

                    }

                // set value to view model
                aPerson.fullName = fullname
                aPerson.email = email
                aPerson.phoneNo = phone
                person.setaPerson(aPerson)

                //set the navigation header name
                val navView: NavigationView = binding.root.rootView.findViewById<NavigationView>(R.id.navView)
                val headerView = navView.getHeaderView(0)
                val username : TextView = headerView.findViewById(R.id.usernameDis)
                username.text = binding.Name.text.toString()

                binding.EditUsername.isEnabled = false
                binding.Name.isEnabled = false
                binding.email.isEnabled = false
                binding.phone.isEnabled = false

                binding.btnProfileEdit.visibility = VISIBLE
                binding.btnProfileSubmit.visibility = INVISIBLE
                binding.Name.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.email.onEditorAction(EditorInfo.IME_ACTION_DONE)
                binding.phone.onEditorAction(EditorInfo.IME_ACTION_DONE)

                binding.Name.setText(person.getPerson().fullName)
                binding.email.setText(person.getPerson().email)
                binding.phone.setText(person.getPerson().phoneNo)
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


    // check the input field
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


    // check user fullname
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

    // check user email format and duplicate
    private fun validateEmail(): Boolean {
        val db = Firebase.firestore

        var trueFalse = true
        duplicate = 0

        db.collection("Employees").whereNotEqualTo("username", binding.EditUsername.text.toString())
            .get()
            .addOnSuccessListener { result ->
                var email = binding.email.text.toString().lowercase(Locale.getDefault())
                for(document in result){
                    if(email?.contains(document.data.get("email").toString()) == true){
                        duplicate = 1
                    }
                }

                if (email.trim().isEmpty()) {
                    binding.emailLayout.error = "Required Field!"
                    binding.email.requestFocus()
                    trueFalse = false
                } else if (!validEmail(email)) {
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

    //check user phone number format and duplicate
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