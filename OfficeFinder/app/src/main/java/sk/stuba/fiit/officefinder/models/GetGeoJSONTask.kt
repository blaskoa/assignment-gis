package sk.stuba.fiit.officefinder.models

import android.os.AsyncTask
import org.json.JSONObject
import sk.stuba.fiit.officefinder.activities.MapsActivity
import sk.stuba.fiit.officefinder.services.OfficeService

class GetGeoJSONTask(private var activity: MapsActivity) : AsyncTask<Unit, Unit, List<JSONObject>>() {
    override fun doInBackground(vararg params: Unit?): List<JSONObject> {
        val service = OfficeService()
        return service.get()
    }

    override fun onPostExecute(result: List<JSONObject>?) {
        result!!.forEach {
            activity.updateJSON(it)
        }
    }
}