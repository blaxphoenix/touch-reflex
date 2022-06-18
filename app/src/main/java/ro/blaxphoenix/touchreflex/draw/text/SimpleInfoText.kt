package ro.blaxphoenix.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import ro.blaxphoenix.touchreflex.R

class SimpleInfoText(
    parentView: View,
    var text: String,
    var isIgnored: Boolean = false,
    private var x: Float? = null,
    private var y: Float? = null,
    textSize: Float = 100f,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, textSize, color) {

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

    fun setNewCoordinates(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

}