package ro.blaxphoenix.touchreflex.draw.text

import android.graphics.Paint
import android.text.TextPaint
import android.view.View
import androidx.annotation.ColorInt
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.utils.FontCache
import ro.blaxphoenix.touchreflex.utils.Utils

abstract class InfoText(
    private val parentView: View,
    var text: String,
    textSize: Float,
    @ColorInt color: Int
) : CustomDrawable {

    var textSize: Float = textSize
        set(value) {
            field = value
            paint.textSize = textSize
            onTextSizeChanged(textSize)
            resetShadowLayer()
        }

    @ColorInt
    var color: Int = color
        set(value) {
            field = value
            paint.color = value
        }

    val paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    init {
        initPaint()
    }

    private fun initPaint() {
        paint.color = color
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        paint.typeface = FontCache.get(R.font.kdamthmorproregular, parentView.context)
        resetShadowLayer()
    }

    open fun onTextSizeChanged(textSize: Float) {}

    fun measureTextLength(customText: String? = null) =
        if (customText != null) {
            paint.measureText(customText)
        } else {
            paint.measureText(text)
        }

    private fun resetShadowLayer() = Utils.setPaintShadowLayer(paint, parentView)

}