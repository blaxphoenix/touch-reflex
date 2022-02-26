package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.example.touchreflex.R
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleDrawableManager
import com.example.touchreflex.draw.text.AnimatedInfoText
import com.example.touchreflex.draw.text.InfoTextDrawableManager
import com.example.touchreflex.draw.text.SimpleScoreInfoText
import com.example.touchreflex.ui.ReflexAnimationView.State.*

class ReflexAnimationView(context: Context) : View(context) {

    private enum class State {
        START, GAME, RESTART
    }

    private var state: State = START
    private var circleManager: CustomDrawableManager? = null
    private var startTextManager: CustomDrawableManager? = null
    private var restartTextManager: CustomDrawableManager? = null
    private var scoreTextManager: CustomDrawableManager? = null
    private var scoreInfoText: SimpleScoreInfoText
    private var totalScore = 0

    init {
        startTextManager =
            InfoTextDrawableManager(
                listOf(
                    AnimatedInfoText(
                        this,
                        resources.getString(R.string.start_game)
                    )
                )
            )

        restartTextManager =
            InfoTextDrawableManager(
                listOf(
                    AnimatedInfoText(
                        this,
                        resources.getString(R.string.restart_game)
                    )
                )
            )

        scoreInfoText = SimpleScoreInfoText(
            this,
            totalScore.toString()
        )
        scoreTextManager =
            InfoTextDrawableManager(
                listOf(
                    scoreInfoText
                )
            )

        circleManager = InfiniteCompositeCircleDrawableManager(
            this,
            object : ReflexAnimationCallback {
                override fun onScored() {
                    scored()
                }

                override fun onGameOver() {
                    gameOver()
                }
            }
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        startTextManager?.init()
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> startTextManager?.onDraw(canvas)
            GAME -> {
                circleManager?.onDraw(canvas)
                scoreTextManager?.onDraw(canvas)
            }
            RESTART -> {
                circleManager?.onDraw(canvas)
                restartTextManager?.onDraw(canvas)
                scoreTextManager?.onDraw(canvas)
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
                        initGame()
                    }
                    GAME -> circleManager?.onTouch(touchX, touchY)
                    RESTART -> {
                        initGame()
                    }
                }
            }
        }
        return true
    }

    private fun initGame() {
        state = GAME
        totalScore = 0
        scoreInfoText.text = totalScore.toString()
        circleManager?.onStop()
        circleManager?.init()
    }

    private fun scored() {
        totalScore++
        scoreInfoText.text = totalScore.toString()
    }

    private fun gameOver() {
        state = RESTART
        restartTextManager?.init()
        scoreInfoText.text = "Total Score: $totalScore"
    }

}