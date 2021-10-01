package com.joonasm.sporttracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.joonasm.sporttracker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHost.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.mapFragment, R.id.profileFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = binding.bottomNavigationView
        navView.setupWithNavController(navController)
    }
}