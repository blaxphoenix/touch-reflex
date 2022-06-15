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
    private val radius: Float = 90f
    private val startCircleDuration: Long = 3500L
    private val startCircleInterval: Long = 1750L
    private val minCircleDuration: Long = 2250L
    private val minCircleInterval: Long = 750L
    private var circleDuration: Long = startCircleDuration
    private var circleInterval: Long = startCircleInterval
    private var hue: Float = 0f

    protected var alpha: Int = 0xFF
    private val marginModifier = 1.10f

    override fun init(): CustomDrawableManager {
        circleDuration = startCircleDuration
        circleInterval = startCircleInterval
        postDelayed(buildCompositeCircle(), initialDelay = 250)
        postDelayed(buildCompositeCircle(), false, 250, startCircleInterval)
        postDelayed(buildCompositeCircle(), false, 250, startCircleInterval * 2)
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
        if (circleDuration > minCircleDuration) {
            var modifier = 80L
            if (minCircleDuration / circleDuration <= 0.5) {
                modifier = 120L
            }
            val updatedCircleDuration = circleDuration - (circleDuration / modifier)
            circleDuration = if (updatedCircleDuration >= minCircleDuration) {
                updatedCircleDuration
            } else {
                minCircleDuration
            }
        }
        if (circleInterval > minCircleInterval) {
            var modifier = 60L
            if (minCircleInterval / circleInterval <= 0.5) {
                modifier = 120L
            }
            val updatedCircleInterval = circleInterval - (circleInterval / modifier)
            circleInterval = if (updatedCircleInterval >= minCircleInterval) {
                updatedCircleInterval
            } else {
                minCircleInterval
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