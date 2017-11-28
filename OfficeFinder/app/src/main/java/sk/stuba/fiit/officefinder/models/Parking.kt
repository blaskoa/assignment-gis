package sk.stuba.fiit.officefinder.models

import com.google.gson.annotations.SerializedName

class Parking (
        @SerializedName("name")
        val name: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("geoPointStrings")
        val geoPointStrings: List<GeoPointString>,
        @SerializedName("distance")
        val distance: Double,
        @SerializedName("score")
        val score: Double,
        @SerializedName("access")
        val access: String
)