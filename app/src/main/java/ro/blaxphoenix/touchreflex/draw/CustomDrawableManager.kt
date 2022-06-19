package ro.blaxphoenix.touchreflex.draw

import android.graphics.Canvas

interface CustomDrawableManager {
    fun init(): CustomDrawableManager
    fun onDraw(canvas: Canvas)
    fun onTouch(touchX: Float, touchY: Float)
    fun onPause()
    fun onStop()
}