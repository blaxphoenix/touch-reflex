package com.example.touchreflex.draw.text

import android.graphics.Paint
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawable

abstract class InfoText(
    private val parentView: View
) : CustomDrawable {

    protected val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val textSize: Float = 100f

    init {
        initPaint()
    }

    private fun initPaint() {
        paint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.colorCircleFill, null)
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        paint.setShadowLayer(5.5f, 6.0f, 6.0f, parentView.resources.getColor(R.color.black, null))
    }

}