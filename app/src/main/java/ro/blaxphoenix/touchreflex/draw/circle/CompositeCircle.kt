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
import ro.blaxphoenix.touchreflex.utils.ComponentSizeCache
import ro.blaxphoenix.touchreflex.utils.ReverseInterpolator
import ro.blaxphoenix.touchreflex.utils.Utils
import kotlin.math.max

class CompositeCircle(
    private val circleManager: InfiniteCompositeCircleDrawableManager,
    private val parentView: View,
    val xCenter: Float,
    val yCenter: Float,
    private var radius: Float = ComponentSizeCache.SizeType.MAX_CIRCLE_RADIUS.size,
    defaultRadius: Float,
    private val duration: Long,
    @FloatRange(from = .0, to = 360.0)
    private val hue: Float,
    @IntRange(from = 0, to = 255)
    private val alpha: Int = 0xFF
) : CustomDrawable {

    var defaultRadius: Float = defaultRadius
        set(value) {
            field = value
            animator?.setFloatValues(0f, value)
        }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        val value = it.animatedValue as Float
        radius = value
        val percentageSubunit = it.animatedValue as Float / defaultRadius
        setColors(
            (1 - baseSaturation) * percentageSubunit,
            (1 - baseLuminosity) * percentageSubunit
        )
        parentView.invalidate()
    }
    private val updateListenerInverted = ValueAnimator.AnimatorUpdateListener {
        val value = it.animatedValue as Float
        radius = value
        val percentageSubunit = it.animatedValue as Float / defaultRadius
        setColors(
            customSaturation = percentageSubunit,
            customLuminosity = 1f
        )
        parentView.invalidate()
    }
    private val onAnimationEndListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            isInverted = true
            initAnimator(Utils.nextLongWithMargin(duration) * 3)
            animator?.start()
        }
    }
    private val onAnimationEndListenerInverted = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            if (!isDisabled) {
                circleManager.onGameOver(xCenter, yCenter)
                isDone = true
            }
        }
    }

    private val strokeAlpha = alpha / 2
    private val baseSaturation: Float = .5f
    private val baseLuminosity: Float = .5f

    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var isInverted = false
    var isDisabled = true
        private set
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

    private fun setColors(
        saturationModifier: Float = 0f,
        luminosityModifier: Float = 0f,
        customSaturation: Float? = null,
        customLuminosity: Float? = null
    ) {
        paintFill.color =
            Color.HSVToColor(
                alpha,
                floatArrayOf(
                    hue,
                    customSaturation ?: (baseSaturation + saturationModifier),
                    customLuminosity ?: (baseLuminosity + luminosityModifier)
                )
            )
        paintStroke.color =
            Color.HSVToColor(
                alpha - strokeAlpha,
                floatArrayOf(
                    hue,
                    customSaturation ?: (baseSaturation + saturationModifier),
                    customLuminosity ?: (baseLuminosity + luminosityModifier)
                )
            )
    }

    private fun initAnimator(duration: Long = this.duration) {
        animator = ValueAnimator.ofFloat(0f, defaultRadius)
        animator?.duration = Utils.nextLongWithMargin(duration)
        if (!isInverted) {
            animator?.interpolator = AccelerateDecelerateInterpolator()
            animator?.addUpdateListener(updateListener)
            animator?.addListener(onAnimationEndListener)
        } else {
            animator?.interpolator = ReverseInterpolator(AccelerateDecelerateInterpolator())
            animator?.addUpdateListener(updateListenerInverted)
            animator?.addListener(onAnimationEndListenerInverted)
        }
    }

    override fun onStartDrawing() {
        isDisabled = false
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (!isDisabled) {
            canvas.drawCircle(xCenter, yCenter, radius, paintStroke)
            canvas.drawCircle(xCenter, yCenter, radius - (radius * .25f), paintFill)
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
        !isDisabled && Utils.isInBoundaryCircle(
            touchX,
            touchY,
            xCenter,
            yCenter,
            // have a minimum radius considered for easier clicking when circle is too small
            max(radius, defaultRadius / 3)
        )

}