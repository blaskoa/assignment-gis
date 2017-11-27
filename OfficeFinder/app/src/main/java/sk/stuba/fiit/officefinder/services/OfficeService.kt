package sk.stuba.fiit.officefinder.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.stuba.fiit.officefinder.models.GeoPoint
import sk.stuba.fiit.officefinder.repositories.OfficeFinderApi

class OfficeService {
    private val officeFinderApi: OfficeFinderApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.88:27519/")
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        officeFinderApi = retrofit.create(OfficeFinderApi::class.java)
    }

    fun get(): List<String> {

        var result: List<String> = emptyList()

        val officeCall = officeFinderApi.getFiltered()
        val response = officeCall.execute()
        if (response.isSuccessful) {
            result = response.body()!!
        }

        return result
    }

    fun get(point: GeoPoint): List<String> {

        var result: List<String> = emptyList()

        val officeCall = officeFinderApi.getFiltered(point)
        val response = officeCall.execute()
        if (response.isSuccessful) {
            result = response.body()!!
        }

        return result
    }
}