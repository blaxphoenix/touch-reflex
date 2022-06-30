package ro.blaxphoenix.touchreflex.draw.button

import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.draw.text.SimpleInfoText
import ro.blaxphoenix.touchreflex.utils.Utils

class SingleSelectorButton(
    parentView: View,
    var centerX: Float,
    var centerY: Float,
    private var width: Float,
    private var height: Float,
    text: String,
    @ColorInt color: Int,
    @ColorInt textColor: Int,
    isSelected: Boolean = false
) : CustomDrawable, CustomDrawableManager {

    @ColorInt
    var color: Int = color
        set(value) {
            field = value
            backgroundRectangle.color = value
        }

    @ColorInt
    var textColor: Int = textColor
        set(value) {
            field = value
            infoText.color = value
        }

    var isSelected: Boolean = isSelected
        set(value) {
            field = value
            backgroundRectangle.alpha = if (value) {
                150
            } else {
                50
            }
        }

    var rect: RectF = buildRectangle()
        private set

    // drawables
    private val backgroundRectangle: BackgroundRectangle =
        BackgroundRectangle(parentView, rect, color)
    private val infoText: SimpleInfoText =
        SimpleInfoText(
            parentView,
            text,
            x = rect.centerX(),
            y = rect.centerY() + 40,
            color = textColor
        )

    init {
        // to call the custom setter
        this.isSelected = isSelected
    }

    private fun buildRectangle(): RectF =
        RectF(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2)

    override fun onDraw(canvas: Canvas) {
        backgroundRectangle.onDraw(canvas)
        infoText.onDraw(canvas)
    }

    override fun onTouch(touchX: Float, touchY: Float) {
        this.isSelected = !this.isSelected
    }

    // TODO clear or impl?
    override fun onStartDrawing() {}
    override fun init(): CustomDrawableManager = this
    override fun onPause() {}
    override fun onStop() {}
    override fun onDisable() {}

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean =
        rect.contains(touchX, touchY)

    fun setNewAttributes(
        newCenterX: Float,
        newCenterY: Float,
        newWidth: Float,
        newHeight: Float,
        newTextSize: Float
    ) {
        centerX = newCenterX
        centerY = newCenterY
        height = newHeight
        width = newWidth
        rect = buildRectangle()
        rect = RectF(
            centerX - width / 2,
            centerY - height / 2,
            centerX + width / 2,
            centerY + height / 2
        )
        backgroundRectangle.rect = rect
        backgroundRectangle.resetShadowLayer()
        infoText.textSize = newTextSize
        infoText.setNewCoordinates(centerX, centerY + newTextSize * .4f)
    }

}