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
    private val circleManager: CustomDrawableManager,
    private val parentView: View,
    private val xCenter: Float,
    private val yCenter: Float,
    private var radius: Float,
    private val duration: Long,
    private val colorFill: Int? = null,
    private val colorStroke: Int? = null
) : CustomDrawable {

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
        initColors()
    }

    private fun initColors() {
        paintFill.color =
            colorFill ?: ResourcesCompat.getColor(
                parentView.resources, R.color.circle_fill, null
            )
        paintStroke.color =
            colorStroke ?: ResourcesCompat.getColor(
                parentView.resources, R.color.circle_stroke, null
            )
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 100f)
        animator?.duration = Utils.nextLongWithMargin(duration)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            radius = it.animatedValue as Float
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

    fun isInBoundary(touchX: Float, touchY: Float): Boolean {
        return ((touchX - xCenter) * (touchX - xCenter)) + ((touchY - yCenter) * (touchY - yCenter)) <= radius * radius
    }

}