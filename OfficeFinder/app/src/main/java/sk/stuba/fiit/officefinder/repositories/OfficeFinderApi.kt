package sk.stuba.fiit.officefinder.repositories

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import sk.stuba.fiit.officefinder.models.GeoPoint
import sk.stuba.fiit.officefinder.models.Office

interface OfficeFinderApi {
    @POST("/api/Offices")
    fun getFiltered(@Body point: GeoPoint): Call<List<Office>>
}