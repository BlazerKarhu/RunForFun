package com.joonasm.sporttracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.joonasm.sporttracker.databinding.FragmentMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.*


class MapFragment : Fragment(R.layout.fragment_map), LocationListener {
    private lateinit var binding: FragmentMapBinding
    lateinit var locationManager: LocationManager
    private lateinit var map: MapView
    private lateinit var marker: Marker
    private var track = true
    private var currentSpeed = 0f
    private var locations = arrayListOf<Location>()
    private var distance = 0f
    private var totalDistance = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        binding = FragmentMapBinding.inflate(layoutInflater)

        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val policy: StrictMode.ThreadPolicy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setMap()
        requestLocation()

        binding.showCurrentLocationFAB.setOnClickListener {
            track = !track
        }

        return binding.root
    }

    private fun setMap() {
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        binding.map.controller.setZoom(17.0)
        binding.map.controller.setCenter(GeoPoint(60.1699, 24.9384))
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500,
                1f,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        getSpeed(location)
        getDistance(location)
        locations.add(location)
        Log.d("LOCATIONS", locations.size.toString())
        if (track) {
            binding.map.controller.setCenter(
                GeoPoint(
                    location.latitude,
                    location.longitude
                )
            )
        }
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d(
                "GEOLOCATION", "new latitude: ${location.latitude} " +
                        "and longitude : ${location.longitude}"
            )
            setMarker(location)
        }
    }

    //get the speed from the given location updates
    private fun getSpeed(location: Location) {
        try {
            currentSpeed = location.speed * 3600 / 1000
            val convertedSpeed = String.format("%.2f", currentSpeed)
            Log.d("SPEED", convertedSpeed + "Km/h")
            binding.speedMeter.text = getString(R.string.speed_meter, currentSpeed)
        } catch (e: Exception) {
            Log.e("SPEED", e.toString())
        }
    }

    //get the distance from the given location updated
    private fun getDistance(location: Location) {
        try {
            distance = locations.last().distanceTo(location)
            totalDistance += distance
            Log.d("DISTANCE", totalDistance.toString())
            binding.distanceMeter.text = getString(R.string.distance_meter, totalDistance)
        } catch (e: Exception) {
            Log.e("DISTANCE", e.toString())
        }
    }

    //Set marker on users current location
    private fun setMarker(location: Location) {
        try {
            marker = Marker(binding.map)
            marker.position = GeoPoint(location.latitude, location.longitude)
            marker.title = getAddress(
                GeoPoint(
                    location.latitude,
                    location.longitude
                )
            )
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.closeInfoWindow()
            marker.setInfoWindow(null)
            binding.map.overlays.clear()
            binding.map.overlays.add(marker)
            map.invalidate()

        } catch (e: Exception) {
            Log.e("Marker", e.toString())
        }
    }
    //TODO add a route polyline

    private fun getAddress(point: GeoPoint): String {
        var address = ""
        val geoCoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(
                point.latitude,
                point.longitude, 1
            )
            if (addresses.size > 0) {
                address = addresses[0].getAddressLine(0)
            }
            //Toast.makeText(context, address, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}

