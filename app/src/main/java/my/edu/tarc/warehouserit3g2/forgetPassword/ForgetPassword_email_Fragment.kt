package my.edu.tarc.warehouserit3g2.forgetPassword

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentForgetPasswordEmailBinding
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class ForgetPassword_email_Fragment : Fragment() {
    private lateinit var binding : FragmentForgetPasswordEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password_email_, container, false)
        val db = Firebase.firestore



        binding.btnfgemail.setOnClickListener {

            var username = binding.fgusername.text.toString()
            var email = binding.fgEmail.text.toString()

            if(binding.fgusername.text.toString().isEmpty() || binding.fgusername.text.toString().isBlank()) {
                if(binding.fgEmail.text.toString().isEmpty() || binding.fgEmail.text.toString().isBlank()) {
                    binding.fgEmailLayout.error = "This field is required !"
                    binding.fgEmail.requestFocus()
                }else {
                    binding.fgEmailLayout.isErrorEnabled = false
                }
                binding.fgusernameLayout.error = "This field is required !"
                binding.fgusernameLayout.requestFocus()
            } else if(binding.fgEmail.text.toString().isEmpty() || binding.fgEmail.text.toString().isBlank()) {
                binding.fgEmailLayout.error = "This field is required !"
                binding.fgusernameLayout.isErrorEnabled = false
                binding.fgEmail.requestFocus()
            } else {
                binding.fgEmailLayout.isErrorEnabled = false
                binding.fgusernameLayout.isErrorEnabled = false
                var check = false
                db.collection("Employees")
                    .get()
                    .addOnSuccessListener { result ->
                        for(perInfo in result)
                        {

                            if(perInfo.data?.get("username").toString() == username &&
                                perInfo.data?.get("email").toString() == email  ) {
                                check = true
                                var no  = (Math.random()*999999).toInt()


                                GlobalScope.launch(IO) {
                                    Transport.send(plainMail(no, email))
                                }

                                db.collection("Employees").document(username)
                                    .update(
                                        mapOf(
                                            "resetToken" to no
                                        )
                                    )

                                val action = ForgetPassword_email_FragmentDirections.actionForgetPasswordEmailFragmentToResetTokenValidateFragment(username)
                                Navigation.findNavController(it).navigate(action)

                                Toast.makeText(
                                    context,
                                    "Username and email founded, please check reset password token in your email",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }
                        if(!check) {
                            Toast.makeText(
                                context,
                                "Username or email not found, please enter again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("fail", "Fail to load data")
                    }
            }

        }

        return binding.root
    }

    private fun plainMail(no : Int, email: String): MimeMessage {
        val from = "victorritdemo@gmail.com"

        val properties = System.getProperties()

        with(properties) {
            put("mail.smtp.host", "smtp.gmail.com") //Configure smtp host
            put("mail.smtp.port", "587") //Configure port
            put("mail.smtp.starttls.enable", "true") //Enable TLS
            put("mail.smtp.auth", "true") //Enable authentication
        }

        val auth = object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(from, "demoacc.123") //Credentials of the sender email
        }

        val session = Session.getDefaultInstance(properties, auth)

        val message = MimeMessage(session)

        with(message) {
            setFrom(InternetAddress(from))

            addRecipient(Message.RecipientType.TO, InternetAddress(email))
            subject = "Reset Password Token" //Email subject
            setContent(
                "<html><body><h1>This is your reset password token.<br>Token : <b style='color:red'>${no}</b></h1></body></html>",
                "text/html; charset=utf-8"
            ) //Sending html message, you may change to send text here.

        }

        return message
    }


}