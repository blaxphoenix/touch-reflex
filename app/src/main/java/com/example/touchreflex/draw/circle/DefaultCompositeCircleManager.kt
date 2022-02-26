package com.example.touchreflex.draw.circle

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.touchreflex.utils.Utils

@Deprecated("not in use anymore")
class DefaultCompositeCircleManager(
    private val parentView: View,
    private val count: Int = 10,
    private val periodBetween: Long = 1000L,
    private val durationPerCircle: Long = 1000L
) : CircleManager {

    private val circles: ArrayList<CompositeCircle> = arrayListOf()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val radius: Float = 50f

    override fun init(): DefaultCompositeCircleManager {
        for (i in 1..count) {
            val circle =
                CompositeCircle(
                    this,
                    parentView,
                    Utils.nextFloat(radius, parentView.width.toFloat()),
                    Utils.nextFloat(radius, parentView.height.toFloat()),
                    radius,
                    durationPerCircle
                )
            mainHandler.postDelayed({
                circles.add(circle)
                circle.startDrawing()
            }, i * Utils.nextLongWithMargin(periodBetween))
        }
        return this
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
        TODO("Not yet implemented")
    }

}