package weiss.kotlin.wordleexperiment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment


class WinDialog : DialogFragment(){

    private val TAG = "WinOrLossDialogActivity"
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            sharedPreferences = requireActivity().applicationContext.getSharedPreferences("preferences_file",Context.MODE_PRIVATE)
            var string = sharedPreferences.getString("solution", "default")
            var winningDialog = "The winning word was $string. \nPlay again?"

            val builder = AlertDialog.Builder(it)
            builder.setTitle("Congratulations!")
            builder.setMessage(winningDialog)
                .setPositiveButton(R.string.start,
                    DialogInterface.OnClickListener { dialog, id ->
                        // START THE GAME!
                        Log.d(TAG, "start button pressed.")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog. I'm leaving this empty because it seems to be working as-is.
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}