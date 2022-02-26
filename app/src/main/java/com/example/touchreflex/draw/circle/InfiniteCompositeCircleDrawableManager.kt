package com.example.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.utils.Utils
import java.util.LinkedList

class InfiniteCompositeCircleDrawableManager(
    private val parentView: View,
    private val callback: ReflexAnimationCallback? = null
) : CustomDrawableManager {

    private val circles: LinkedList<CompositeCircle> = LinkedList()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val radius: Float = 80f
    private val startCircleDuration: Long = 2000L
    private val startCircleInterval: Long = 2000L
    private val minCircleDuration: Long = 1250L
    private val minCircleInterval: Long = 750L
    private var circleDuration: Long = startCircleDuration
    private var circleInterval: Long = startCircleInterval

    override fun init(): CustomDrawableManager {
        circleDuration = startCircleDuration
        circleInterval = startCircleInterval
        postDelayed(buildCompositeCircle())
        postDelayed(buildCompositeCircle(), false, startCircleInterval / 2)
        postDelayed(buildCompositeCircle(), false, startCircleInterval / 4)
        return this
    }

    private fun buildCompositeCircle(): CompositeCircle {
        return CompositeCircle(
            this,
            parentView,
            Utils.nextFloat(radius, parentView.width.toFloat() - radius),
            Utils.nextFloat(radius, parentView.height.toFloat() - radius),
            radius,
            circleDuration
        )
    }

    private fun postDelayed(
        circle: CompositeCircle,
        updateTimers: Boolean = true,
        extraDelay: Long = 0L
    ) {
        mainHandler.postDelayed({
            circles.add(circle)
            circle.onStartDrawing()
            postDelayed(buildCompositeCircle())
        }, extraDelay + Utils.nextLongWithMargin(circleInterval, circleInterval / 3L))
        if (updateTimers) {
            updateTimers()
        }
    }

    private fun updateTimers() {
        if (circleDuration > minCircleDuration) {
            val updatedCircleDuration = circleDuration - (circleDuration / 30L)
            circleDuration = if (updatedCircleDuration >= minCircleDuration) {
                updatedCircleDuration
            } else {
                minCircleDuration
            }
        }
        if (circleInterval > minCircleInterval) {
            val updatedCircleInterval = circleInterval - (circleInterval / 30L)
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