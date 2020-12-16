package com.amuyu.groutingbolivia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amuyu.groutingbolivia.customcomponents.FabBottomNavigationView
import com.amuyu.groutingbolivia.ui.cart.MainCartFragment
import com.amuyu.groutingbolivia.ui.reportes.dialog.DisplayReport
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
        navController.addOnDestinationChangedListener { _, dest, _ ->
            when (dest.id){
                R.id.navigation_home -> {
                    main_fab.show()
                    main_fab.setImageResource(R.drawable.ic_twotone_shopping_cart_24)
                }
                R.id.navigation_dashboard -> {
                    main_fab.hide()
                }
                R.id.navigation_notifications -> {
                    main_fab.show()
                    main_fab.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24)
                }
                R.id.navigation_profile -> {
                    main_fab.hide()
                }
            }
        }
        main_fab.setOnClickListener {
            when  (navController.currentDestination!!.id) {
            R.id.navigation_home -> {
            displayCart()
        }
            R.id.navigation_dashboard -> {
            main_fab.hide()
        }
            R.id.navigation_notifications -> {
            displayPDF()
        }
            R.id.navigation_profile -> {
            main_fab.hide()
        }
        }
        }
    mViewModel.cartSize.observe(this, Observer {
        Log.d(TAG, "$it")
        cartSize = it?:0
    })
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                .requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    121);
        }
    }
    fun displayCart(){
        val aux = MainCartFragment.newInstance()
        if(cartSize>0) {
            aux.show(supportFragmentManager.beginTransaction(), "MainCartFragment")
        }else{
            Toast.makeText(this, "Porfavor agregue un item al carrito", Toast.LENGTH_SHORT).show()
        }
    }
    fun displayPDF(){
        try{
            val desde = mViewModel.desde.value!!
            val hasta = mViewModel.hasta.value!!
            DisplayReport.newInstance(desde, hasta).also {
                it.show(supportFragmentManager, "Display REport")
            }
        }catch (e: Exception){
            Toast.makeText(this, "Porfavor seleccione un intervalo de tiempo valido", Toast.LENGTH_SHORT).show()
        }
    }
}