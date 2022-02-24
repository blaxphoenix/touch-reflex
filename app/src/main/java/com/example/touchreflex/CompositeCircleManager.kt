package com.example.touchreflex

import android.graphics.Canvas
import android.os.Handler
import android.view.View
import kotlin.random.Random

class CompositeCircleManager(
    private val parentView: View,
    private val handler: Handler,
    private val w: Int,
    private val h: Int,
    private val count: Int = 10,
    private val periodBetween: Long = 1000L,
    private val durationPerCircle: Long = 1000L,
    private val r: Float = 50f
) {

    private val circles: ArrayList<CompositeCircle> = arrayListOf()

    fun init(): CompositeCircleManager {
        for (i in 0..count) {
            val circle =
                CompositeCircle(
                    parentView,
                    Utils.nextFloat(r, w.toFloat()),
                    Utils.nextFloat(r, h.toFloat()),
                    r,
                    durationPerCircle
                )
            handler.postDelayed({
                circles.add(circle)
                circle.startDrawing()
            }, i * Utils.nextLongWithMargin(periodBetween))
        }
        return this
    }

    fun onDraw(canvas: Canvas) {
        circles.forEach { it.onDraw(canvas) }
    }

    fun onTouch(touchX: Float, touchY: Float) {
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

}