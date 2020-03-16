package com.ijmacd.internetclockcontrol

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class Communicator (val context: Context) {

    fun send (text: String) {
        Log.d(javaClass.simpleName, "Sending {$text}")
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val address = preferences.getString("host_preference", "")
        val port = preferences.getString("port_preference", "0")!!.toInt()
        doAsync {
            try {
                val connection = Socket(address, port)
                val writer: OutputStream = connection.getOutputStream()
                writer.write((text + "\n").toByteArray())
                val input = BufferedReader(InputStreamReader(connection.getInputStream()))
                if (input.readLine() == "thanks")
                    vibrate()
                connection.close()
            } catch (e: Exception) {}
        }.execute()
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= 26) {
            if (vibrator.hasAmplitudeControl())
                vibrator.vibrate(VibrationEffect.createOneShot(10, 0x20))
            else
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
        } else vibrator.vibrate(10)
    }

    class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}
