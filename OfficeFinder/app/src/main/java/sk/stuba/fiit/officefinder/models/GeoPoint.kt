package sk.stuba.fiit.officefinder.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

class GeoPoint(
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double
) {
        fun toLatLng() : LatLng {
                return LatLng(latitude, longitude)
        }
}
