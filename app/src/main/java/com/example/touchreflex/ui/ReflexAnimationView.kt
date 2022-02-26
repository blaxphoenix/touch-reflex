package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.example.touchreflex.draw.circle.CircleManager
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleManager

class ReflexAnimationView(context: Context) : View(context) {

    private var circleManager: CircleManager? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        circleManager = InfiniteCompositeCircleManager(this).init()
    }

    override fun onDraw(canvas: Canvas) {
        circleManager?.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                circleManager?.onTouch(touchX, touchY)
            }
        }
        return true
    }

}