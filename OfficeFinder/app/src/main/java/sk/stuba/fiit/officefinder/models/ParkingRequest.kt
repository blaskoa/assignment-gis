package sk.stuba.fiit.officefinder.models

import com.google.gson.annotations.SerializedName

class ParkingRequest(
        @SerializedName("officeId")
        val officeId: Long,
        @SerializedName("distance")
        val distance: Double
)