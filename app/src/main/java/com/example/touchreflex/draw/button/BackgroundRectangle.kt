package com.example.touchreflex.draw.button

import android.graphics.Canvas
import android.view.View
import com.example.touchreflex.draw.CustomDrawable

class BackgroundRectangle(
    private val parentView: View,
    private val x: Float,
    private val y: Float,
) : CustomDrawable {
    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun onDisable() {}
}