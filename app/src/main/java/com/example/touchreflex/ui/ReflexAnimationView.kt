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
import com.example.touchreflex.draw.button.SingleSelectorButton
import com.example.touchreflex.draw.button.SingleSelectorButtonDrawableManager
import com.example.touchreflex.draw.circle.CircleManagerSettings
import com.example.touchreflex.draw.circle.DemoInfiniteCompositeCircleDrawableManager
import com.example.touchreflex.draw.circle.InfiniteCompositeCircleDrawableManager
import com.example.touchreflex.draw.image.SimpleImage
import com.example.touchreflex.draw.text.AnimatedInfoText
import com.example.touchreflex.draw.text.InfoTextDrawableManager
import com.example.touchreflex.draw.text.SimpleInfoText
import com.example.touchreflex.utils.AudioService
import com.example.touchreflex.utils.GameState
import com.example.touchreflex.utils.GameState.*
import com.example.touchreflex.utils.MusicType
import java.util.*

/**
 * The basic reflex animation game view.
 */
class ReflexAnimationView(context: Context) : View(context) {

    var state: GameState = START
        private set
    private var gameMode: GameMode = GameMode.EASY
        set(value) {
            field = value
            circleManager.settings = value.settings
            demoCircleManager.settings = value.settings

            startHighScoreInfoText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            startAnimatedText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            gameModeButtonEasy.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            gameModeButtonEasy.textColor =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            gameModeButtonHard.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            gameModeButtonHard.textColor =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            inGameCurrentScoreText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            restartCurrentScoreInfoText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            restartHighScoreInfoText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            restartAnimatedText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            backButton?.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null)

            demoCircleManager.onStop()
            demoCircleManager.init()
            startHighScoreInfoText.text =
                context.getString(
                    R.string.info_high_score,
                    context.getString(gameMode.nameResourceId),
                    highScores[gameMode]
                )
        }
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var highScoreViewModel: HighScoreViewModel
    private lateinit var audioService: AudioService

    private var gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, CustomGestureListener(this))

    private var currentTotalScore = 0
    private var highScores: EnumMap<GameMode, Int> = EnumMap(GameMode.values().associateWith { 0 })

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

    private val demoCircleManager: DemoInfiniteCompositeCircleDrawableManager =
        DemoInfiniteCompositeCircleDrawableManager(this)

    // start game text
    private val startAnimatedText: AnimatedInfoText = AnimatedInfoText(
        this,
        resources.getString(R.string.start_game),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
    )
    private val startHighScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(
            R.string.info_high_score,
            context.getString(gameMode.nameResourceId),
            highScores[gameMode]
        ),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
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
            startAnimatedText,
            startHighScoreInfoText,
            startDescriptionInfoText1,
            startDescriptionInfoText2
        )
    )

    // restart game text
    private val restartAnimatedText: AnimatedInfoText = AnimatedInfoText(
        this,
        resources.getString(R.string.restart_game),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
    )
    private val restartCurrentScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(
            R.string.info_current_score,
            context.getString(gameMode.nameResourceId),
            currentTotalScore
        ),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
    )
    private val restartHighScoreInfoText: SimpleInfoText = SimpleInfoText(
        this,
        context.getString(
            R.string.info_high_score,
            context.getString(gameMode.nameResourceId),
            highScores[gameMode]
        ),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
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
            restartAnimatedText,
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
        currentTotalScore.toString(),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
    )
    private val inGameTextManager: InfoTextDrawableManager = InfoTextDrawableManager(
        arrayListOf(
            inGameCurrentScoreText
        )
    )

    // game mode selection buttons
    private val gameModeButtonEasy = SingleSelectorButton(
        this,
        0f,
        0f,
        220f,
        450f,
        context.getString(R.string.game_mode_easy).uppercase(),
        ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null),
        ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null),
        true
    )
    private val gameModeButtonHard = SingleSelectorButton(
        this,
        0f,
        0f,
        220f,
        450f,
        context.getString(R.string.game_mode_hard).uppercase(),
        ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null),
        ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
    )
    private val gameModeButtonManager = SingleSelectorButtonDrawableManager(
        arrayListOf(
            gameModeButtonEasy,
            gameModeButtonHard
        )
    )

    // back button
    private val backButton: SimpleImage? =
        ResourcesCompat.getDrawable(resources, R.drawable.custom_back_button, null)?.let {
            SimpleImage(
                it,
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null),
                0, 0, 200, 200
            )
        }

    private val highScoreObserver = Observer<MutableList<HighScoreItem>> { list ->
        list?.let {
            list.forEach { highScores[it.gameMode] = it.score }
            val item = list.firstOrNull { it.gameMode == gameMode }
            item?.let {
                highScores[it.gameMode] = it.score
                startHighScoreInfoText.text =
                    context.getString(
                        R.string.info_high_score,
                        context.getString(gameMode.nameResourceId),
                        it.score
                    )
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

        gameModeButtonEasy.setNewCoordinates(this.width * 0.3f, this.height / 1.25f)
        gameModeButtonHard.setNewCoordinates(this.width * 0.7f, this.height / 1.25f)
    }

    private fun initGame() {
        audioService
            .playConfirmSound()
            .switchMusic(gameMode.musicType)
        state = GAME
        currentTotalScore = 0
        inGameCurrentScoreText.text = currentTotalScore.toString()
        restartHighScoreInfoText.isIgnored = true
        demoCircleManager.onStop()
        circleManager.onStop()
        circleManager.init()
    }

    private fun scored() {
        audioService.playTouchSound()
        currentTotalScore++
        inGameCurrentScoreText.text = currentTotalScore.toString()
    }

    private fun gameOver() {
        mainHandler.postDelayed({
            state = RESTART
        }, 750L)

        state = RESTART_DELAY
        restartTextManager.init()
        audioService.switchMusic(MusicType.MENU)

        if (currentTotalScore > highScores[gameMode]!!) {
            audioService.playHighScoreSound()
            highScores[gameMode] = currentTotalScore
            highScoreViewModel.insert(HighScoreItem(gameMode, currentTotalScore))
            restartNewHighScoreInfoText.isIgnored = false
            restartMotivationInfoText.isIgnored = true
        } else {
            audioService.playGameOverSound()
            restartNewHighScoreInfoText.isIgnored = true
            restartMotivationInfoText.isIgnored = false
        }
        restartHighScoreInfoText.isIgnored = false
        restartHighScoreInfoText.text =
            context.getString(
                R.string.info_high_score,
                context.getString(gameMode.nameResourceId),
                highScores[gameMode]
            )
        restartCurrentScoreInfoText.text =
            context.getString(
                R.string.info_current_score,
                context.getString(gameMode.nameResourceId),
                currentTotalScore
            )
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> {
                demoCircleManager.onDraw(canvas)
                startTextManager.onDraw(canvas)
                gameModeButtonManager.onDraw(canvas)
            }
            GAME -> {
                circleManager.onDraw(canvas)
                inGameTextManager.onDraw(canvas)
            }
            RESTART, RESTART_DELAY -> {
                circleManager.onDraw(canvas)
                restartTextManager.onDraw(canvas)
                backButton?.onDraw(canvas)
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
                START -> {
                    // TODO move to common place inside reflex view class maybe?
                    val centerY = viewCallback.height / 2
                    val touchX = e.x
                    val touchY = e.y
                    // TODO implement properly with animatedText.isInBoundary()
                    if (touchY > centerY - 250 && touchY < centerY + 250) {
                        viewCallback.initGame()
                    }
                    if (viewCallback.gameModeButtonEasy.isInBoundary(touchX, touchY)) {
                        if (!viewCallback.gameModeButtonEasy.isSelected) {
                            viewCallback.gameModeButtonEasy.onTouch(touchX, touchY)
                            viewCallback.gameModeButtonHard.onTouch(touchX, touchY)
                            viewCallback.gameMode = GameMode.EASY
                        }
                    } else if (viewCallback.gameModeButtonHard.isInBoundary(touchX, touchY)) {
                        if (!viewCallback.gameModeButtonHard.isSelected) {
                            viewCallback.gameModeButtonHard.onTouch(touchX, touchY)
                            viewCallback.gameModeButtonEasy.onTouch(touchX, touchY)
                            viewCallback.gameMode = GameMode.HARD
                        }
                    }
                }
                RESTART -> {
                    // TODO move to common place inside reflex view class maybe?
                    val centerY = viewCallback.height / 2
                    val touchX = e.x
                    val touchY = e.y
                    // TODO implement properly with animatedText.isInBoundary()
                    if (touchY > centerY - 250 && touchY < centerY + 250) {
                        viewCallback.initGame()
                    }
                    // TODO make it also work on START screen to exit app?
                    if (viewCallback.backButton?.isInBoundary(touchX, touchY) == true) {
                        viewCallback.state = START
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
                        if (viewCallback.circleManager.settings == CircleManagerSettings.EASY) {
                            CircleManagerSettings.HARD
                        } else {
                            CircleManagerSettings.EASY
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