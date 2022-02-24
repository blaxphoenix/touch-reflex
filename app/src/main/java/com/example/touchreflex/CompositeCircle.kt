package com.example.touchreflex

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat

data class CompositeCircle(
    val parentView: View,
    val x: Float,
    val y: Float,
    var r: Float,
    val duration: Long = 1000L
) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    init {
        fillPaint.color = ResourcesCompat.getColor(parentView.resources, R.color.colorCircleFill, null)
        fillPaint.isDither = true
        fillPaint.style = Paint.Style.FILL
        strokePaint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.colorCircleStroke, null)
        strokePaint.isDither = true
        strokePaint.style = Paint.Style.FILL
    }

    fun startDrawing() {
        animator = ValueAnimator.ofFloat(0f, 100f)
        animator?.duration = Utils.nextLongWithMargin(duration)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            r = it.animatedValue as Float
            parentView.invalidate()
        }
        animator?.start()
    }

    fun onDraw(canvas: Canvas) {
        canvas.drawCircle(x, y, r, strokePaint)
        canvas.drawCircle(x, y, r  - (r * 0.25f), fillPaint)
        parentView.invalidate()
    }

    fun isInBoundary(touchX: Float, touchY: Float): Boolean {
        return ((touchX - x) * (touchX - x)) + ((touchY - y) * (touchY - y)) <= r * r
    }

    fun disable() {
        animator?.removeAllUpdateListeners()
        animator?.cancel()
    }

}