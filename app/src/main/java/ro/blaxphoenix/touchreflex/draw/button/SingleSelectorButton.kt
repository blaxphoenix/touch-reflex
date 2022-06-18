package ro.blaxphoenix.touchreflex.draw.button

import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.draw.text.SimpleInfoText

class SingleSelectorButton(
    parentView: View,
    private var centerX: Float,
    private var centerY: Float,
    private val height: Float,
    private val width: Float,
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

    private var rect =
        RectF(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2)
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
        this.isSelected = isSelected
    }

    override fun onStartDrawing() {}

    override fun init(): CustomDrawableManager = this

    override fun onDraw(canvas: Canvas) {
        backgroundRectangle.onDraw(canvas)
        infoText.onDraw(canvas)
    }

    override fun onTouch(touchX: Float, touchY: Float) {
        this.isSelected = !this.isSelected
    }

    override fun onPause() {}

    override fun onStop() {}

    override fun onDisable() {}

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean =
        rect.contains(touchX, touchY)

    fun setNewCoordinates(newCenterX: Float, newCenterY: Float) {
        centerX = newCenterX
        centerY = newCenterY
        rect = RectF(
            centerX - width / 2,
            centerY - height / 2,
            centerX + width / 2,
            centerY + height / 2
        )
        backgroundRectangle.rect = rect
        infoText.setNewCoordinates(centerX, centerY + 40)
    }

}