package com.example.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.utils.Utils
import java.util.*

class InfiniteCompositeCircleDrawableManager(
    private val parentView: View,
    private val callback: ReflexAnimationCallback? = null
) : CustomDrawableManager {

    private val circles: LinkedList<CompositeCircle> = LinkedList()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val radius: Float = 80f
    private val startCircleDuration: Long = 2000L
    private val startCircleInterval: Long = 2000L
    private val minCircleDuration: Long = 1000L
    private val minCircleInterval: Long = 1150L
    private var circleDuration: Long = startCircleDuration
    private var circleInterval: Long = startCircleInterval
    private var hue: Float = 0f

    override fun init(): CustomDrawableManager {
        circleDuration = startCircleDuration
        circleInterval = startCircleInterval
        postDelayed(buildCompositeCircle(), initialDelay = 250)
        postDelayed(buildCompositeCircle(), false, 250, startCircleInterval / 2)
        postDelayed(buildCompositeCircle(), false, 250, startCircleInterval / 4)
        return this
    }

    private fun buildCompositeCircle(): CompositeCircle {
        updateHue()
        return CompositeCircle(
            this,
            parentView,
            Utils.nextFloat(radius, parentView.width.toFloat() - radius),
            Utils.nextFloat(radius, parentView.height.toFloat() - radius),
            radius,
            circleDuration,
            hue
        )
    }

    private fun updateHue() {
        hue = if (hue < 360) {
            hue + 5
        } else {
            0f
        }
    }

    private fun postDelayed(
        circle: CompositeCircle,
        updateTimers: Boolean = true,
        initialDelay: Long? = null,
        extraDelay: Long = 0L
    ) {
        val delay = initialDelay ?: Utils.nextLongWithMargin(circleInterval, circleInterval / 3L)
        mainHandler.postDelayed({
            circles.add(circle)
            circle.onStartDrawing()
            postDelayed(buildCompositeCircle())
        }, extraDelay + delay)
        if (updateTimers) {
            updateTimers()
        }
    }

    private fun updateTimers() {
        if (circleDuration > minCircleDuration) {
            val updatedCircleDuration = circleDuration - (circleDuration / 40L)
            circleDuration = if (updatedCircleDuration >= minCircleDuration) {
                updatedCircleDuration
            } else {
                minCircleDuration
            }
        }
        if (circleInterval > minCircleInterval) {
            val updatedCircleInterval = circleInterval - (circleInterval / 40L)
            circleInterval = if (updatedCircleInterval >= minCircleInterval) {
                updatedCircleInterval
            } else {
                minCircleInterval
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        circles.forEach { it.onDraw(canvas) }
    }

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
        }
        circles.remove(toRemove)
    }

    override fun onPause() {
        circles.forEach { it.pause() }
        mainHandler.removeCallbacksAndMessages(null)
        callback?.onGameOver()
    }

    override fun onStop() {
        circles.clear()
        mainHandler.removeCallbacksAndMessages(null)
    }

}