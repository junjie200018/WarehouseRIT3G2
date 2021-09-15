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
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityEmployeeBinding
import java.io.ByteArrayOutputStream


class EmployeeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var app2: AppBarConfiguration
    private lateinit var person: ViewModel
    private lateinit var img : ImageView
    private var imgUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_employee)

        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarMain.toolbar)
        person = ViewModel.getInstance()



        val db = Firebase.firestore
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val username : TextView = headerView.findViewById(R.id.usernameDis)
        val navController = findNavController(R.id.myNavHostFragment)
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
                 R.id.receiveProduct_Fragment, R.id.onReceived_Fragment,
                R.id.display_Received_item_Fragment, R.id.onRack_Product_Fragment, R.id.onRack_Display_Fragment,
                R.id.changeRack_product_Fragment, R.id.scanScrap_Fragment,R.id.profileEdit_Fragment, R.id.receiveProductList_Fragment,
                R.id.scrapList_Fragment, R.id.displayTransit_Fragment, R.id.rackList_Fragment, R.id.scanTransit_Product_Fragment
            ), drawerLayout
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
            Log.w(ContentValues.TAG, "abaaba 2 = ${imgUri}")
            img.setImageURI(data?.data)
        }

        val bitmap = (img.getDrawable() as BitmapDrawable).bitmap
        Log.w(ContentValues.TAG, "abaaba 3 = ${bitmap}")

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
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}