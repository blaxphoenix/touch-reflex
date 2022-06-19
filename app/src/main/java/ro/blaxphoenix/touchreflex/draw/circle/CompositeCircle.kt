package ro.blaxphoenix.touchreflex.draw.circle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import ro.blaxphoenix.touchreflex.draw.CustomDrawable
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.utils.ReverseInterpolator
import ro.blaxphoenix.touchreflex.utils.Utils

class CompositeCircle(
    private val circleManager: CustomDrawableManager,
    private val parentView: View,
    val xCenter: Float,
    val yCenter: Float,
    @FloatRange(from = 0.0, to = Utils.MAX_CIRCLE_RADIUS.toDouble())
    private var radius: Float = Utils.MAX_CIRCLE_RADIUS,
    @FloatRange(from = 0.0, to = Utils.MAX_CIRCLE_RADIUS.toDouble())
    animatorValue: Float,
    private val duration: Long,
    @FloatRange(from = 0.0, to = 360.0)
    private val hue: Float,
    @IntRange(from = 0, to = 255)
    private val alpha: Int = 0xFF
) : CustomDrawable {

    // TODO find a better solution
    var animatorValue: Float = animatorValue
        set(value) {
            field = value
            animator?.setFloatValues(0f, value)
        }
    private val strokeAlpha = alpha / 2
    private val baseSaturation: Float = 0.5f
    private val baseLuminosity: Float = 0.5f

    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var isInverted = false
    private var isDisabled = true
    var isDone = false
        private set

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

    private fun setColors(saturationModifier: Float = 0f, luminosityModifier: Float = 0f) {
        paintFill.color =
            Color.HSVToColor(
                alpha,
                floatArrayOf(
                    hue,
                    baseSaturation + saturationModifier,
                    baseLuminosity + luminosityModifier
                )
            )
        paintStroke.color =
            Color.HSVToColor(
                alpha - strokeAlpha,
                floatArrayOf(
                    hue,
                    baseSaturation + saturationModifier,
                    baseLuminosity + luminosityModifier
                )
            )
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, animatorValue)
        animator?.duration = Utils.nextLongWithMargin(duration)
        animator?.interpolator = AccelerateDecelerateInterpolator()
        // TODO 2 update listeners (isInverted)
        // TODO isInverted = reduce saturation to make the circles go white before disappearing
        animator?.addUpdateListener {
            val value = it.animatedValue as Float
            radius = value
            val percentageSubunit = it.animatedValue as Float / animatorValue
            setColors(
                (1 - baseSaturation) * percentageSubunit,
                (1 - baseLuminosity) * percentageSubunit
            )
            parentView.invalidate()
        }
        // TODO 2 onAnimationEnd listeners (isInverted)
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (isInverted) {
                    if (!isDisabled) {
                        circleManager.onPause()
                        isDone = true
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
        isDisabled = false
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (!isDisabled) {
            canvas.drawCircle(xCenter, yCenter, radius, paintStroke)
            canvas.drawCircle(xCenter, yCenter, radius - (radius * 0.25f), paintFill)
        }
    }

    override fun onDisable() {
        isDisabled = true
        animator?.cancel()
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
    }

    fun pause() = animator?.pause()

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean =
        !isDisabled && Utils.isInBoundaryCircle(touchX, xCenter, touchY, yCenter, radius)

}