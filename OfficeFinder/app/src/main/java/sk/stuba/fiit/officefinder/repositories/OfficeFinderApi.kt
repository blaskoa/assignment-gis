package sk.stuba.fiit.officefinder.repositories

import retrofit2.Call
import retrofit2.http.GET
import sk.stuba.fiit.officefinder.models.Office

interface  OfficeFinderApi {
    @GET("/api/Offices")
    fun get(): Call<String>
}