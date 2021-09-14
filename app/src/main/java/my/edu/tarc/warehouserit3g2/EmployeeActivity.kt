package my.edu.tarc.warehouserit3g2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.databinding.ActivityEmployeeBinding



class EmployeeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var app2: AppBarConfiguration
    private lateinit var person: ViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_employee)

        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.appBarMain.toolbar)




        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val username : TextView = headerView.findViewById(R.id.usernameDis)
        val navController = findNavController(R.id.myNavHostFragment)

        person = ViewModel.getInstance()
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}