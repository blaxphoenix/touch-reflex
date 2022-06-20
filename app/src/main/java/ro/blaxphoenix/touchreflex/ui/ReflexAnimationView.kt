package ro.blaxphoenix.touchreflex.ui

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
import ro.blaxphoenix.touchreflex.R
import ro.blaxphoenix.touchreflex.db.GameMode
import ro.blaxphoenix.touchreflex.db.HighScoreItem
import ro.blaxphoenix.touchreflex.draw.DefaultDrawableManager
import ro.blaxphoenix.touchreflex.draw.ReflexAnimationCallback
import ro.blaxphoenix.touchreflex.draw.button.SingleSelectorButton
import ro.blaxphoenix.touchreflex.draw.button.SingleSelectorButtonDrawableManager
import ro.blaxphoenix.touchreflex.draw.circle.InfiniteCompositeCircleDrawableManager
import ro.blaxphoenix.touchreflex.draw.image.SimpleImage
import ro.blaxphoenix.touchreflex.draw.text.AnimatedInfoText
import ro.blaxphoenix.touchreflex.draw.text.SimpleInfoText
import ro.blaxphoenix.touchreflex.utils.AudioService
import ro.blaxphoenix.touchreflex.utils.GameState
import ro.blaxphoenix.touchreflex.utils.GameState.*
import ro.blaxphoenix.touchreflex.utils.MusicType
import ro.blaxphoenix.touchreflex.utils.Utils
import java.util.*
import kotlin.math.roundToInt

/**
 * The basic reflex animation game view.
 */
class ReflexAnimationView(context: Context) : View(context) {

    var state: GameState = START
        private set

    private var gameMode: GameMode = GameMode.EASY
        private set(value) {
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

    private var currentTotalScore = 0
    private var highScores: EnumMap<GameMode, Int> = EnumMap(GameMode.values().associateWith { 0 })

    // circles
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
    private val demoCircleManager: InfiniteCompositeCircleDrawableManager =
        object : InfiniteCompositeCircleDrawableManager(this) {
            init {
                alpha = 0x66
            }

            override fun updateTimers() {}
            override fun onPause() =
                circles.forEach { if (it.isDone) circles.remove(it) }
        }

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
    private val startTextManager: DefaultDrawableManager = DefaultDrawableManager(
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
    private val restartTextManager: DefaultDrawableManager = DefaultDrawableManager(
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
    private val inGameTextManager: DefaultDrawableManager = DefaultDrawableManager(
        arrayListOf(
            inGameCurrentScoreText
        )
    )

    // game mode selection buttons
    private val gameModeButtonEasy = SingleSelectorButton(
        this,
        0f,
        0f,
        225f,
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
        225f,
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
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        highScoreViewModel.allHighScoreItems.removeObserver(highScoreObserver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setDrawableAttributes()
        startTextManager.init()
        demoCircleManager.onStop()
        demoCircleManager.init()
    }

    private fun setDrawableAttributes() {
        demoCircleManager.radius = Utils.getSize(Utils.MAX_CIRCLE_RADIUS, width, 1f)
        circleManager.radius = Utils.getSize(Utils.MAX_CIRCLE_RADIUS, width, 1f)
        startAnimatedText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        startHighScoreInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        startDescriptionInfoText1.textSize = Utils.getSize(Utils.MAX_SMALL_TEXT_SIZE, width)
        startDescriptionInfoText2.textSize = Utils.getSize(Utils.MAX_SMALL_TEXT_SIZE, width)
        restartAnimatedText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartCurrentScoreInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartHighScoreInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartNewHighScoreInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartMotivationInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartGameOverInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        inGameCurrentScoreText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        gameModeButtonEasy.setNewAttributes(
            width * 0.3f,
            height / 1.25f,
            Utils.getSize(Utils.MAX_BUTTON_HEIGHT, width),
            Utils.getSize(Utils.MAX_BUTTON_WIDTH, width),
            Utils.getSize(Utils.MAX_BUTTON_TEXT_SIZE, width)
        )
        gameModeButtonHard.setNewAttributes(
            width * 0.7f,
            height / 1.25f,
            Utils.getSize(Utils.MAX_BUTTON_HEIGHT, width),
            Utils.getSize(Utils.MAX_BUTTON_WIDTH, width),
            Utils.getSize(Utils.MAX_BUTTON_TEXT_SIZE, width)
        )
        // TODO use the same width height order in the whole project
        backButton?.setNewSize(
            width = Utils.getSize(Utils.MAX_IMAGE_SIZE * 2, width).roundToInt(),
            height = Utils.getSize(Utils.MAX_IMAGE_SIZE * 2, width).roundToInt()
        )

        val xPos = width / 2f
        var yPos = height - (height / 10f)
        startDescriptionInfoText1.setNewCoordinates(xPos, yPos)
        yPos += startDescriptionInfoText1.textSize * 1.25f
        startDescriptionInfoText2.setNewCoordinates(xPos, yPos)
        yPos = startHighScoreInfoText.y!! + startHighScoreInfoText.textSize * 1.25f
        restartHighScoreInfoText.setNewCoordinates(xPos, yPos)
        yPos = height / 1.25f
        restartGameOverInfoText.setNewCoordinates(xPos, yPos)
        yPos += restartGameOverInfoText.textSize * 1.35f
        restartNewHighScoreInfoText.setNewCoordinates(xPos, yPos)
        restartMotivationInfoText.setNewCoordinates(xPos, yPos)
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
                    if (touchY > centerY - viewCallback.startAnimatedText.textSize * 1.5f
                        && touchY < centerY + viewCallback.startAnimatedText.textSize * 1.5f
                    ) {
                        viewCallback.initGame()
                    }
                    if (viewCallback.gameModeButtonEasy.isInBoundary(touchX, touchY)) {
                        if (!viewCallback.gameModeButtonEasy.isSelected) {
                            viewCallback.gameModeButtonEasy.onTouch(touchX, touchY)
                            viewCallback.gameModeButtonHard.onTouch(touchX, touchY)
                            viewCallback.gameMode = GameMode.EASY
                            viewCallback.audioService.playConfirmSound()
                        }
                    } else if (viewCallback.gameModeButtonHard.isInBoundary(touchX, touchY)) {
                        if (!viewCallback.gameModeButtonHard.isSelected) {
                            viewCallback.gameModeButtonHard.onTouch(touchX, touchY)
                            viewCallback.gameModeButtonEasy.onTouch(touchX, touchY)
                            viewCallback.gameMode = GameMode.HARD
                            viewCallback.audioService.playConfirmSound()
                        }
                    }
                }
                RESTART -> {
                    // TODO move to common place inside reflex view class maybe?
                    val centerY = viewCallback.height / 2
                    val touchX = e.x
                    val touchY = e.y
                    // TODO implement properly with animatedText.isInBoundary()
                    if (touchY > centerY - viewCallback.startAnimatedText.textSize * 1.5f
                        && touchY < centerY + viewCallback.startAnimatedText.textSize * 1.5f
                    ) {
                        viewCallback.initGame()
                    }
                    // TODO make it also work on START screen to exit app?
                    if (viewCallback.backButton?.isInBoundary(touchX, touchY) == true) {
                        viewCallback.state = START
                        viewCallback.audioService.playConfirmSound()
                        // TODO better solution?
                        viewCallback.demoCircleManager.onStop()
                        viewCallback.demoCircleManager.init()
                    }
                }
                else -> {
                }
            }
            return true
        }

    }

}