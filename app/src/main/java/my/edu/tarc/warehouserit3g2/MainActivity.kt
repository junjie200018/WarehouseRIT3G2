package my.edu.tarc.warehouserit3g2


import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Data.Person
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Person: PersonViewModel
    private lateinit var aPerson: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val db = Firebase.firestore


        binding.btnLogin.setOnClickListener {
            val InputUsername = binding.username.text.toString().trim()
            val InputPassword = binding.password.text.toString()

            when {
                TextUtils.isEmpty(InputUsername) -> {
                    if (TextUtils.isEmpty(InputPassword)) {
                        binding.passwordLayout.error = "This field is required!"
                    } else {
                        binding.passwordLayout.isErrorEnabled = false
                    }
                    binding.usernameLayout.error = "This field is required!"

                }
                TextUtils.isEmpty(InputPassword) -> {
                    binding.passwordLayout.error = "This field is required!"
                    binding.usernameLayout.isErrorEnabled = false
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
                                    Person = PersonViewModel.getInstance()
                                    Person.setaPerson(aPerson)
                                    if (aPerson.role == "worker") {
                                        val intent = Intent(this, EmployeeActivity::class.java)
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(intent)

                                    } else if (aPerson.role == "manager") {
                                        val intent = Intent(this, ManagerActivity::class.java)
                                        Toast.makeText(
                                            applicationContext,
                                            "Login Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(intent)
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Password not correct",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } else {
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