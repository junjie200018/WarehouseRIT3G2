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
import my.edu.tarc.warehouserit3g2.databinding.ActivityManagerBinding

class ManagerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityManagerBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var Person: PersonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_manager)

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager)

        //side menu
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarManager.toolbar)

        binding.appBarManager.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val navController = findNavController(R.id.managerNavHostFragment)
        val username : TextView = headerView.findViewById(R.id.usernameDis)

        Person = PersonViewModel.getInstance()
        username.text = Person.getPerson().fullName

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeManager_Fragment,R.id.productMovement_Fragment2,R.id.stockIn_Fragment, R.id.stockOut_Fragment
            ),drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.managerNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}