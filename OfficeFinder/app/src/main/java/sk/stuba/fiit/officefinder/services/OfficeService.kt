package sk.stuba.fiit.officefinder.services

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import sk.stuba.fiit.officefinder.models.Office
import sk.stuba.fiit.officefinder.repositories.OfficeFinderApi
import java.lang.reflect.GenericSignatureFormatError

class OfficeService {
    private val officeFinderApi: OfficeFinderApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.220:27519/")
                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .build()

        officeFinderApi = retrofit.create(OfficeFinderApi::class.java)
    }

    fun get(): List<JSONObject> {

        var result: List<JSONObject> = emptyList()

        val officeCall = officeFinderApi.get()
        val response = officeCall.execute()
        if (response.isSuccessful) {
            val office = response.body()
            val gson = Gson()
//            val officeReal = gson.fromJson<Office>(office, Office.class)


//            result = office!!.polygons.map {
//                JSONObject(Gson().toJson(it))
//            }
        }

        return result
    }
}