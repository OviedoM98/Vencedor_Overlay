// MovableButton.kt
package com.example.overlaybuttons

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.widget.Button

class MovableButton(context: Context, attrs: AttributeSet?) : Button(context, attrs) {
    init {
        val green = Color.parseColor("#428106")
        background = context.getDrawable(R.drawable.circle_bg)?.apply {
            setTint(green)
        }
        setTextColor(Color.WHITE)
    }
}
