package sk.stuba.fiit.officefinder.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.stuba.fiit.officefinder.models.GeoPoint
import sk.stuba.fiit.officefinder.models.Office
import sk.stuba.fiit.officefinder.repositories.OfficeFinderApi

class OfficeService {
    private val officeFinderApi: OfficeFinderApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.220:27519/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        officeFinderApi = retrofit.create(OfficeFinderApi::class.java)
    }

    fun get(point: GeoPoint): List<Office> {
        var result: List<Office> = emptyList()

        val officeCall = officeFinderApi.getFiltered(point)
        val response = officeCall.execute()
        if (response.isSuccessful) {
            result = response.body()!!
        }

        return result
    }
}