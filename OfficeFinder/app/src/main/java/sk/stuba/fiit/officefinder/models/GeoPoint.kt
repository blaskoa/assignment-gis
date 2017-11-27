package sk.stuba.fiit.officefinder.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Adam Blasko on 26. 11. 2017.
 */

class GeoPoint(
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double
)
