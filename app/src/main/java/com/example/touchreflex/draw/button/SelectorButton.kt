package com.example.touchreflex.draw.button

import android.graphics.Canvas
import android.view.View
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.draw.CustomDrawableManager

class SelectorButton(
    private val parentView: View,
    private val x: Float,
    private val y: Float,
    private val text: String,
    private var isSelected: Boolean = false
) : CustomDrawable, CustomDrawableManager {
    override fun onStartDrawing() {}
    override fun init(): CustomDrawableManager {
        TODO("Not yet implemented")
    }

    override fun onDraw(canvas: Canvas) {
    }

    override fun onTouch(touchX: Float, touchY: Float) {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

    override fun onDisable() {}


}