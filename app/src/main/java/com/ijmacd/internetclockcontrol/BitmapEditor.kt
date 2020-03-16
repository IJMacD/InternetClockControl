package com.ijmacd.internetclockcontrol

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import kotlin.experimental.and

class BitmapEditor @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var value = ByteArray(8)
        set (value) {
            field = value
            invalidate()
        }

    private val off = Paint().apply { color = Color.BLACK }
    private val on = Paint().apply { color = Color.RED }

    private var handler: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas?) {
        val scale = 0.9F
        val ballWidth = width / 8

        for (i in 0 until 8) {
            for (j in 0 until 8) {
                val x = i.toFloat() * ballWidth
                val y = j.toFloat() * ballWidth
                val w = ballWidth * scale

                val paint = if (value[j].toInt() and (0x80 shr i) > 0) on else off

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas?.drawOval(x, y, x + w, y + w, paint)
                } else {
                    canvas?.drawRect(x, y, x + w, y + w, paint)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val i = (event.x / width * 8).toInt()
                val j = (event.y / width * 8).toInt()

                value[j] = (value[j].toInt() xor (0x80 shr i)).toByte()

                invalidate()

                handler?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setOnChangeListener (handler: ()->Unit) {
        this.handler = handler
    }
}