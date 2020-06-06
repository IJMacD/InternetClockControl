package com.ijmacd.internetclockcontrol

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.preference.PreferenceManager
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val SAVED_MESSAGE = "saved_message"
    private val SAVED_HEX = "saved_hex"
    private var messageText: EditText? = null
    private var container: LinearLayout? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    @ExperimentalUnsignedTypes
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val c = Communicator(context!!)

        messageText = view.findViewById<EditText>(R.id.message_input)
        messageText?.setText(savedInstanceState?.getString(SAVED_MESSAGE))

        view.findViewById<Button>(R.id.button_send).setOnClickListener {
            c.send(messageText?.text.toString())
        }

        view.findViewById<Button>(R.id.time_button).setOnClickListener {
            c.send("time")
        }

        view.findViewById<Button>(R.id.times_button).setOnClickListener {
            c.send("times")
        }

        view.findViewById<Button>(R.id.date_button).setOnClickListener {
            c.send("date")
        }

        view.findViewById<Button>(R.id.temp_button).setOnClickListener {
            c.send("temp")
        }

        view.findViewById<Button>(R.id.pressure_button).setOnClickListener {
            c.send("pressure")
        }

        view.findViewById<Button>(R.id.cycle_button).setOnClickListener {
            c.send("cycle")
        }

        container = view.findViewById<LinearLayout>(R.id.bitmap_container)
        val dp = context!!.resources.displayMetrics.density
        val layoutParams = ViewGroup.LayoutParams((300 * dp).toInt(),(300 * dp).toInt())
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val count = preferences.getString("num_devices_preference", "1")!!.toInt()
        val savedHex = savedInstanceState?.getString(SAVED_HEX)
        Log.d(javaClass.simpleName, "Restored: $savedHex")
        for (i in 1..count) {
            val be = BitmapEditor(context!!)
            be.layoutParams = layoutParams
            val segment = savedHex?.substring((i - 1) * 16, i * 16)
            if (segment != null && segment.length >= 16) {
                val bytes = ByteArray(8)
                for (i in bytes.indices) {
                    val str = segment.substring(i * 2, i * 2 + 2)
                    bytes[i] = Integer.parseInt(str, 16).toByte()
                }
                be.value = bytes
            }
            container?.addView(be)
            be.setOnChangeListener {
                val hex = getHex()
                c.send("bitmap $hex")

                view.findViewById<TextView>(R.id.bitmap_text).text = hex
            }
        }
    }

    private fun getHex(): String {
        return container!!.children
            .map {
                (it as BitmapEditor).value.map { String.format("%02X", it) }.joinToString("")
            }
            .joinToString("")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_MESSAGE, messageText?.text.toString())
        val hex = getHex()
        Log.d(javaClass.simpleName, "Saved: $hex")
        outState.putString(SAVED_HEX, hex)
        super.onSaveInstanceState(outState)
    }
}
