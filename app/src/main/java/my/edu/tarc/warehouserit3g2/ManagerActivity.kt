package my.edu.tarc.warehouserit3g2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityManagerBinding

class ManagerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityManagerBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var person: ViewModel

    private lateinit var img : ImageView
    private var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_manager)

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager)

        //side menu
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarManager.toolbar)

//        binding.appBarManager.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val username : TextView = headerView.findViewById(R.id.usernameDis)
        val profilePhoto : ImageView = headerView.findViewById(R.id.ProfilePhoto)

        profilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            launchSomeActivity.launch(intent)

        }

        val navController = findNavController(R.id.managerNavHostFragment)

        person = ViewModel.getInstance()
        username.text = person.getPerson().fullName

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.productMovement_Fragment2,R.id.stockIn_Fragment, R.id.stockOut_Fragment,
                R.id.profileEdit_Fragment2,R.id.currentQty_Fragment, R.id.transitList_Fragment,
                R.id.searchStock_Fragment, R.id.addRack, R.id.receiveProduct_Fragment
            ),drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    var launchSomeActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            imgUri  = data?.data
            img.setImageURI(data?.data)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.managerNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}