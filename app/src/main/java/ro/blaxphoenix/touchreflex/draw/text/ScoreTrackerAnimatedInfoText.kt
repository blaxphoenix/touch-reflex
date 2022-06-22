package ro.blaxphoenix.touchreflex.draw.text

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.utils.Utils
import kotlin.math.roundToInt

class ScoreTrackerAnimatedInfoText(
    private val parentView: View,
    text: String,
    private var x: Float? = null,
    private var y: Float? = null,
    @FloatRange(from = .0, to = Utils.MAX_DEFAULT_TEXT_SIZE.toDouble())
    textSize: Float = Utils.MAX_DEFAULT_TEXT_SIZE,
    @ColorInt color: Int = ResourcesCompat.getColor(parentView.resources, R.color.white, null)
) : InfoText(parentView, text, textSize, color) {

    private var animator: ValueAnimator? = null
    private var animatorValue = textSize

    @IntRange(from = 0, to = 255)
    private val alpha: Int = 255

    init {
        this.textSize = 120f
        initAnimator()
        paint.color = Color.argb(alpha, color.red, color.green, color.blue)
        paint.clearShadowLayer()
    }

    // TODO write a separate AnimatedCustomDrawable interface for animator related stuff
    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, animatorValue)
        animator?.duration = 750
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.addUpdateListener {
            val percentageSubunit = 1 - it.animatedValue as Float / animatorValue
            paint.textSize = textSize + it.animatedValue as Float
            paint.color = Color.argb(
                (alpha * percentageSubunit).roundToInt(),
                color.red,
                color.green,
                color.blue
            )
            parentView.invalidate()
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                paint.textSize = textSize
                paint.color = Color.argb(alpha, color.red, color.green, color.blue)
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
        y = textSize - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, x!!, y!!, paint)
    }

    override fun onDisable() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean = false

    override fun onTextSizeChanged(textSize: Float) {
        animatorValue = textSize
        animator?.setFloatValues(0f, animatorValue)
    }

}