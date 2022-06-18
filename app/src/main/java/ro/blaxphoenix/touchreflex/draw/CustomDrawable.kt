package ro.blaxphoenix.touchreflex.draw

import android.graphics.Canvas

interface CustomDrawable {
    fun onStartDrawing()
    fun onDraw(canvas: Canvas)
    fun onDisable()
    fun isInBoundary(touchX: Float, touchY: Float): Boolean
}