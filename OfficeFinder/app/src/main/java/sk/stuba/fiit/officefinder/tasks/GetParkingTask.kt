package sk.stuba.fiit.officefinder.tasks

import android.os.AsyncTask
import sk.stuba.fiit.officefinder.activities.MapsActivity
import sk.stuba.fiit.officefinder.models.Parking
import sk.stuba.fiit.officefinder.models.ParkingRequest
import sk.stuba.fiit.officefinder.services.OfficeService

class GetParkingTask(private var activity: MapsActivity) : AsyncTask<ParkingRequest, Unit, List<Parking>>() {
    override fun doInBackground(vararg params: ParkingRequest): List<Parking> {
        val service = OfficeService()

        return service.getParking(params[0])
    }

    override fun onPostExecute(result: List<Parking>) {
        activity.createParkingPolygons(result)
    }
}