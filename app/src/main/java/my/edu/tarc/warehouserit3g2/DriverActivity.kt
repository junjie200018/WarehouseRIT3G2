package my.edu.tarc.warehouserit3g2

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityDriverBinding
import java.io.ByteArrayOutputStream

class DriverActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityDriverBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var person: ViewModel
    private lateinit var img : ImageView
    private var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_driver)

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver)

        //side menu
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarDriver.toolbar)

        val db = Firebase.firestore
        person = ViewModel.getInstance()

        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val navController = findNavController(R.id.DriverNavHostFragment)
        val username : TextView = headerView.findViewById(R.id.usernameDis)
        img = headerView.findViewById(R.id.ProfilePhoto)

        db.collection("Employees").document(person.getPerson().username)
            .get()
            .addOnSuccessListener { result ->

                val decodedString: ByteArray = Base64.decode(result.data?.get("Image").toString(), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                img.setImageBitmap(decodedByte)
            }

        img.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launchSomeActivity.launch(intent)
        }

        username.text = person.getPerson().fullName

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.pickupListFragment, R.id.profileEdit_Fragment3
            ),drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    var launchSomeActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val db = Firebase.firestore
        person = ViewModel.getInstance()
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            imgUri  = data?.data
            img.setImageURI(data?.data)
        }

        val bitmap = (img.getDrawable() as BitmapDrawable).bitmap
        val byteArray: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val StrImg: String = Base64.encodeToString(byteArray.toByteArray(), Base64.DEFAULT)

        db.collection("Employees").document(person.getPerson().username)
            .update(
                mapOf(
                    "Image" to StrImg
                )
            )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.DriverNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}