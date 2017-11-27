package sk.stuba.fiit.officefinder.activities

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import org.json.JSONObject
import sk.stuba.fiit.officefinder.R
import sk.stuba.fiit.officefinder.models.GetGeoJSONTask


class MapsActivity :
        FragmentActivity(),
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener
{
    override fun onPolygonClick(p0: Polygon?) {
        p0!!.id
    }

    private var map: GoogleMap? = null
    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLastKnownLocation: Location? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var officeLayers: MutableList<GeoJsonLayer> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and getFiltered notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun createOfficeLayers(offices: List<String>?) {
        runOnUiThread {
            if (officeLayers.isNotEmpty()) {
                officeLayers.forEach({it.removeLayerFromMap()})
                officeLayers.clear()
            }
            offices!!.forEach {
                val officeLayer = GeoJsonLayer(map, JSONObject(it))
                officeLayer.features.forEach({ feature ->
                    val style = GeoJsonPolygonStyle()
                    style.fillColor = Color.RED
                    feature.polygonStyle = style
                })
                officeLayer.setOnFeatureClickListener({ feature ->
                    val bundle = Bundle()
                    bundle.putString(Constants.NAME_BUNDLE_KEY, feature.getProperty("name"))
                    bundle.putString(Constants.ID_BUNDLE_KEY, feature.id)

                    val dialog = OfficeDialogFragment()
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "tag2")
                })
                officeLayer.addLayerToMap()
                officeLayers.add(officeLayer)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        button.setOnClickListener {
            val x = GetGeoJSONTask(this)
            x.execute(mLastKnownLocation)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the

        val bratislava = LatLng(48.1010091, 17.099517)
        map!!.addMarker(MarkerOptions().position(bratislava).title("Marker in Bratislava"))
        map!!.moveCamera(CameraUpdateFactory.newLatLng(bratislava))
        map!!.moveCamera(CameraUpdateFactory.zoomTo(15F))
        map!!.setOnPolygonClickListener(this)

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
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
        /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
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
                    }
                    else {
                        Log.d("tag", "Current location is null. Using defaults.")
                        Log.e("tag", "Exception: %s", task.getException())
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }
}

class Constants {
    companion object {
        const val NAME_BUNDLE_KEY = "NAME_BUNDLE_KEY"
        const val ID_BUNDLE_KEY = "ID_BUNDLE_KEY"
    }
}
