package com.example.touchreflex.draw.text

import android.graphics.Paint
import android.text.TextPaint
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.utils.FontCache

abstract class InfoText(
    private val parentView: View,
    protected var textSize: Float = 100f
) : CustomDrawable {

    protected val paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    init {
        initPaint()
    }

    private fun initPaint() {
        paint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.white, null)
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        paint.typeface = FontCache.get(R.font.kdamthmorproregular, parentView.context)
        paint.setShadowLayer(5.5f, 6.0f, 6.0f, parentView.resources.getColor(R.color.grey, null))
    }

}