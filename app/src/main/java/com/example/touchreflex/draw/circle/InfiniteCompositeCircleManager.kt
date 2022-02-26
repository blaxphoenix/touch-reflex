package com.example.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.touchreflex.utils.Utils

class InfiniteCompositeCircleManager(
    private val parentView: View
) : CircleManager {

    private val circles: ArrayList<CompositeCircle> = arrayListOf()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val radius: Float = 75f
    private var circleDuration: Long = 2000L
    private var circleInterval: Long = 2000L
    private val minCircleDuration: Long = 500L
    private val minCircleInterval: Long = 500L

    override fun init(): CircleManager {
        postDelayed(buildCompositeCircle())
        postDelayed(buildCompositeCircle(), false)
        return this
    }

    private fun buildCompositeCircle(): CompositeCircle {
        return CompositeCircle(
            this,
            parentView,
            Utils.nextFloat(radius, parentView.width.toFloat()),
            Utils.nextFloat(radius, parentView.height.toFloat()),
            radius,
            circleDuration
        )
    }

    private fun postDelayed(circle: CompositeCircle, updateTimers: Boolean = true) {
        mainHandler.postDelayed({
            circles.add(circle)
            circle.startDrawing()
            postDelayed(buildCompositeCircle())
        }, Utils.nextLongWithMargin(circleInterval, circleInterval / 3L))
        if (updateTimers) {
            updateTimers()
        }
    }

    private fun updateTimers() {
        if (circleDuration > minCircleDuration) {
            val updatedCircleDuration = circleDuration - (circleDuration / 10L)
            circleDuration = if (updatedCircleDuration >= minCircleDuration) {
                updatedCircleDuration
            } else {
                minCircleDuration
            }
        }
        if (circleInterval > minCircleInterval) {
            val updatedCircleInterval = circleInterval - (circleInterval / 10L)
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
        val toRemove: ArrayList<CompositeCircle> = arrayListOf()
        for (circle in circles.reversed()) {
            if (circle.isInBoundary(touchX, touchY)) {
                circle.disable()
                toRemove.add(circle)
                break
            }
        }
        circles.removeAll(toRemove)
    }

    override fun onPause() {
        circles.forEach { it.pause() }
        mainHandler.removeCallbacksAndMessages(null)
    }

}