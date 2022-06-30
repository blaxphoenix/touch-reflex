@file:Suppress("MemberVisibilityCanBePrivate")

package ro.blaxphoenix.touchreflex.draw.text

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.utils.ComponentSizeCache
import ro.blaxphoenix.touchreflex.utils.Utils

class SimpleInfoText(
    parentView: View,
    text: String,
    // TODO move to someplace more common place (interface, abstract etc.)
    var isIgnored: Boolean = false,
    x: Float? = null,
    y: Float? = null,
    textSize: Float = ComponentSizeCache.SizeType.MAX_DEFAULT_TEXT_SIZE.size,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, text, textSize, color) {

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

    override fun onDisable() {
        // TODO isIgnored set on onDisable() -> needs an onEnable() too?
    }

    fun setNewCoordinates(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

    // TODO find a better solution or good enough?
    override fun onTextSizeChanged(textSize: Float) {
        if (paint.textAlign == Paint.Align.CENTER) {
            y = textSize - ((paint.descent() + paint.ascent()) / 2)
        }
    }

}