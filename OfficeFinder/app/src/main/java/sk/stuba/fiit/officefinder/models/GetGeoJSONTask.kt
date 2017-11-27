package sk.stuba.fiit.officefinder.models

import android.location.Location
import android.os.AsyncTask
import sk.stuba.fiit.officefinder.activities.MapsActivity
import sk.stuba.fiit.officefinder.services.OfficeService

class GetGeoJSONTask(private var activity: MapsActivity) : AsyncTask<Location?, Unit, List<Office>>() {
    override fun doInBackground(vararg params: Location?): List<Office> {
        val service = OfficeService()
        val location = params[0]

        var point = GeoPoint(49.0, 19.0)
        if (location != null) {
            point = GeoPoint(location.latitude, location.longitude)
        }

        return service.get(point)
    }

    override fun onPostExecute(result: List<Office>) {
        activity.createOfficePolygons(result)
    }
}