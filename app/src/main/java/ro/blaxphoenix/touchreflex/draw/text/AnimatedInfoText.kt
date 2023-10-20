package ro.blaxphoenix.touchreflex.draw.text

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.utils.ComponentSizeCache
import ro.blaxphoenix.touchreflex.utils.ReverseInterpolator

class AnimatedInfoText(
    private val parentView: View,
    text: String,
    textSize: Float = ComponentSizeCache.SizeType.MAX_DEFAULT_TEXT_SIZE.size,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, text, textSize, color) {

    private var animator: ValueAnimator? = null
    private var invert = false
    private var x: Float? = null
    private var y: Float? = null

    init {
        this.textSize = 120f
        initAnimator()
    }

    // TODO write a separate AnimatedCustomDrawable interface for animator related stuff
    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, textSize * .4f)
        animator?.duration = 1300
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            paint.textSize = textSize + it.animatedValue as Float
            parentView.invalidate()
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (invert) {
                    animation.interpolator = AccelerateDecelerateInterpolator()
                    animation.start()
                    invert = false
                } else {
                    animation.interpolator =
                        ReverseInterpolator(AccelerateDecelerateInterpolator())
                    animation.start()
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
        if (x == null) {
            x = canvas.width / 2f
        }
        y = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, x!!, y!!, paint)
    }

    override fun onDisable() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

    // TODO implement properly
    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

    override fun onTextSizeChanged(textSize: Float) {
        animator?.setFloatValues(0f, textSize * .4f)
    }

}