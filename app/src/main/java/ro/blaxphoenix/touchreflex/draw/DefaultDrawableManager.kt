package ro.blaxphoenix.touchreflex.draw

import android.graphics.Canvas
import ro.blaxphoenix.touchreflex.draw.text.InfoText

class DefaultDrawableManager(
    private val elements: ArrayList<InfoText>
) : CustomDrawableManager {
    override fun init(): CustomDrawableManager {
        elements.forEach { it.onStartDrawing() }
        return this
    }

    override fun onDraw(canvas: Canvas) {
        elements.forEach { it.onDraw(canvas) }
    }

    override fun onTouch(touchX: Float, touchY: Float) {}

    override fun onPause() {
        // TODO pause animations?
    }

    override fun onStop() {}
}