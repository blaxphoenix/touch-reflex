package ro.blaxphoenix.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import ro.blaxphoenix.touchreflex.db.GameMode
import ro.blaxphoenix.touchreflex.draw.CustomDrawableManager
import ro.blaxphoenix.touchreflex.draw.ReflexAnimationCallback
import ro.blaxphoenix.touchreflex.utils.Utils
import java.util.concurrent.ConcurrentLinkedDeque

open class InfiniteCompositeCircleDrawableManager(
    private val parentView: View,
    private val callback: ReflexAnimationCallback? = null
) : CustomDrawableManager {

    protected val circles: ConcurrentLinkedDeque<CompositeCircle> = ConcurrentLinkedDeque()
    private val mainHandler = Handler(Looper.getMainLooper())

    var settings: CircleManagerSettings = CircleManagerSettings.EASY
    private var circleDuration: Long = settings.startCircleDuration
    private var circleInterval: Long = settings.startCircleInterval

    @FloatRange(from = 0.0, to = Utils.MAX_CIRCLE_RADIUS.toDouble())
    var radius: Float = Utils.MAX_CIRCLE_RADIUS

    @FloatRange(from = 0.0, to = 360.0)
    private var hue: Float = 0f

    @IntRange(from = 0, to = 255)
    protected var alpha: Int = 0xFF
    private val marginModifier = 1.10f

    override fun init(): CustomDrawableManager {
        circleDuration = settings.startCircleDuration
        circleInterval = settings.startCircleInterval
        postDelayed(buildCompositeCircle(), initialDelay = 250)
        if (settings.gameMode == GameMode.EASY) {
            postDelayed(buildCompositeCircle(), false, 250, settings.startCircleInterval)
        }
        postDelayed(buildCompositeCircle(), false, 250, settings.startCircleInterval * 2)
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
            animatorValue = radius,
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
            ) <= it.animatorValue * 4
        } != null

    private fun postDelayed(
        circle: CompositeCircle,
        updateTimers: Boolean = true,
        initialDelay: Long? = null,
        extraDelay: Long = 0L
    ) {
        val delay = initialDelay ?: Utils.nextLongWithMargin(circleInterval, circleInterval / 3L)
        circles.add(circle)
        mainHandler.postDelayed({
            circle.onStartDrawing()
            postDelayed(buildCompositeCircle())
        }, extraDelay + delay)
        if (updateTimers) {
            updateTimers()
        }
    }

    protected open fun updateTimers() {
        if (circleDuration > settings.minCircleDuration) {
            val percentage: Float = settings.minCircleDuration / circleDuration.toFloat() * 100
            println("durationPercentage: $percentage% = (${settings.minCircleDuration} / ${circleDuration.toFloat()} * 100)")
            val modifier: Long? =
                settings.circleDurationDecelerationMap[percentage.toInt()]
            println("durationModifier: $modifier")
            val updatedCircleDuration = circleDuration - (circleDuration / modifier!!)
            circleDuration = if (updatedCircleDuration >= settings.minCircleDuration) {
                updatedCircleDuration
            } else {
                settings.minCircleDuration
            }
            println("updatedCircleDuration: $updatedCircleDuration")
        }
        if (circleInterval > settings.minCircleInterval) {
            val percentage: Float = settings.minCircleInterval / circleInterval.toFloat() * 100
            println("intervalPercentage: $percentage% = (${settings.minCircleInterval} / ${circleInterval.toFloat()} * 100)")
            val modifier: Long? = settings.circleIntervalDecelerationMap[percentage.toInt()]
            println("intervalModifier: $modifier")
            val updatedCircleInterval = circleInterval - (circleInterval / modifier!!)
            circleInterval = if (updatedCircleInterval >= settings.minCircleInterval) {
                updatedCircleInterval
            } else {
                settings.minCircleInterval
            }
            println("updatedCircleInterval: $updatedCircleInterval")
        }
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
        toRemove?.let {
            callback?.onScored()
            circles.remove(it)
        }
    }

    override fun onPause() {
        pauseCircles()
        mainHandler.removeCallbacksAndMessages(null)
        callback?.onGameOver()
    }

    override fun onStop() {
        pauseCircles()
        circles.clear()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun pauseCircles() = circles.forEach { it.pause() }

}