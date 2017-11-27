package sk.stuba.fiit.officefinder.models

import com.google.gson.annotations.SerializedName

class Office(
        @SerializedName("name")
        val Name: String,
        val id: Int
)
