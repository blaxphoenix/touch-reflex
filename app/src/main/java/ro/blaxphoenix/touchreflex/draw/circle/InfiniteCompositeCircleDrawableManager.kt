package ro.blaxphoenix.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.draw.ReflexAnimationCallback
import ro.blaxphoenix.touchreflex.utils.ComponentSizeCache
import ro.blaxphoenix.touchreflex.utils.Utils
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.math.roundToLong

open class InfiniteCompositeCircleDrawableManager(
    private val parentView: View,
    private val callback: ReflexAnimationCallback? = null
) : CustomDrawableManager {

    protected val circles: ConcurrentLinkedDeque<CompositeCircle> = ConcurrentLinkedDeque()
    private val circlesNotDrawn: ConcurrentLinkedDeque<CompositeCircle> = ConcurrentLinkedDeque()
    private val mainHandler = Handler(Looper.getMainLooper())

    var settings: CircleManagerSettings = CircleManagerSettings.EASY
    private var circleDuration: Long = settings.startCircleDuration
    private var circleInterval: Long = settings.startCircleInterval

    var radius: Float = ComponentSizeCache.SizeType.MAX_CIRCLE_RADIUS.size

    @FloatRange(from = .0, to = 360.0)
    private var hue: Float = 0f

    @IntRange(from = 0, to = 255)
    protected var alpha: Int = 0xFF
    private val marginModifier = 1.10f

    override fun init(): CustomDrawableManager {
        circleDuration = settings.startCircleDuration
        circleInterval = settings.startCircleInterval
        postDelayed(
            buildCompositeCircle(),
            250
        )
        for (i in 2 until settings.numberOfCirclesToStartWith) {
            postDelayed(
                buildCompositeCircle(),
                delayMultiplier = i.toFloat() / settings.numberOfCirclesToStartWith,
            )
        }
        return this
    }

    private fun buildCompositeCircle(): CompositeCircle {
        updateHue()

        var x: Float
        var y: Float
        var tries = 0
        do {
            // TODO save the most optimal one in case it goes past the limit to use that
            x = Utils.nextFloat(
                radius * marginModifier,
                parentView.width.toFloat() - (radius * marginModifier)
            )
            y = Utils.nextFloat(
                radius * marginModifier,
                parentView.height.toFloat() - (radius * marginModifier)
            )
            tries++
            // limit tries to not enter a possible infinite loop
        } while (checkIfOverlapping(x, y) && tries < 10)

        return CompositeCircle(
            this,
            parentView,
            x,
            y,
            radius,
            defaultRadius = radius,
            circleDuration,
            hue,
            alpha
        )
    }

    protected open fun updateHue() {
        hue = if (hue < 360) {
            hue + 5
        } else {
            0f
        }
    }

    private fun checkIfOverlapping(x: Float, y: Float): Boolean =
        circles.find {
            Utils.distance(
                x,
                y,
                it.xCenter,
                it.yCenter
                // TODO is this good enough?
            ) <= it.defaultRadius * 4
        } != null

    private fun postDelayed(
        circle: CompositeCircle,
        customDelay: Long? = null,
        delayMultiplier: Float = 1f,
        updateTimers: Boolean = true
    ) {
        val delay = customDelay ?: (Utils.nextLongWithMargin(
            circleInterval,
            circleInterval / 4L
        ) * delayMultiplier).roundToLong()
        circles.add(circle)
        mainHandler.postDelayed({
            if (circles.count { !it.isDisabled } >= Utils.MAX_NUMBER_OF_CIRCLES_AT_ONCE) {
                circlesNotDrawn.add(circle)
            } else {
                circle.onStartDrawing()
            }
            postDelayed(buildCompositeCircle())
        }, delay)
        if (updateTimers) {
            updateTimers()
        }
    }

    protected open fun updateTimers() {
        // TODO remove all debugger println(s)
        // TODO configurable logger?
        if (circleDuration > settings.minCircleDuration) {
            val percentage: Float = settings.minCircleDuration / circleDuration.toFloat() * 100
            val modifier: Long? =
                settings.circleDurationDecelerationMap[percentage.toInt()]
            //println("#### durationPercentage: $percentage")
            //println("#### circleDurationModifier: $modifier")
            val updatedCircleDuration = circleDuration - (circleDuration / modifier!!)
            circleDuration = if (updatedCircleDuration >= settings.minCircleDuration) {
                updatedCircleDuration
            } else {
                settings.minCircleDuration
            }
            //println("#### circleDuration: $circleDuration")
        }
        if (circleInterval > settings.minCircleInterval) {
            val percentage: Float = settings.minCircleInterval / circleInterval.toFloat() * 100
            val modifier: Long? =
                settings.circleIntervalDecelerationMap[percentage.toInt()]
            //println("#### intervalPercentage: $percentage")
            //println("#### circleIntervalModifier: $modifier")
            val updatedCircleInterval = circleInterval - (circleInterval / modifier!!)
            circleInterval = if (updatedCircleInterval >= settings.minCircleInterval) {
                updatedCircleInterval
            } else {
                settings.minCircleInterval
            }
            //println("#### circleInterval: $circleInterval")
        }
        //println("---------")
    }

    override fun onDraw(canvas: Canvas) = circles.forEach { it.onDraw(canvas) }

    override fun onTouch(touchX: Float, touchY: Float) {
        var toRemove: CompositeCircle? = null
        for (circle in circles.descendingIterator()) {
            if (circle.isInBoundary(touchX, touchY)) {
                circle.onDisable()
                toRemove = circle
                break
            }
        }
        toRemove?.let { c1 ->
            callback?.onScored()
            circles.remove(c1)
            circlesNotDrawn.firstOrNull()?.let { c2 ->
                c2.onStartDrawing()
                circlesNotDrawn.remove(c2)
            }
        }
    }

    override fun onPause() {
        pauseCircles()
        mainHandler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        pauseCircles()
        circles.clear()
        circlesNotDrawn.clear()
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun onGameOver(xCenter: Float, yCenter: Float) {
        onPause()
        callback?.onGameOver(xCenter, yCenter)
    }

    private fun pauseCircles() {
        circles.forEach { it.pause() }
        circlesNotDrawn.forEach { it.pause() }
    }

}