package com.example.touchreflex.draw.circle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.example.touchreflex.R
import com.example.touchreflex.draw.ReverseInterpolator
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.utils.Utils

data class CompositeCircle(
    val circleManager: CustomDrawableManager,
    val parentView: View,
    val x: Float,
    val y: Float,
    var r: Float,
    val duration: Long
) : CustomDrawable {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var isInverted = false
    private var isDisabled = false

    init {
        initPaint()
        initAnimator()
    }

    private fun initPaint() {
        fillPaint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.circle_fill, null)
        fillPaint.isDither = true
        fillPaint.style = Paint.Style.FILL
        strokePaint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.circle_stroke, null)
        strokePaint.isDither = true
        strokePaint.style = Paint.Style.FILL
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 100f)
        animator?.duration = Utils.nextLongWithMargin(duration)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            r = it.animatedValue as Float
            parentView.invalidate()
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (isInverted) {
                    if (!isDisabled) {
                        circleManager.onPause()
                    }
                } else {
                    animation?.interpolator =
                        ReverseInterpolator(AccelerateDecelerateInterpolator())
                    animation?.start()
                    isInverted = true
                }
            }
        })
    }

    override fun onStartDrawing() {
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(x, y, r, strokePaint)
        canvas.drawCircle(x, y, r - (r * 0.25f), fillPaint)
        parentView.invalidate()
    }

    override fun onDisable() {
        isDisabled = true
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

    fun pause() {
        animator?.pause()
    }

    fun isInBoundary(touchX: Float, touchY: Float): Boolean {
        return ((touchX - x) * (touchX - x)) + ((touchY - y) * (touchY - y)) <= r * r
    }

}