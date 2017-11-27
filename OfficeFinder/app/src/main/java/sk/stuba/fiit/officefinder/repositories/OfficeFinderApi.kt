package sk.stuba.fiit.officefinder.repositories

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import sk.stuba.fiit.officefinder.models.GeoPoint

interface OfficeFinderApi {
    @GET("/api/Offices")
    fun getFiltered(): Call<List<String>>

    @POST("/api/Offices")
    fun getFiltered(@Body point: GeoPoint): Call<List<String>>
}