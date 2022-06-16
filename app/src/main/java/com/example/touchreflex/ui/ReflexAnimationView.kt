package com.example.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
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
import com.example.touchreflex.draw.ReflexAnimationCallback
import com.example.touchreflex.draw.circle.CircleManagerSettings
import com.example.touchreflex.draw.circle.DemoCompositeCircleDrawableManager
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleDrawableManager
import com.example.touchreflex.draw.text.AnimatedInfoText
import com.example.touchreflex.draw.text.InfoTextDrawableManager
import com.example.touchreflex.draw.text.SimpleInfoText
import com.example.touchreflex.utils.AudioService
import com.example.touchreflex.utils.GameState
import com.example.touchreflex.utils.GameState.*
import com.example.touchreflex.utils.MusicType

/**
 * The basic reflex animation game view.
 */
class ReflexAnimationView(context: Context) : View(context) {

    var state: GameState = START
        private set
    private var gameMode: GameMode = GameMode.DEFAULT
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var highScoreViewModel: HighScoreViewModel
    private lateinit var audioService: AudioService

    private var gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, CustomGestureListener(this))

    private var totalScore = 0
    private var highScore = 0

    private val circleManager: InfiniteCompositeCircleDrawableManager =
        InfiniteCompositeCircleDrawableManager(
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

    private val demoCircleManager: DemoCompositeCircleDrawableManager =
        DemoCompositeCircleDrawableManager(this)

    // start game text
    private val startHighScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        highScore.toString(),
        color = ResourcesCompat.getColor(this.resources, R.color.blue_light_2, null)
    )
    private val startDescriptionInfoText1: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.game_description_start_game_1),
        textSize = 60f,
        color = ResourcesCompat.getColor(this.resources, R.color.purple_light_2, null)
    )
    private val startDescriptionInfoText2: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.game_description_start_game_2),
        textSize = 60f,
        color = ResourcesCompat.getColor(this.resources, R.color.purple_light_2, null)
    )
    private val startTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            AnimatedInfoText(
                this,
                resources.getString(R.string.start_game),
                color = ResourcesCompat.getColor(this.resources, R.color.blue_heavy_1, null)
            ),
            startHighScoreInfoText,
            startDescriptionInfoText1,
            startDescriptionInfoText2
        )
    )

    // restart game text
    private val restartCurrentScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.info_current_score, totalScore),
        color = ResourcesCompat.getColor(this.resources, R.color.blue_light_2, null)
    )
    private val restartHighScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.info_high_score, highScore),
        color = ResourcesCompat.getColor(this.resources, R.color.blue_heavy_1, null)
    )
    private val restartNewHighScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.restart_game_new_high_score),
        true,
        textSize = 100f,
        color = ResourcesCompat.getColor(this.resources, R.color.yellow_heavy_2, null)
    )
    private val restartMotivationInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.restart_game_motivation),
        true,
        textSize = 100f,
        color = ResourcesCompat.getColor(this.resources, R.color.yellow_heavy_2, null)
    )
    private val restartGameOverInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(R.string.game_over),
        textSize = 100f,
        color = ResourcesCompat.getColor(this.resources, R.color.red, null)
    )
    private val restartTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            AnimatedInfoText(
                this,
                resources.getString(R.string.restart_game),
                color = ResourcesCompat.getColor(this.resources, R.color.red_heavy_1, null)
            ),
            restartCurrentScoreInfoText,
            restartHighScoreInfoText,
            restartNewHighScoreInfoText,
            restartMotivationInfoText,
            restartGameOverInfoText
        )
    )

    // in game text
    private val inGameCurrentScoreText: SimpleInfoText = SimpleInfoText(
        this,
        totalScore.toString(),
        color = ResourcesCompat.getColor(this.resources, R.color.blue_light_2, null)
    )
    private val inGameTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            inGameCurrentScoreText
        )
    )

    private val highScoreObserver = Observer<MutableList<HighScoreItem>> { list ->
        list?.let {
            val item = list.firstOrNull { it.gameMode == GameMode.DEFAULT }
            if (item != null) {
                highScore = item.score
                startHighScoreInfoText.text =
                    context.getString(R.string.info_high_score, item.score)
            }
        }
    }

    fun setUpView(
        highScoreViewModel: HighScoreViewModel,
        audioService: AudioService
    ): ReflexAnimationView {
        this.highScoreViewModel = highScoreViewModel
        this.audioService = audioService
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        highScoreViewModel.allHighScoreItems.observeForever(highScoreObserver)
        startTextManager.init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        highScoreViewModel.allHighScoreItems.removeObserver(highScoreObserver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        startTextManager.init()
        demoCircleManager.onStop()
        demoCircleManager.init()
        setUpCoordinates()
    }

    private fun setUpCoordinates() {
        var xPos = this.width / 2f
        var yPos = this.height - 200f
        startDescriptionInfoText1.setNewCoordinates(xPos, yPos)
        yPos += 80f
        startDescriptionInfoText2.setNewCoordinates(xPos, yPos)
        xPos = this.width / 2f
        yPos = 250f
        restartHighScoreInfoText.setNewCoordinates(xPos, yPos)
        xPos = this.width / 2f
        yPos = this.height / 1.25f
        restartGameOverInfoText.setNewCoordinates(xPos, yPos)
        yPos += 120f
        restartNewHighScoreInfoText.setNewCoordinates(xPos, yPos)
        restartMotivationInfoText.setNewCoordinates(xPos, yPos)
    }

    private fun initGame() {
        audioService
            .playConfirmSound()
            .switchMusic(MusicType.DEFAULT_GAME)
        state = GAME
        totalScore = 0
        inGameCurrentScoreText.text = totalScore.toString()
        restartHighScoreInfoText.isIgnored = true
        demoCircleManager.onStop()
        circleManager.onStop()
        circleManager.init()
    }

    private fun scored() {
        audioService.playTouchSound()
        totalScore++
        inGameCurrentScoreText.text = totalScore.toString()
    }

    private fun gameOver() {
        mainHandler.postDelayed({
            state = RESTART
        }, 750L)

        state = RESTART_DELAY
        restartTextManager.init()
        audioService.switchMusic(MusicType.MENU)

        if (totalScore > highScore) {
            audioService.playHighScoreSound()
            highScore = totalScore
            highScoreViewModel.insert(HighScoreItem(GameMode.DEFAULT, highScore))
            restartNewHighScoreInfoText.isIgnored = false
            restartMotivationInfoText.isIgnored = true
        } else {
            audioService.playGameOverSound()
            restartNewHighScoreInfoText.isIgnored = true
            restartMotivationInfoText.isIgnored = false
        }
        restartHighScoreInfoText.isIgnored = false
        restartHighScoreInfoText.text = context.getString(R.string.info_high_score, highScore)
        restartCurrentScoreInfoText.text =
            context.getString(R.string.info_current_score, totalScore)
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> {
                demoCircleManager.onDraw(canvas)
                startTextManager.onDraw(canvas)
            }
            GAME -> {
                circleManager.onDraw(canvas)
                inGameTextManager.onDraw(canvas)
            }
            RESTART, RESTART_DELAY -> {
                circleManager.onDraw(canvas)
                restartTextManager.onDraw(canvas)
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

    fun onBackPressed() {
        println(state)
        if (state == GAME) {
            circleManager.onStop()
        }
        state = START
        demoCircleManager.onStop()
        demoCircleManager.init()
        audioService.switchMusic(MusicType.MENU)
    }

    private class CustomGestureListener(val viewCallback: ReflexAnimationView) :
        GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (viewCallback.state == GAME) {
                viewCallback.circleManager.onTouch(e.x, e.y)
            }
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            when (viewCallback.state) {
                START, RESTART -> {
                    val centerY = viewCallback.height / 2
                    val touchY = e.y
                    if (touchY > centerY - 150 && touchY < centerY + 150) {
                        viewCallback.initGame()
                    }
                }
                else -> {
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            when (viewCallback.state) {
                START -> {
                    viewCallback.circleManager.settings =
                        if (viewCallback.circleManager.settings == CircleManagerSettings.DEFAULT) {
                            CircleManagerSettings.HARD
                        } else {
                            CircleManagerSettings.DEFAULT
                        }
                    viewCallback.initGame()
                }
                RESTART -> {
                    viewCallback.initGame()
                }
                else -> {
                    // nothing
                }
            }
        }
    }

}