package ro.blaxphoenix.touchreflex.draw.button

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.utils.Utils

class BackgroundRectangle(
    private val parentView: View,
    var rect: RectF,
    @ColorInt color: Int,
    @IntRange(from = 0, to = 255) alpha: Int = 50
) : CustomDrawable {

    @ColorInt
    var color: Int = color
        set(value) {
            field = value
            paintFill.color = value
            paintFill.alpha = alpha
            paintStroke.color = value
        }

    private val paintFill: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintStroke: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var alpha: Int = alpha
        set(value) {
            field = value
            paintFill.alpha = value
        }

    init {
        paintFill.color = color
        paintFill.alpha = alpha
        paintFill.style = Paint.Style.FILL
        resetShadowLayer()
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = 4f
        paintStroke.color = color
    }

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        // TODO round edge radius calc from rect
        canvas.drawRoundRect(rect, 30f, 30f, paintFill)
        canvas.drawRoundRect(rect, 30f, 30f, paintStroke)
    }

    override fun onDisable() {}

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = rect.contains(touchX, touchY)

    fun resetShadowLayer() = Utils.setPaintShadowLayer(paintFill, parentView)

}