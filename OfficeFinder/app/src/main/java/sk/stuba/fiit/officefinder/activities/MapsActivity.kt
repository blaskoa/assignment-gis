package sk.stuba.fiit.officefinder.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import sk.stuba.fiit.officefinder.R
import sk.stuba.fiit.officefinder.models.GetGeoJSONTask
import sk.stuba.fiit.officefinder.models.Office


class MapsActivity :
        FragmentActivity(),
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnMapClickListener {

    override fun onMapClick(p0: LatLng?) {
        hideSelection()
    }

    override fun onPolygonClick(polygon: Polygon?) {
        moveCameraToPolygon(polygon)
        val tag = polygon!!.tag

        if (tag is PolygonTypes) {
            when(tag) {
                PolygonTypes.OFFICE -> handleOnOfficeClick(officeMap[polygon.id])
                PolygonTypes.PARKING -> TODO()
            }
        }
    }

    private fun handleOnOfficeClick(office: Office?) {
        showSelection(office!!)
    }

    private fun hideSelection() {
        drawer.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        drawer.visibility = View.GONE
                    }
                })
    }

    private fun showSelection(office: Office) {
        officeLabel.text = office.name
        drawer.visibility = View.VISIBLE
        drawer.alpha = 0.0f
        // Start the animation
        drawer.animate()
                .alpha(1.0f)
                .setListener(null)
    }

    private fun moveCameraToPolygon(polygon: Polygon?) {
        val bounds = getPolygonBounds(polygon!!.points)
        map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
    }

    private var map: GoogleMap? = null
    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLastKnownLocation: Location? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var officePolygons: MutableList<Polygon> = ArrayList()
    private var officeMap: MutableMap<String, Office> = HashMap()

    private lateinit var drawer: LinearLayout
    private lateinit var officeLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and getFiltered notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        drawer = findViewById(R.id.drawerLayout)
        officeLabel = findViewById(R.id.officeNameTextView)
        mapFragment.getMapAsync(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun createOfficePolygons(offices: List<Office>) {
        runOnUiThread {
            var color = Color.BLUE
            if (officePolygons.isNotEmpty()) {
                officePolygons.forEach { it.remove() }
                officePolygons.clear()
                color = Color.RED
            }
            if (officeMap.isNotEmpty()) {
                officeMap.clear()
            }

            offices.forEach {
                val pointCollection = it.geoPointStrings.map { it.toLatLngList() }
                val polygonOptions =
                        PolygonOptions()
                                .fillColor(color)
                                .strokeWidth(5.0F)
                                .clickable(true)

                pointCollection.forEach({
                    polygonOptions.addAll(it)
                })
                val polygon = map!!.addPolygon(polygonOptions)
                polygon.tag = PolygonTypes.OFFICE
                officePolygons.add(polygon)
                officeMap.put(polygon.id, it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        button.setOnClickListener {
            hideSelection()
            val x = GetGeoJSONTask(this)
            x.execute(mLastKnownLocation)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val bratislava = LatLng(48.1010091, 17.099517)
        map!!.moveCamera(CameraUpdateFactory.newLatLng(bratislava))
        map!!.moveCamera(CameraUpdateFactory.zoomTo(15F))

        map!!.setOnPolygonClickListener(this)
        map!!.setOnMapClickListener(this)

        updateLocationUI()
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            updateLocationUI()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mLastKnownLocation = task.result
                        if (mLastKnownLocation != null) {
                            val latLng = LatLng(mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude)
                            map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        }
                    } else {
                        Log.d("tag", "Current location is null. Using defaults.")
                        Log.e("tag", "Exception: %s", task.getException())
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    private fun getPolygonBounds(polygonPointsList: List<LatLng>): LatLngBounds {
        val builder = LatLngBounds.Builder()
        for (i in 0 until polygonPointsList.size) {
            builder.include(polygonPointsList[i])
        }

        return builder.build()
    }
}

class Constants {
    companion object {
        const val NAME_BUNDLE_KEY = "NAME_BUNDLE_KEY"
        const val ID_BUNDLE_KEY = "ID_BUNDLE_KEY"
    }
}

enum class PolygonTypes {
    OFFICE, PARKING
}
