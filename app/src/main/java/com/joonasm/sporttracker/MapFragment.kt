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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.joonasm.sporttracker.databinding.FragmentMapBinding
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

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
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
                1000,
                1f,
                this
            )
        }
    }

    //Create a function that disables or enables updating your location on map


    override fun onLocationChanged(location: Location) {
        Log.d(
            "GEOLOCATION", "new latitude: ${location.latitude} " +
                    "and longitude : ${location.longitude}"
        )

        marker = Marker(binding.map)
        if (track){
            binding.map.controller.setCenter(
                GeoPoint(
                    location.latitude,
                    location.longitude
                )
            )
        }
        marker.position = GeoPoint(location.latitude, location.longitude)
        marker.title = getAddress(
            GeoPoint(
                location.latitude,
                location.longitude
            )
        )
        marker.setId("meLocation")
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.closeInfoWindow()
        binding.map.overlays.clear()
        binding.map.overlays.add(marker)
        map.invalidate()
    }

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
            Toast.makeText(context, address, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}

