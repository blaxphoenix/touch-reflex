package com.example.touchreflex.draw.text

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.touchreflex.draw.ReverseInterpolator

class AnimatedInfoText(
    private val parentView: View,
    private val text: String
) : InfoText(parentView) {

    private var animator: ValueAnimator? = null
    private var invert = false

    init {
        this.textSize = 120f
        initAnimator()
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 40f)
        animator?.duration = 1300L
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
    }

    override fun onDisable() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

}