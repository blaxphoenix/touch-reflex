package com.example.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View
import com.example.touchreflex.draw.CustomDrawableManager

class InfoTextDrawableManager(
    parentView: View,
    text: String
) : CustomDrawableManager {

    private val infoText: InfoText = InfoText(parentView, text)

    override fun init(): CustomDrawableManager {
        infoText.onStartDrawing()
        return this
    }

    override fun onDraw(canvas: Canvas) {
        infoText.onDraw(canvas)
    }

    override fun onTouch(touchX: Float, touchY: Float) {}

    override fun onPause() {}

    override fun onStop() {}

}