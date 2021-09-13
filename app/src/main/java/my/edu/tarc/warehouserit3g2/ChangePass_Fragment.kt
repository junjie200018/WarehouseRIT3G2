package my.edu.tarc.warehouserit3g2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangePassBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentChangeRackRackBinding
import java.util.regex.Pattern


class ChangePass_Fragment : Fragment() {

    private lateinit var binding: FragmentChangePassBinding
    private lateinit var Person: PersonViewModel
    private val navController by lazy { NavHostFragment.findNavController(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_pass, container, false)
        val db = Firebase.firestore

        binding.btnChangeSubmit.setOnClickListener {



            Person = PersonViewModel.getInstance()
            var userName = Person.getUsername().username
            var oldPass = Person.getUsername().password
            var newPassword = binding.newPassword.text.toString()
            var retypePassword = binding.retypePassword.text.toString()

                    var oldPassword= binding.oldPassword.text.toString()
//user input
//                    val passHash = BCrypt.withDefaults().hashToString(12, "12345".toCharArray())

                    val correct = BCrypt.verifyer().verify(
                        oldPassword.toCharArray(),
                        oldPass
                    )
                    if(!correct.verified){
                        Toast.makeText(
                            context,
                            "Old Password not Correct",
                            Toast.LENGTH_LONG
                        ).show()
                    }else{
                        if( checkPass(newPassword) ){
                            if(checkPass(retypePassword)){
                                if(newPassword.equals(retypePassword)){
                                    val passHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

                                    db.collection("Employees").document(userName)
                                        .update(
                                            mapOf(
                                                "password" to passHash
                                            )
                                        )
                                    Toast.makeText(
                                        context,
                                        "Change password completed",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate(R.id.action_changePass_Fragment_to_profileEdit_Fragment)
                                }else{
                                    Toast.makeText(
                                        context,
                                        "Retype Password not Correct. Please try again !!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }else{
                                Toast.makeText(
                                    context,
                                    "Retype Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }else{
                            Toast.makeText(
                                context,
                                "New Password must be between 6 to 20, contain at least 1 lower and uppercase, a digit and a symbol",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }




        }

        return binding.root
    }

    private fun checkPass (pass: String):Boolean{

        var REG = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,20}$"
        if(!Pattern.compile(REG).matcher(binding.newPassword.text.toString()).matches()){
            return false
        }
        return true
    }

}