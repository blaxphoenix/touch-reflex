package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleManager
import com.example.touchreflex.ui.ReflexAnimationView.State.*

class ReflexAnimationView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private enum class State {
        START, GAME, RESTART
    }

    private var state: State = START
    private var circleManager: CustomDrawableManager? = null

    init {
        circleManager = InfiniteCompositeCircleManager(
            this,
            object : ReflexAnimationCallback {
                override fun onGameOver() {
                    state = RESTART
                }

            }
        )
        paint.color =
            ResourcesCompat.getColor(this.resources, R.color.colorCircleFill, null)
        paint.isDither = true
        paint.style = Paint.Style.FILL
        paint.textSize = 100f
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> {
                canvas.drawText("START GAME", width / 2f, height / 2f, paint)
            }
            GAME -> circleManager?.onDraw(canvas)
            RESTART -> {
                circleManager?.onDraw(canvas)
                canvas.drawText("RESTART GAME", width / 2f, height / 2f, paint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (state) {
                    START -> {
                        state = GAME
                        initGame()
                    }
                    GAME -> circleManager?.onTouch(touchX, touchY)
                    RESTART -> {
                        state = GAME
                        circleManager?.onStop()
                        circleManager?.init()
                    }
                }
            }
        }
        return true
    }

    private fun initGame() {
        circleManager?.init()
    }

}