package com.example.touchreflex.draw.text

import android.graphics.Paint
import android.text.TextPaint
import android.view.View
import androidx.annotation.ColorInt
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.utils.FontCache

abstract class InfoText(
    private val parentView: View,
    protected var textSize: Float = 100f,
    @ColorInt color: Int
) : CustomDrawable {

    @ColorInt var color: Int = color
        set(value) {
            field = value
            paint.color = value
        }

    protected val paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

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
        paint.setShadowLayer(5.5f, 6.0f, 6.0f, parentView.resources.getColor(R.color.grey, null))
    }

}