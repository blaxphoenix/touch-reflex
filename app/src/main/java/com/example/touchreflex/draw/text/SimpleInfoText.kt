package com.example.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View

class SimpleInfoText(
    parentView: View,
    var text: String,
    var isIgnored: Boolean = false,
    private val x: Float? = null,
    private val y: Float? = null
) : InfoText(parentView) {

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        if (!isIgnored) {
            if (x != null && y != null) {
                canvas.drawText(text, x, y, paint)
            } else {
                val xPos = canvas.width / 2f
                val yPos = textSize - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText(text, xPos, yPos, paint)
            }
        }
    }

    override fun onDisable() {}

}