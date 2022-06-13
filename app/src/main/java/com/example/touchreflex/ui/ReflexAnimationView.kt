package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import com.example.touchreflex.R
import com.example.touchreflex.db.GameMode
import com.example.touchreflex.db.HighScoreItem
import com.example.touchreflex.draw.CustomDrawableManager
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleDrawableManager
import com.example.touchreflex.draw.text.AnimatedInfoText
import com.example.touchreflex.draw.text.InfoTextDrawableManager
import com.example.touchreflex.draw.text.SimpleInfoText
import com.example.touchreflex.ui.ReflexAnimationView.State.*

/**
 * The basic reflex animation game view.
 */
class ReflexAnimationView(context: Context) : View(context) {

    private enum class State {
        START, GAME, RESTART_DELAY, RESTART
    }

    private val highScoreObserver = Observer<MutableList<HighScoreItem>> { list ->
        list?.let {
            val item = list.firstOrNull { it.gameMode == GameMode.DEFAULT }
            if (item != null) {
                highScore = item.score
            }
        }
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val soundPool: SoundPool
    private val startSoundId: Int
    private val stopSoundId: Int
    private val touchSoundId: Int

    private var state: State = START

    private var circleManager: CustomDrawableManager
    private var startTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            AnimatedInfoText(
                this,
                resources.getString(R.string.start_game),
                color = ResourcesCompat.getColor(this.resources, R.color.blue_heavy_1, null)
            )
        )
    )
    private var restartTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            AnimatedInfoText(
                this,
                resources.getString(R.string.restart_game),
                color = ResourcesCompat.getColor(this.resources, R.color.red_heavy_1, null)
            )
        )
    )
    private var scoreTextManager: InfoTextDrawableManager
    private var scoreInfoText: SimpleInfoText
    private var highScoreInfoText: SimpleInfoText? = null
    private var gameDescriptionInfoText1: SimpleInfoText? = null
    private var gameDescriptionInfoText2: SimpleInfoText? = null
    private var restartGameNewHighScoreInfoText: SimpleInfoText? = null
    private var restartGameMotivationInfoText: SimpleInfoText? = null
    private var restartGameGameOverInfoText: SimpleInfoText? = null

    private var totalScore = 0
    private var highScore = 0

    private lateinit var highScoreViewModel: HighScoreViewModel

    private var gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, CustomGestureListener(this))

    init {
        scoreInfoText = SimpleInfoText(
            this,
            totalScore.toString(),
            color = ResourcesCompat.getColor(this.resources, R.color.blue_light_2, null)
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
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
            )
            .build()

        touchSoundId = soundPool.load(context, R.raw.glass_002, 1)
        startSoundId = soundPool.load(context, R.raw.confirmation_002, 1)
        stopSoundId = soundPool.load(context, R.raw.error_006, 1)
    }

    fun setUpView(highScoreViewModel: HighScoreViewModel): ReflexAnimationView {
        this.highScoreViewModel = highScoreViewModel
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        highScoreViewModel.allHighScoreItems.observeForever(highScoreObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        highScoreViewModel.allHighScoreItems.removeObserver(highScoreObserver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        startTextManager.init()

        if (highScoreInfoText == null) {
            val xPos = this.width / 2f
            val yPos = 250f
            highScoreInfoText = SimpleInfoText(
                this,
                context.getString(R.string.info_high_score, highScore),
                true,
                xPos,
                yPos,
                color = ResourcesCompat.getColor(this.resources, R.color.blue_heavy_1, null)
            )
            scoreTextManager.elements.add(highScoreInfoText!!)
        }

        if (gameDescriptionInfoText1 == null && gameDescriptionInfoText2 == null) {
            val xPos = this.width / 2f
            var yPos = this.height - 200f
            gameDescriptionInfoText1 = SimpleInfoText(
                this,
                context.getString(R.string.game_description_start_game_1),
                x = xPos,
                y = yPos,
                textSize = 60f,
                color = ResourcesCompat.getColor(this.resources, R.color.purple_light_2, null)
            )
            yPos += 80f
            gameDescriptionInfoText2 = SimpleInfoText(
                this,
                context.getString(R.string.game_description_start_game_2),
                x = xPos,
                y = yPos,
                textSize = 60f,
                color = ResourcesCompat.getColor(this.resources, R.color.purple_light_2, null)
            )
            startTextManager.elements.add(gameDescriptionInfoText1!!)
            startTextManager.elements.add(gameDescriptionInfoText2!!)
        }

        if (restartGameNewHighScoreInfoText == null && restartGameMotivationInfoText == null) {
            val xPos = this.width / 2f
            var yPos = this.height / 1.25f
            restartGameGameOverInfoText = SimpleInfoText(
                this,
                context.getString(R.string.game_over),
                false,
                xPos,
                yPos,
                100f,
                color = ResourcesCompat.getColor(this.resources, R.color.red, null)
            )
            yPos += 120f
            restartGameNewHighScoreInfoText = SimpleInfoText(
                this,
                context.getString(R.string.restart_game_new_high_score),
                true,
                xPos,
                yPos,
                100f,
                color = ResourcesCompat.getColor(this.resources, R.color.yellow_heavy_2, null)
            )
            restartGameMotivationInfoText = SimpleInfoText(
                this,
                context.getString(R.string.restart_game_motivation),
                true,
                xPos,
                yPos,
                100f,
                color = ResourcesCompat.getColor(this.resources, R.color.yellow_heavy_2, null)
            )
            restartTextManager.elements.add(restartGameGameOverInfoText!!)
            restartTextManager.elements.add(restartGameNewHighScoreInfoText!!)
            restartTextManager.elements.add(restartGameMotivationInfoText!!)
        }

    }

    private fun initGame() {
        soundPool.play(startSoundId, 1f, 1f, 0, 0, 1f)
        state = GAME
        totalScore = 0
        scoreInfoText.text = totalScore.toString()
        highScoreInfoText?.isIgnored = true
        circleManager.onStop()
        circleManager.init()
    }

    private fun scored() {
        soundPool.play(touchSoundId, 1f, 1f, 0, 0, 1f)
        totalScore++
        scoreInfoText.text = totalScore.toString()
    }

    private fun gameOver() {
        soundPool.play(stopSoundId, 1f, 1f, 0, 0, 1f)
        mainHandler.postDelayed({
            state = RESTART
        }, 750L)

        state = RESTART_DELAY
        restartTextManager.init()

        if (totalScore > highScore) {
            highScore = totalScore
            highScoreViewModel.insert(HighScoreItem(GameMode.DEFAULT, highScore))
            restartGameNewHighScoreInfoText?.isIgnored = false
            restartGameMotivationInfoText?.isIgnored = true
        } else {
            restartGameNewHighScoreInfoText?.isIgnored = true
            restartGameMotivationInfoText?.isIgnored = false
        }
        highScoreInfoText?.isIgnored = false
        highScoreInfoText?.text = context.getString(R.string.info_high_score, highScore)
        scoreInfoText.text = context.getString(R.string.info_total_score, totalScore)
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> {
                startTextManager.onDraw(canvas)
            }
            GAME -> {
                circleManager.onDraw(canvas)
                scoreTextManager.onDraw(canvas)
            }
            RESTART, RESTART_DELAY -> {
                circleManager.onDraw(canvas)
                restartTextManager.onDraw(canvas)
                scoreTextManager.onDraw(canvas)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private class CustomGestureListener(val viewCallback: ReflexAnimationView) :
        GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // only in the active game state, so the clicking/touching is reactive
            if (viewCallback.state == GAME) {
                viewCallback.circleManager.onTouch(e.x, e.y)
            }
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // don't catch accidental navigation swipes if user wants to exit the app or go back
            when (viewCallback.state) {
                START -> {
                    viewCallback.initGame()
                }
                RESTART -> {
                    viewCallback.initGame()
                }
                else -> {
                    // nothing
                }
            }
            return true
        }
    }

}