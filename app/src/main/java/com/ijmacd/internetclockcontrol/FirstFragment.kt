package com.ijmacd.internetclockcontrol

import android.os.Bundle
import android.os.Debug
import android.util.AttributeSet
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

        view.findViewById<Button>(R.id.button_send).setOnClickListener {
            val text = view.findViewById<EditText>(R.id.message_input)
            c.send(text.text.toString())
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

        val container = view.findViewById<LinearLayout>(R.id.bitmap_container)
        val dp = context!!.resources.displayMetrics.density
        val layoutParams = ViewGroup.LayoutParams((300 * dp).toInt(),(300 * dp).toInt())
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val count = preferences.getString("num_devices_preference", "1")!!.toInt()
        for (i in 1..count) {
            val be = BitmapEditor(context!!)
            be.layoutParams = layoutParams
            container.addView(be)
            be.setOnChangeListener {
                val hex = container.children
                    .map { (it as BitmapEditor).value.toUByteArray().joinToString("") {
                        it.toString(16).toUpperCase(Locale.ROOT).padStart(2, '0')
                    } }
                    .joinToString("")
                c.send("bitmap $hex")

                view.findViewById<TextView>(R.id.bitmap_text).text = hex
            }
        }
    }
}
