package ro.blaxphoenix.touchreflex.draw.image

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.draw.text.SimpleInfoText
import kotlin.math.roundToInt

class ImageAndText(
    parentView: View,
    drawable: Drawable,
    // TODO nullables?
    var centerX: Float,
    var centerY: Float,
    var width: Int,
    var height: Int,
    text: String,
    @ColorInt color: Int,
) : CustomDrawable, CustomDrawableManager {

    // TODO double check which field needs to be private and public
    private val distanceModifier: Float = .3f

    var text: String = text
        set(value) {
            field = value
            infoText.text = value
            // TODO recalculate centerX and things
            computeAttributes()
        }

    @ColorInt
    var color: Int = color
        set(value) {
            field = value
            image.color = color
            infoText.color = color
        }

    // drawables
    private val image: SimpleImage =
        SimpleImage(
            drawable,
            color,
            centerX.roundToInt(), centerY.roundToInt(), width, height
        )
    val infoText: SimpleInfoText =
        SimpleInfoText(
            parentView,
            text,
            color = color
        )

    init {
        infoText.paint.textAlign = Paint.Align.LEFT
    }

    override fun onDraw(canvas: Canvas) {
        image.onDraw(canvas)
        infoText.onDraw(canvas)
    }

    fun setNewAttributes(
        newCenterX: Float,
        newCenterY: Float,
        newWidth: Float,
        newHeight: Float,
        newTextSize: Float
    ) {
        infoText.textSize = newTextSize
        centerX = newCenterX
        centerY = newCenterY
        width = newWidth.roundToInt()
        height = newHeight.roundToInt()
        computeAttributes()
    }

    private fun computeAttributes() {
        val distance = (width * distanceModifier)
        val totalWidth = width + distance + infoText.measureTextLength()
        // TODO text Y consider paint ascent/descent
        val textBounds = Rect()
        infoText.paint.getTextBounds(text, 0, text.length, textBounds)
        infoText.setNewCoordinates(
            centerX - (totalWidth / 2) + width + distance,
            centerY + (textBounds.height() / 2f)
        )
        image.setNewSize(
            (centerX - (totalWidth / 2)).roundToInt(),
            (centerY - (height / 2f)).roundToInt(),
            width, height
        )
    }

    // TODO clear or impl?
    override fun init(): CustomDrawableManager = this
    override fun onStartDrawing() {}
    override fun onDisable() {}
    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false
    override fun onTouch(touchX: Float, touchY: Float) {}
    override fun onPause() {}
    override fun onStop() {}
}