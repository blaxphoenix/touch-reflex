package com.example.touchreflex

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class ReflexAnimationView(context: Context) : View(context) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var circleManager: CompositeCircleManager? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        circleManager = CompositeCircleManager(
            this,
            mainHandler,
            width,
            height,
            15,
            500L,
            750L
        ).init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
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