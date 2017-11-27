package sk.stuba.fiit.officefinder.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

class GeoPointString (
        @SerializedName("geoPoints")
        val geoPoints: List<GeoPoint>
) {
        fun toLatLngList(): List<LatLng> {
            return geoPoints.map { it.toLatLng() }
        }
}