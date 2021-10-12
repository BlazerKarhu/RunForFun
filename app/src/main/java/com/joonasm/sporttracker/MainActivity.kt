package com.joonasm.sporttracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.joonasm.sporttracker.database.ExerciseInfo
import com.joonasm.sporttracker.database.User
import com.joonasm.sporttracker.database.UserDB
import com.joonasm.sporttracker.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding

    //Sensor manager for step counter
    private lateinit var sm: SensorManager
    private var running = false
    private var sensorStepCounter: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        initSensor()
        loadData()
        resetSteps()
        addHardcodedPerson()
    }

    private fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("PERMISSION", "Asking permission")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        0
                    )
                }
            } else {
                Log.d("PERMISSION", "Already have permission")
                registerSensor()
            }
        } else {
            registerSensor()
        }
    }

    override fun onStart() {
        super.onStart()
        requestActivityRecognitionPermission()
        running = true
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

        //Should hide the bottom tab, but does not
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.mapFragment -> showBottomNav()
                R.id.profileFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE

    }

    private fun initSensor() {
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorStepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private fun registerSensor() {
        sensorStepCounter?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        Log.d("STEPS", "New step count ${totalSteps.toInt() - previousTotalSteps.toInt()}")
        if (running) {
            totalSteps = event.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user
            binding.steps.text = ("$currentSteps")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //Nothing here for now
    }

    private fun resetSteps() {
        val tvStepsTaken = binding.steps
        tvStepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tvStepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tvStepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    @DelicateCoroutinesApi
    private fun addHardcodedPerson() {
        val db = UserDB.get(applicationContext)

        GlobalScope.launch {
            val id = db.userDao().insert(User(0, "John", "Doe", 160, 80))
            db.exerciseDao().insert(ExerciseInfo(id, "Run", getCurrentDate(), 65f, 1200f))
            db.exerciseDao().insert(ExerciseInfo(id, "Walk", getCurrentDate(), 30f, 500f))
            withContext(Main) {
                //txtDbInsert.text = getString(R.string.user_added_with_id, id)
                Log.d("DBG", "User added with $id")
            }
        }
    }

    private fun getCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:MM:SS", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }
}