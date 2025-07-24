// OverlayService.kt
package com.example.overlaybuttons

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.*
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var container: FrameLayout
    private val handler = Handler(Looper.getMainLooper())

    private var brightness = 0.5f
    private val minBrightness = 0.3f
    private val step = 0.1f

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        container = FrameLayout(this).apply {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.START
            windowManager.addView(this, params)
        }

        val overlayView = LayoutInflater.from(this)
            .inflate(R.layout.overlay_layout, container, false)
        container.addView(overlayView)

        val minusBtn = overlayView.findViewById<MovableButton>(R.id.minusBtn)
        val wifiBtn = overlayView.findViewById<MovableButton>(R.id.wifiBtn)
        val plusBtn = overlayView.findViewById<MovableButton>(R.id.plusBtn)
        val infoTv = overlayView.findViewById<TextView>(R.id.infoTv)

        val moveListener = MovableTouchListener(container, windowManager)

        listOf(minusBtn, wifiBtn, plusBtn).forEach {
            it.setOnTouchListener { v, ev ->
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> v.scaleX = 1.25f.also { v.scaleY = it }
                    MotionEvent.ACTION_UP -> v.scaleX = 1f.also { v.scaleY = it }
                }
                moveListener.onTouch(v, ev)
            }
        }

        minusBtn.setOnClickListener {
            brightness = (brightness - step).coerceAtLeast(minBrightness)
            Settings.System.putFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }

        plusBtn.setOnClickListener {
            brightness = (brightness + step).coerceAtMost(1f)
            Settings.System.putFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }

        wifiBtn.setOnClickListener {
            Thread {
                Settings.Global.putInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 1)
                val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                intent.putExtra("state", true)
                sendBroadcast(intent)

                for (i in 7 downTo 1) {
                    handler.post { infoTv.text = "Reiniciando Red Telcel: $i" }
                    Thread.sleep(1000)
                }

                Settings.Global.putInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
                val intent2 = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                intent2.putExtra("state", false)
                sendBroadcast(intent2)

                handler.post { infoTv.text = "" }
            }.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(container)
    }
}
