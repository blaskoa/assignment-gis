package sk.stuba.fiit.officefinder.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject
import sk.stuba.fiit.officefinder.R
import sk.stuba.fiit.officefinder.models.GetGeoJSONTask

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    public fun updateJSON(geoJson: JSONObject) {
        runOnUiThread {
            val test = GeoJsonLayer(map, geoJson)
            test.addLayerToMap()
        }
    }

    override fun onResume() {
        super.onResume()
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        button.setOnClickListener {
            val x = GetGeoJSONTask(this)
            x.execute()
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
    }
}
