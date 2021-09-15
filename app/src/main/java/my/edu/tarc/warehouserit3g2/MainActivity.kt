package my.edu.tarc.warehouserit3g2


import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.Models.ViewModel

import my.edu.tarc.warehouserit3g2.databinding.ActivityMainBinding
import my.edu.tarc.warehouserit3g2.forgetPassword.ForgetPasswordActivity
import my.edu.tarc.warehouserit3g2.person.Person
import my.edu.tarc.warehouserit3g2.person.PersonDB
import my.edu.tarc.warehouserit3g2.person.PersonDao

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Person: ViewModel
    private lateinit var dao: PersonDao
    private lateinit var aPerson: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ac = this
        CoroutineScope(IO).launch {

            //connect local database
            dao = PersonDB.getInstance(applicationContext).personDao

            //get view model
            Person = ViewModel.getInstance()

            //get person from local database
            var person = dao.getPerson()

            //remember me
            if(person != null) {
                Person.setaPerson(person)
                if (person.role == "worker") {
                    intent("worker")
                } else if (person.role == "manager") {
                    intent("manager")
                } else if (person.role == "driver") {
                    intent("driver")
                }

            } else {
                CoroutineScope(Main).launch {
                    binding = DataBindingUtil.setContentView(ac, R.layout.activity_main)

                    //connect firebase
                    val db = Firebase.firestore

                    //call forget password activity
                    binding.forgetPassword.setOnClickListener {
                        intent("forget")
                    }

                    binding.btnLogin.setOnClickListener {

                        val InputUsername = binding.username.text.toString().trim()
                        val InputPassword = binding.password.text.toString()

                        binding.usernameLayout.isFocusable = true
                        binding.usernameLayout.isEnabled = true
                        //validate
                        when {
                            TextUtils.isEmpty(InputUsername) || InputUsername.isBlank() -> {
                                if (TextUtils.isEmpty(InputPassword) || InputPassword.isBlank()) {
                                    binding.passwordLayout.error = "This field is required!"
                                } else {
                                    binding.passwordLayout.isErrorEnabled = false
                                }
                                binding.usernameLayout.error = "This field is required!"
                                binding.username.requestFocus()
                            }
                            TextUtils.isEmpty(InputPassword) || InputPassword.isBlank() -> {
                                binding.passwordLayout.error = "This field is required!"
                                binding.usernameLayout.isErrorEnabled = false
                                binding.password.requestFocus()
                            }
                            else -> {
                                binding.loadingIndi.visibility = View.VISIBLE
                                binding.usernameLayout.isErrorEnabled = false
                                binding.passwordLayout.isErrorEnabled = false

                                db.collection("Employees").document(InputUsername)
                                    .get()
                                    .addOnSuccessListener { acc ->
                                        if (acc.data != null) {
                                            aPerson = Person(
                                                0,
                                                acc.data?.get("username").toString(),
                                                acc.data?.get("password").toString(),
                                                acc.data?.get("role").toString(),
                                                acc.data?.get("fullName").toString(),
                                                acc.data?.get("email").toString(),
                                                acc.data?.get("phoneNo").toString()
                                            )

                                            //verify password with hash format
                                            val correct = BCrypt.verifyer().verify(
                                                InputPassword.toCharArray(),
                                                aPerson.password
                                            )

                                            if (correct.verified) {

                                                if (binding.rmbCheck.isChecked) {
                                                    CoroutineScope(IO).launch {
                                                        dao.insertPerson(aPerson)
                                                    }
                                                }
                                                Person.setaPerson(aPerson)

                                                if (aPerson.role == "worker") {
                                                    intent("worker")
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Login Successful",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    binding.loadingIndi.visibility = View.INVISIBLE

                                                } else if (aPerson.role == "manager") {
                                                    intent("manager")
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Login Successful",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    binding.loadingIndi.visibility = View.INVISIBLE

                                                } else if (aPerson.role == "driver") {
                                                    intent("driver")
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Login Successful",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    binding.loadingIndi.visibility = View.INVISIBLE

                                                }
                                                binding.username.clearFocus()
                                                binding.password.clearFocus()
                                                binding.username.text?.clear()
                                                binding.password.text?.clear()

                                            } else {
                                                binding.loadingIndi.visibility = View.INVISIBLE
                                                binding.password.requestFocus()
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Password not correct",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                        } else {
                                            binding.loadingIndi.visibility = View.INVISIBLE
                                            binding.username.requestFocus()
                                            Toast.makeText(
                                                applicationContext,
                                                "Username not found",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun intent(role:String) {
        if(role == "worker") {
            val intent = Intent(this, EmployeeActivity::class.java)
            startActivity(intent)
        } else if(role == "manager") {
            val intent = Intent(this, ManagerActivity::class.java)
            startActivity(intent)
        } else if(role == "driver") {
            val intent = Intent(this, DriverActivity::class.java)
            startActivity(intent)
        } else if(role == "forget") {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}