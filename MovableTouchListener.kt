// MovableTouchListener.kt
package com.example.overlaybuttons

import android.view.*
import android.widget.FrameLayout

class MovableTouchListener(
    private val container: FrameLayout,
    private val windowManager: WindowManager
) : View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val layoutParams = container.layoutParams as WindowManager.LayoutParams

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = event.rawX - layoutParams.x
                dY = event.rawY - layoutParams.y
            }
            MotionEvent.ACTION_MOVE -> {
                layoutParams.x = (event.rawX - dX).toInt()
                layoutParams.y = (event.rawY - dY).toInt()
                windowManager.updateViewLayout(container, layoutParams)
            }
        }
        return true
    }
}
