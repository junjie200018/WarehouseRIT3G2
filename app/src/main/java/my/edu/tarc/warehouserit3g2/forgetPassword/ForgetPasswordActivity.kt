package my.edu.tarc.warehouserit3g2.forgetPassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.edu.tarc.warehouserit3g2.R
import javax.mail.internet.MimeMessage
import javax.mail.*
import javax.mail.internet.InternetAddress

class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
    }
}