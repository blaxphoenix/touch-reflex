package com.example.touchreflex.draw.button

import android.graphics.Canvas
import com.example.touchreflex.draw.CustomDrawableManager

class SingleSelectorButtonDrawableManager(
    private val elements: ArrayList<SingleSelectorButton>
) : CustomDrawableManager {
    override fun init(): CustomDrawableManager {
        elements.forEach { it.onStartDrawing() }
        return this
    }

    override fun onDraw(canvas: Canvas) = elements.forEach { it.onDraw(canvas) }

    override fun onTouch(touchX: Float, touchY: Float) {
        // TODO do the button switching logic here?
    }

    override fun onPause() {}

    override fun onStop() {}
}