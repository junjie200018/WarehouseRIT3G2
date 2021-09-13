package my.edu.tarc.warehouserit3g2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import my.edu.tarc.warehouserit3g2.databinding.ActivityDriverBinding
import my.edu.tarc.warehouserit3g2.databinding.ActivityManagerBinding

class DriverActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityDriverBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_driver)

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver)

        //side menu
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarDriver.toolbar)

        binding.appBarDriver.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.DriverNavHostFragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.pickupListFragment
            ),drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.DriverNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}