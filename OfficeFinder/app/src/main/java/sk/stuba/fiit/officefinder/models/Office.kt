package sk.stuba.fiit.officefinder.models

import com.google.gson.annotations.SerializedName

class Office(
        @SerializedName("name")
        val name: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("geoPointStrings")
        val geoPointStrings: List<GeoPointString>
)
