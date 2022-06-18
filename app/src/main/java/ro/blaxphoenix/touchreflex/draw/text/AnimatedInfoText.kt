package ro.blaxphoenix.touchreflex.draw.text

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.utils.ReverseInterpolator
import ro.blaxphoenix.touchreflex.utils.Utils

class AnimatedInfoText(
    private val parentView: View,
    private val text: String,
    @FloatRange(from = 0.0, to = Utils.MAX_DEFAULT_TEXT_SIZE.toDouble())
    textSize: Float = Utils.MAX_DEFAULT_TEXT_SIZE,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, textSize, color) {

    private var animator: ValueAnimator? = null
    private var invert = false
    private var x: Float? = null
    private var y: Float? = null

    init {
        this.textSize = 120f
        initAnimator()
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, textSize * 0.4f)
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
        if (x != null && y != null) {
            canvas.drawText(text, x!!, y!!, paint)
        } else {
            val xPos = canvas.width / 2f
            val yPos = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2)
            x = xPos
            y = yPos
            canvas.drawText(text, xPos, yPos, paint)
        }
    }

    override fun onDisable() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

    // TODO implement properly
    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

    override fun onTextSizeChanged(textSize: Float) {
        animator?.setFloatValues(0f, textSize * 0.4f)
    }

}