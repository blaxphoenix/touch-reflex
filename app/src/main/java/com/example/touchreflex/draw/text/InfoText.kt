package com.example.touchreflex.draw.text

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawable
import com.example.touchreflex.draw.ReverseInterpolator

class InfoText(
    private val parentView: View,
    private val text: String
) : CustomDrawable {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textSize = 100f
    private var animator: ValueAnimator? = null
    private var invert = false

    init {
        initPaint()
        initAnimator()
    }

    private fun initPaint() {
        paint.color =
            ResourcesCompat.getColor(parentView.resources, R.color.colorCircleFill, null)
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 70f)
        animator?.duration = 1000L
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            paint.textSize = textSize + it.animatedValue as Float
            parentView.invalidate()
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (invert) {
                    animation?.interpolator = AccelerateDecelerateInterpolator()
                    animation?.start()
                    invert = false
                } else {
                    animation?.interpolator =
                        ReverseInterpolator(AccelerateDecelerateInterpolator())
                    animation?.start()
                    invert = true
                }
            }
        })
    }

    override fun onStartDrawing() {
        paint.textSize = textSize
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, xPos, yPos, paint)
        parentView.invalidate()
    }

    override fun onDisable() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

}