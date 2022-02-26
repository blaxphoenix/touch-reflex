package com.example.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View

class SimpleScoreInfoText(
    private val parentView: View,
    var text: String
) : InfoText(parentView) {

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        val xPos = canvas.width / 2f
        val yPos = textSize - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, xPos, yPos, paint)
        parentView.invalidate()
    }

    override fun onDisable() {}

}