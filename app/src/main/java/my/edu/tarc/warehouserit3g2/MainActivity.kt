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
import androidx.lifecycle.ViewModel
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityMainBinding
import my.edu.tarc.warehouserit3g2.person.Person
import my.edu.tarc.warehouserit3g2.person.PersonDB
import my.edu.tarc.warehouserit3g2.person.PersonDao

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Person: PersonViewModel
    private lateinit var dao: PersonDao
    private lateinit var aPerson: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(IO).launch {
            dao = PersonDB.getInstance(applicationContext).personDao
            Person = PersonViewModel.getInstance()
            //dao.removeAll()

            var person = dao.getPerson()

            if(person != null) {
                Person.setaPerson(person)
                if (person.role == "worker") {
                    intent("worker")
                } else if (person.role == "manager") {
                    intent("manager")
                } else if (person.role == "driver") {
                    intent("driver")
                }
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val db = Firebase.firestore

        binding.btnLogin.setOnClickListener {
            binding.loadingIndi.visibility = View.VISIBLE
            val InputUsername = binding.username.text.toString().trim()
            val InputPassword = binding.password.text.toString()
//            val passHash = BCrypt.withDefaults().hashToString(12, "12345".toCharArray())
//            Log.d("register", "$passHash")
            binding.usernameLayout.isFocusable = true
            binding.usernameLayout.isEnabled = true
            when {
                TextUtils.isEmpty(InputUsername) -> {
                    if (TextUtils.isEmpty(InputPassword)) {
                        binding.passwordLayout.error = "This field is required!"
                    } else {
                        binding.passwordLayout.isErrorEnabled = false
                    }
                    binding.usernameLayout.error = "This field is required!"
                    binding.username.requestFocus()
                }
                TextUtils.isEmpty(InputPassword) -> {
                    binding.passwordLayout.error = "This field is required!"
                    binding.usernameLayout.isErrorEnabled = false
                    binding.password.requestFocus()
                }
                else -> {
                    binding.usernameLayout.isErrorEnabled = false
                    binding.passwordLayout.isErrorEnabled = false
                    db.collection("Employees").document(InputUsername)
                        .get()
                        .addOnSuccessListener { acc ->

                            if (acc.data != null) {
                                aPerson = Person(0,acc.data?.get("username").toString(),acc.data?.get("password").toString(),acc.data?.get("role").toString(),
                                    acc.data?.get("fullName").toString(),acc.data?.get("email").toString(),acc.data?.get("phoneNo").toString())

                                val correct = BCrypt.verifyer().verify(
                                    InputPassword.toCharArray(),
                                    aPerson.password
                                )

                                if (correct.verified) {


                                    if(binding.rmbCheck.isChecked) {
                                        CoroutineScope(IO).launch {
                                            dao.insertPerson(aPerson)
                                        }
                                    }
                                    Person.setaPerson(aPerson)
                                    if (aPerson.role == "worker") {
                                        val intent = Intent(this, EmployeeActivity::class.java)
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        binding.loadingIndi.visibility = View.INVISIBLE
                                        startActivity(intent)

                                    } else if (aPerson.role == "manager") {
                                        val intent = Intent(this, ManagerActivity::class.java)
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        binding.loadingIndi.visibility = View.INVISIBLE
                                        startActivity(intent)
                                    } else if (aPerson.role == "driver") {
                                        val intent = Intent(this, DriverActivity::class.java)
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        binding.loadingIndi.visibility = View.INVISIBLE
                                        startActivity(intent)
                                    }
                                    binding.username.clearFocus()
                                    binding.password.clearFocus()
                                    binding.username.text?.clear()
                                    binding.password.text?.clear()

                                } else {
                                    binding.password.requestFocus()
                                    Toast.makeText(
                                        applicationContext,
                                        "Password not correct",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } else {
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

    private fun intent(role:String) {
        binding.loadingIndi.visibility = View.INVISIBLE
        if(role == "worker") {
            val intent = Intent(this, EmployeeActivity::class.java)
            startActivity(intent)
        } else if(role == "manager") {
            val intent = Intent(this, ManagerActivity::class.java)
            startActivity(intent)
        } else if(role == "driver") {
            val intent = Intent(this, DriverActivity::class.java)
            startActivity(intent)
        }
    }
}