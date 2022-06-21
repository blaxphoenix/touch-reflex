@file:Suppress("MemberVisibilityCanBePrivate")

package ro.blaxphoenix.touchreflex.draw.text

import android.graphics.Canvas
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.utils.Utils

class SimpleInfoText(
    parentView: View,
    var text: String,
    var isIgnored: Boolean = false,
    x: Float? = null,
    y: Float? = null,
    @FloatRange(from = 0.0, to = Utils.MAX_DEFAULT_TEXT_SIZE.toDouble())
    textSize: Float = Utils.MAX_DEFAULT_TEXT_SIZE,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, textSize, color) {

    var x: Float? = x
        private set
    var y: Float? = y
        private set

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        if (!isIgnored) {
            if (x != null && y != null) {
                canvas.drawText(text, x!!, y!!, paint)
            } else {
                x = canvas.width / 2f
                canvas.drawText(text, x!!, this.y!!, paint)
            }
        }
    }

    override fun onDisable() {}

    fun setNewCoordinates(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

    // TODO find a better solution or good enough?
    override fun onTextSizeChanged(textSize: Float) {
        y = textSize - ((paint.descent() + paint.ascent()) / 2)
    }

}