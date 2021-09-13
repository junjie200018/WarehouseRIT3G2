package my.edu.tarc.warehouserit3g2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityEmployeeBinding


class EmployeeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var Person: PersonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_employee)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_employee)

        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

//        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val username : TextView = headerView.findViewById(R.id.usernameDis)
        val navController = findNavController(R.id.myNavHostFragment)

        Person = PersonViewModel.getInstance()
        username.text = Person.getPerson().fullName

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_Fragment, R.id.receiveProduct_Fragment, R.id.onReceived_Fragment,
                R.id.display_Received_item_Fragment, R.id.onRack_Product_Fragment, R.id.onRack_Display_Fragment,
                R.id.changeRack_product_Fragment, R.id.scanScrap_Fragment
            ), drawerLayout
        )



        // val navController = findNavController(R.id.myNavHostFragment)

//        NavigationUI.setupActionBarWithNavController(this,navController, drawerLayout)

//        NavigationUI.setupWithNavController(binding.navView, navController)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}