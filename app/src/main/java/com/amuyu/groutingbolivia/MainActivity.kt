package com.amuyu.groutingbolivia

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amuyu.groutingbolivia.customcomponents.FabBottomNavigationView
import com.amuyu.groutingbolivia.ui.cart.MainCartFragment
import kotlinx.android.synthetic.main.activity_main.*
const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private var cartSize = 0
    private val mViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: FabBottomNavigationView = findViewById(R.id.nav_view)

        //val navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        main_fab.setOnClickListener {
            val aux = MainCartFragment.newInstance()
            if(cartSize>0) {
                aux.show(supportFragmentManager.beginTransaction(), "MainCartFragment")
            }else{
                Toast.makeText(this, "Porfavor agregue un item al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    mViewModel.cartSize.observe(this, Observer {
        Log.d(TAG, "$it")
        cartSize = it?:0
    })
    }
}