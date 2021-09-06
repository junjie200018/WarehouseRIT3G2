package my.edu.tarc.warehouserit3g2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import my.edu.tarc.warehouserit3g2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//
//        drawerLayout = binding.drawerLayout
//
//        setSupportActionBar(binding.appBarMain.toolbar)
//
//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//
////        val drawerLayout: DrawerLayout = binding.drawerLayout
//        val navView: NavigationView = binding.navView
//        val navController = findNavController(R.id.myNavHostFragment)
//
//
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.home_Fragment, R.id.receiveProduct_Fragment, R.id.onReceived_Fragment, R.id.display_Received_item_Fragment
//            ), drawerLayout
//        )
//
//
//
//       // val navController = findNavController(R.id.myNavHostFragment)
//
////        NavigationUI.setupActionBarWithNavController(this,navController, drawerLayout)
//
////        NavigationUI.setupWithNavController(binding.navView, navController)
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.myNavHostFragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
}