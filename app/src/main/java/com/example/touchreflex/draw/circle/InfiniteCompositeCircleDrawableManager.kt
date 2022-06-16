package com.example.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.utils.Utils
import java.util.concurrent.ConcurrentLinkedDeque

open class InfiniteCompositeCircleDrawableManager(
    private val parentView: View,
    private val callback: ReflexAnimationCallback? = null
) : CustomDrawableManager {

    protected val circles: ConcurrentLinkedDeque<CompositeCircle> = ConcurrentLinkedDeque()
    private val mainHandler = Handler(Looper.getMainLooper())

    var settings: CircleManagerSettings = CircleManagerSettings.DEFAULT
    private var circleDuration: Long = settings.startCircleDuration
    private var circleInterval: Long = settings.startCircleInterval

    private val radius: Float = 90f
    private var hue: Float = 0f
    protected var alpha: Int = 0xFF
    private val marginModifier = 1.10f

    override fun init(): CustomDrawableManager {
        circleDuration = settings.startCircleDuration
        circleInterval = settings.startCircleInterval
        postDelayed(buildCompositeCircle(), initialDelay = 250)
        postDelayed(buildCompositeCircle(), false, 250, settings.startCircleInterval)
        postDelayed(buildCompositeCircle(), false, 250, settings.startCircleInterval * 2)
        return this
    }

    private fun buildCompositeCircle(): CompositeCircle {
        updateHue()

        var x: Float
        var y: Float
        var tries = 0
        do {
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
            circleDuration,
            hue,
            alpha
        )
    }

    private fun updateHue() {
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

    private fun updateTimers() {
        if (circleDuration > settings.minCircleDuration) {
            var modifier = settings.circleDurationModifier1
            if (settings.minCircleDuration / circleDuration <= 0.5) {
                modifier = settings.circleDurationModifier2
            }
            val updatedCircleDuration = circleDuration - (circleDuration / modifier)
            circleDuration = if (updatedCircleDuration >= settings.minCircleDuration) {
                updatedCircleDuration
            } else {
                settings.minCircleDuration
            }
        }
        if (circleInterval > settings.minCircleInterval) {
            var modifier = settings.circleIntervalModifier1
            if (settings.minCircleInterval / circleInterval <= 0.5) {
                modifier = settings.circleIntervalModifier2
            }
            val updatedCircleInterval = circleInterval - (circleInterval / modifier)
            circleInterval = if (updatedCircleInterval >= settings.minCircleInterval) {
                updatedCircleInterval
            } else {
                settings.minCircleInterval
            }
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
        if (toRemove != null) {
            callback?.onScored()
            circles.remove(toRemove)
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