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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import sk.stuba.fiit.officefinder.R
import sk.stuba.fiit.officefinder.tasks.GetOfficesTask
import sk.stuba.fiit.officefinder.models.Office
import sk.stuba.fiit.officefinder.models.Parking
import sk.stuba.fiit.officefinder.models.ParkingRequest
import sk.stuba.fiit.officefinder.tasks.GetParkingTask


class MapsActivity :
        FragmentActivity(),
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnMapClickListener {

    override fun onMapClick(p0: LatLng?) {
        hideLayoutWithAnimation(parkingPanel)
        hideLayoutWithAnimation(officePanel)
        if (!isCircleFaded) {
            parkingSelectionCircle.isVisible = false
        }
    }

    override fun onPolygonClick(polygon: Polygon?) {
        moveCameraToPolygon(polygon)
        val tag = polygon!!.tag

        if (tag is PolygonTypes) {
            when(tag) {
                PolygonTypes.OFFICE -> {
                    selectedOfficePolygon = polygon
                    handleOnOfficeClick(officeMap[polygon.id])
                }
                PolygonTypes.PARKING -> {

                }
            }
        }
    }

    private fun handleOnOfficeClick(office: Office?) {
        selectOffice(office!!)
    }

    private fun unselectOffice() {
        selectedOffice = null
        selectedOfficePolygon = null
        hideOfficePanel()
        state = ActivityState.NOTHING
        officePolygons.forEach {
            it.strokeColor = getColorCompat(R.color.strokeActive)
            it.fillColor = getColorCompat(R.color.officeActive)
        }
    }

    private fun hideOfficePanel() {
        hideLayoutWithAnimation(officePanel)
    }

    private fun hideLayoutWithAnimation(linearLayout: LinearLayout) {
        linearLayout.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        linearLayout.visibility = View.GONE
                    }
                })
    }

    private fun selectOffice(office: Office) {
        selectedOffice = office
        officePolygons.forEach {
            if (it.id != selectedOfficePolygon!!.id) {
                it.strokeColor = getColorCompat(R.color.strokeInactive)
                it.fillColor = getColorCompat(R.color.officeInactive)
            }
            else {
                it.strokeColor = getColorCompat(R.color.strokeActive)
                it.fillColor = getColorCompat(R.color.officeActive)
            }

        }
        showOfficePanel(office)
        state = ActivityState.OFFICE
    }

    private fun showOfficePanel(office: Office) {
        officeLabel.text = office.name
        showLayoutWithAnimation(officePanel)
    }

    private fun showLayoutWithAnimation(linearLayout: LinearLayout) {
        linearLayout.visibility = View.VISIBLE
        linearLayout.alpha = 0.0f
        linearLayout.animate()
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
    private var officePolygons: MutableList<Polygon> = ArrayList()
    private var parkingPolygons: MutableList<Polygon> = ArrayList()
    private var officeMap: MutableMap<String, Office> = HashMap()
    private var selectedOffice: Office? = null
    private var selectedOfficePolygon: Polygon? = null
    private var state: ActivityState = ActivityState.NOTHING
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var parkingSelectionCircle: Circle
    private var isCircleFaded: Boolean = false

    private lateinit var officePanel: LinearLayout
    private lateinit var officeLabel: TextView
    private lateinit var parkingPanel: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and getFiltered notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        officePanel = findViewById(R.id.officePanelLayout)
        officeLabel = findViewById(R.id.officeNameTextView)
        parkingPanel = findViewById(R.id.parkingPanelLayout)

        mapFragment.getMapAsync(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun createOfficePolygons(offices: List<Office>) {
        runOnUiThread {
            if (officePolygons.isNotEmpty()) {
                officePolygons.forEach { it.remove() }
                officePolygons.clear()
            }
            if (officeMap.isNotEmpty()) {
                officeMap.clear()
            }

            offices.forEach {
                val pointCollection = it.geoPointStrings.map { it.toLatLngList() }
                val polygonOptions =
                        PolygonOptions()
                                .fillColor(getColorCompat(R.color.officeActive))
                                .strokeColor(getColorCompat(R.color.strokeActive))
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

    fun createParkingPolygons(parking: List<Parking>) {
        runOnUiThread {
            clearParkingPolygonsIfNecessary()
            parking.forEach {
                val pointCollection = it.geoPointStrings.map { it.toLatLngList() }
                val polygonOptions =
                        PolygonOptions()
                                .fillColor(calculateColor(it.score))
                                .strokeWidth(5.0F)
                                .strokeColor(getColorCompat(R.color.strokeActive))
                                .clickable(true)

                pointCollection.forEach({
                    polygonOptions.addAll(it)
                })
                val polygon = map!!.addPolygon(polygonOptions)
                polygon.tag = PolygonTypes.PARKING
                parkingPolygons.add(polygon)
//                officeMap.put(polygon.id, it)
            }
        }
    }

    private fun clearParkingPolygonsIfNecessary() {
        if (parkingPolygons.isNotEmpty()) {
            parkingPolygons.forEach { it.remove() }
            parkingPolygons.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        val refreshButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val selectParkingRangeButton = findViewById<Button>(R.id.selectParkingRangeButton)
        val findParkingButton = findViewById<Button>(R.id.findParkingButton)
        val parkingSeekBar = findViewById<SeekBar>(R.id.parkingRangeSeekBar)
        refreshButton.setOnClickListener {
            unselectOffice()
            hideLayoutWithAnimation(parkingPanel)
            clearParkingPolygonsIfNecessary()
            isCircleFaded = false
            parkingSelectionCircle.isVisible = false
            GetOfficesTask(this).execute(mLastKnownLocation)
        }

        selectParkingRangeButton.setOnClickListener {
            if (state == ActivityState.OFFICE) {
                if (selectedOffice != null && selectedOfficePolygon != null) {
                    state = ActivityState.PARKING_RANGE
                    parkingSelectionCircle.center = getPolygonBounds(selectedOfficePolygon!!.points).center
                    parkingSelectionCircle.isVisible = true
                    isCircleFaded = false
                    hideLayoutWithAnimation(officePanel)
                    showLayoutWithAnimation(parkingPanel)
                }
            }
        }

        parkingSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                parkingSelectionCircle.radius = (5*progress).toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        findParkingButton.setOnClickListener {
            isCircleFaded = true
            parkingSelectionCircle.fillColor = getColorCompat(R.color.circleInactive)
            parkingSelectionCircle.strokeColor = getColorCompat(R.color.strokeInactive)
            hideLayoutWithAnimation(parkingPanel)
            GetParkingTask(this).execute(ParkingRequest(selectedOffice!!.id, parkingSelectionCircle.radius))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val bratislava = LatLng(48.1010091, 17.099517)
        map!!.moveCamera(CameraUpdateFactory.newLatLng(bratislava))
        map!!.moveCamera(CameraUpdateFactory.zoomTo(15F))

        map!!.setOnPolygonClickListener(this)
        map!!.setOnMapClickListener(this)

        val circleOption = CircleOptions()
                .visible(false)
                .radius(5.0)
                .fillColor(getColorCompat(R.color.circleActive))
                .strokeColor(getColorCompat(R.color.strokeActive))
                .center(bratislava)
                .zIndex(1F)
                .strokeWidth(5F)
        parkingSelectionCircle = map!!.addCircle(circleOption)

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

    private fun calculateColor(scale: Double) : Int {
        val hue = (((1-scale)*100) * 1.2).toFloat()
        val hsv = floatArrayOf(hue, 1F, 1F)
        return Color.HSVToColor(150, hsv)
    }

    private fun getColorCompat(id: Int): Int {
        return ContextCompat.getColor(this, id);
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

enum class ActivityState {
    NOTHING, OFFICE, PARKING_RANGE, PARKING
}
