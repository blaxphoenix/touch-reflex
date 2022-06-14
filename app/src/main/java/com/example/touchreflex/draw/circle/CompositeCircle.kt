package com.example.touchreflex.draw.circle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.utils.ReverseInterpolator
import com.example.touchreflex.utils.Utils

data class CompositeCircle(
    private val circleManager: CustomDrawableManager,
    private val parentView: View,
    val xCenter: Float,
    val yCenter: Float,
    private var radius: Float,
    private val duration: Long,
    private val hue: Float
) : CustomDrawable {

    val animatorValue: Float = 100f
    private val saturation: Float = 0.5f
    private val luminosity: Float = 0.5f

    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var isInverted = false
    private var isDisabled = false

    init {
        initPaint()
        initAnimator()
    }

    private fun initPaint() {
        paintFill.isDither = true
        paintFill.style = Paint.Style.FILL
        paintStroke.isDither = true
        paintStroke.style = Paint.Style.FILL
        setColors()
    }

    private fun setColors(modifier: Float = 0f) {
        paintFill.color =
            Color.HSVToColor(floatArrayOf(hue, saturation + modifier, luminosity + modifier))
        paintStroke.color =
            Color.HSVToColor(150, floatArrayOf(hue, saturation + modifier, luminosity + modifier))
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, animatorValue)
        animator?.duration = Utils.nextLongWithMargin(duration)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            val modifier = (1 - saturation) * (value / 100)
            radius = value
            setColors(modifier)
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
                    animation?.duration = (Utils.nextLongWithMargin(duration) * 1.5).toLong()
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
        canvas.drawCircle(xCenter, yCenter, radius, paintStroke)
        canvas.drawCircle(xCenter, yCenter, radius - (radius * 0.25f), paintFill)
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

    fun isInBoundary(touchX: Float, touchY: Float): Boolean =
        Utils.isInBoundaryCircle(touchX, xCenter, touchY, yCenter, radius)

}