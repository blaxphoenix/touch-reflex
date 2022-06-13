package com.example.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View

class SimpleInfoText(
    parentView: View,
    var text: String,
    var isIgnored: Boolean = false,
    private var x: Float? = null,
    private var y: Float? = null,
    textSize: Float = 100f
) : InfoText(parentView, textSize) {

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        if (!isIgnored) {
            if (x != null && y != null) {
                canvas.drawText(text, x!!, y!!, paint)
            } else {
                val xPos = canvas.width / 2f
                val yPos = textSize - ((paint.descent() + paint.ascent()) / 2)
                x = xPos
                y = yPos
                canvas.drawText(text, xPos, yPos, paint)
            }
        }
    }

    override fun onDisable() {}

}