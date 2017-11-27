package sk.stuba.fiit.officefinder.activities


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import sk.stuba.fiit.officefinder.activities.Constants.Companion.ID_BUNDLE_KEY
import sk.stuba.fiit.officefinder.activities.Constants.Companion.NAME_BUNDLE_KEY

class OfficeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val bundle = arguments
        var officeName = "Unknown"
        if (bundle != null) {
            officeName = bundle.getString(NAME_BUNDLE_KEY, "Unknown")
        }
        val officeId = bundle.getString(ID_BUNDLE_KEY)
        builder.setMessage("Find parking places?")
                .setPositiveButton("Yes") { dialog, id ->
                    // FIRE ZE MISSILES!
                }
                .setNegativeButton("No") { dialog, id ->
                    // User cancelled the dialog
                }
                .setTitle(officeName)
        // Create the AlertDialog object and return it
        return builder.create()
    }


}
