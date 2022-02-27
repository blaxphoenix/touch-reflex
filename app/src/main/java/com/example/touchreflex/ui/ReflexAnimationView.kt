package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
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
        START, GAME, RESTART_DELAY, RESTART
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var clickMP: MediaPlayer
    private var startMP: MediaPlayer
    private var stopMP: MediaPlayer

    private var state: State = START
    private var circleManager: CustomDrawableManager? = null
    private var startTextManager: CustomDrawableManager? = null
    private var restartTextManager: CustomDrawableManager? = null
    private var scoreTextManager: InfoTextDrawableManager? = null
    private var scoreInfoText: SimpleScoreInfoText
    private var highScoreInfoText: SimpleScoreInfoText? = null
    private var totalScore = 0
    private var highScore = 0

    init {
        startTextManager =
            InfoTextDrawableManager(
                arrayListOf(
                    AnimatedInfoText(
                        this,
                        resources.getString(R.string.start_game)
                    )
                )
            )

        restartTextManager =
            InfoTextDrawableManager(
                arrayListOf(
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
                arrayListOf(
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

        clickMP = MediaPlayer.create(context, R.raw.glass_002)
        startMP = MediaPlayer.create(context, R.raw.confirmation_002)
        stopMP = MediaPlayer.create(context, R.raw.error_006)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        startTextManager?.init()

        if (highScoreInfoText == null) {
            val xPos = this.width / 2f
            val yPos = 250f
            highScoreInfoText = SimpleScoreInfoText(
                this,
                "High Score: $highScore",
                true,
                xPos,
                yPos
            )
            scoreTextManager?.elements?.add(highScoreInfoText!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> startTextManager?.onDraw(canvas)
            GAME -> {
                circleManager?.onDraw(canvas)
                scoreTextManager?.onDraw(canvas)
            }
            RESTART, RESTART_DELAY -> {
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
                    else -> {}
                }
            }
        }
        return true
    }

    private fun initGame() {
        startMP.start()
        state = GAME
        totalScore = 0
        scoreInfoText.text = totalScore.toString()
        highScoreInfoText?.isIgnored = true
        circleManager?.onStop()
        circleManager?.init()
    }

    private fun scored() {
        clickMP.start()
        totalScore++
        scoreInfoText.text = totalScore.toString()
    }

    private fun gameOver() {
        stopMP.start()
        mainHandler.postDelayed({
            state = RESTART
        }, 750L)

        state = RESTART_DELAY
        restartTextManager?.init()

        if (totalScore > highScore) {
            highScore = totalScore
        }
        highScoreInfoText?.isIgnored = false
        highScoreInfoText?.text = "High Score: $highScore"

        scoreInfoText.text = "Total Score: $totalScore"
    }

}