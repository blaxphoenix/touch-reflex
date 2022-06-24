package ro.blaxphoenix.touchreflex.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
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
import ro.blaxphoenix.touchreflex.draw.image.ImageAndText
import ro.blaxphoenix.touchreflex.draw.image.SimpleImage
import ro.blaxphoenix.touchreflex.draw.text.AnimatedInfoText
import ro.blaxphoenix.touchreflex.draw.text.ScoreTrackerAnimatedInfoText
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
        private set(value) {
            field = value
            setHighScoreDrawableAttributes()
        }

    private var gameMode: GameMode = GameMode.EASY
        private set(value) {
            field = value
            circleManager.settings = value.settings
            demoCircleManager.settings = value.settings

            highScoreDrawable?.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null)
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
            currentScoreText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            inGameCurrentScoreAnimatedText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            inGameNewHighScoreImage?.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
            restartAnimatedText.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
            backButton?.color =
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null)

            demoCircleManager.onStop()
            demoCircleManager.init()
            highScoreDrawable?.text = highScores[gameMode].toString()
        }
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var highScoreViewModel: HighScoreViewModel
    private lateinit var audioService: AudioService

    private var gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(context, CustomGestureListener(this))

    private val highScoreObserver = Observer<MutableList<HighScoreItem>> { list ->
        list?.let {
            list.forEach { highScores[it.gameMode] = it.score }
            list.firstOrNull { it.gameMode == gameMode }?.let {
                highScores[it.gameMode] = it.score
                highScoreDrawable?.text = it.score.toString()
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

                override fun onGameOver(xCenter: Float, yCenter: Float) {
                    gameOver(xCenter, yCenter)
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

    // common drawables
    private val currentScoreText: SimpleInfoText = SimpleInfoText(
        this,
        currentTotalScore.toString(),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
    )
    private val highScoreDrawable: ImageAndText? =
        ResourcesCompat.getDrawable(resources, R.drawable.ic_crown_svgrepo_com, null)?.let {
            ImageAndText(
                this,
                it,
                0f, 0f, 0, 0,
                highScores[gameMode].toString(),
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null)
            )
        }

    // start game
    private val startAnimatedText: AnimatedInfoText = AnimatedInfoText(
        this,
        resources.getString(R.string.start_game),
        color = ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null)
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
    private val startDrawableManager: DefaultDrawableManager = DefaultDrawableManager(
        arrayListOf(
            startAnimatedText,
            startDescriptionInfoText1,
            startDescriptionInfoText2
        )
    )

    // restart game
    private val restartAnimatedText: AnimatedInfoText = AnimatedInfoText(
        this,
        resources.getString(R.string.restart_game),
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
    private val restartDisappearedCircleMarkerImage: SimpleImage? =
        ResourcesCompat.getDrawable(resources, R.drawable.custom_close_icon, null)?.let {
            SimpleImage(
                it,
                ResourcesCompat.getColor(this.resources, R.color.white, null),
                0, 0, 200, 200,
                false
            )
        }
    private val restartDrawableManager: DefaultDrawableManager = DefaultDrawableManager(
        arrayListOf(
            currentScoreText,
            restartAnimatedText,
            restartNewHighScoreInfoText,
            restartMotivationInfoText,
            restartGameOverInfoText
        )
    )

    // in game
    private val inGameCurrentScoreAnimatedText: ScoreTrackerAnimatedInfoText =
        ScoreTrackerAnimatedInfoText(
            this,
            currentTotalScore.toString(),
            color = ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null)
        )
    private val inGameNewHighScoreImage: SimpleImage? =
        ResourcesCompat.getDrawable(resources, R.drawable.ic_crown_svgrepo_com, null)?.let {
            SimpleImage(
                it,
                ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null),
                0, 0, 200, 200,
                true
            )
        }
    private val inGameDrawableManager: DefaultDrawableManager = DefaultDrawableManager(
        arrayListOf(
            currentScoreText,
            inGameCurrentScoreAnimatedText
        )
    )

    // game mode selection buttons
    private val gameModeButtonEasy = SingleSelectorButton(
        this,
        0f,
        0f,
        450f,
        225f,
        context.getString(R.string.game_mode_easy).uppercase(),
        ResourcesCompat.getColor(this.resources, gameMode.colorAccent, null),
        ResourcesCompat.getColor(this.resources, gameMode.colorSecondary, null),
        true
    )
    private val gameModeButtonHard = SingleSelectorButton(
        this,
        0f,
        0f,
        450f,
        225f,
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
        ResourcesCompat.getDrawable(resources, R.drawable.custom_back_icon, null)?.let {
            SimpleImage(
                it,
                ResourcesCompat.getColor(this.resources, gameMode.colorPrimary, null),
                0, 0, 200, 200
            )
        }

    init {
        highScoreDrawable?.let {
            startDrawableManager.add(it)
            restartDrawableManager.add(it)
        }
        backButton?.let { restartDrawableManager.add(it) }
        inGameNewHighScoreImage?.let { inGameDrawableManager.add(it) }
        restartDisappearedCircleMarkerImage?.let { restartDrawableManager.add(it, 0) }
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
        startDrawableManager.init()
        demoCircleManager.onStop()
        demoCircleManager.init()
    }

    private fun setDrawableAttributes() {
        demoCircleManager.radius = Utils.getSize(Utils.MAX_CIRCLE_RADIUS, width)
        circleManager.radius = Utils.getSize(Utils.MAX_CIRCLE_RADIUS, width)
        startAnimatedText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        startDescriptionInfoText1.textSize = Utils.getSize(Utils.MAX_SMALL_TEXT_SIZE, width)
        startDescriptionInfoText2.textSize = Utils.getSize(Utils.MAX_SMALL_TEXT_SIZE, width)
        restartAnimatedText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartNewHighScoreInfoText.textSize =
            Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartMotivationInfoText.textSize =
            Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        restartGameOverInfoText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        currentScoreText.textSize = Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        inGameCurrentScoreAnimatedText.textSize =
            Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width)
        setHighScoreDrawableAttributes()
        gameModeButtonEasy.setNewAttributes(
            width * .3f,
            height / 1.25f,
            Utils.getSize(Utils.MAX_BUTTON_WIDTH, width),
            Utils.getSize(Utils.MAX_BUTTON_HEIGHT, width),
            Utils.getSize(Utils.MAX_BUTTON_TEXT_SIZE, width)
        )
        gameModeButtonHard.setNewAttributes(
            width * .7f,
            height / 1.25f,
            Utils.getSize(Utils.MAX_BUTTON_WIDTH, width),
            Utils.getSize(Utils.MAX_BUTTON_HEIGHT, width),
            Utils.getSize(Utils.MAX_BUTTON_TEXT_SIZE, width)
        )
        backButton?.setNewSize(
            width = Utils.getSize(Utils.MAX_IMAGE_SIZE * 2, width).roundToInt(),
            height = Utils.getSize(Utils.MAX_IMAGE_SIZE * 2, width).roundToInt()
        )
        inGameNewHighScoreImage?.setNewSize(
            Utils.getSize(Utils.MAX_IMAGE_SIZE / 2, width).roundToInt(),
            Utils.getSize(Utils.MAX_IMAGE_SIZE / 2, width).roundToInt(),
            Utils.getSize(Utils.MAX_IMAGE_SIZE, width).roundToInt(),
            Utils.getSize(Utils.MAX_IMAGE_SIZE, width).roundToInt()
        )
        restartDisappearedCircleMarkerImage?.setNewSize(
            width = Utils.getSize(Utils.MAX_IMAGE_SIZE, width).roundToInt(),
            height = Utils.getSize(Utils.MAX_IMAGE_SIZE, width).roundToInt()
        )
        val xPos = width / 2f
        var yPos = height - (height / 10f)
        startDescriptionInfoText1.setNewCoordinates(xPos, yPos)
        yPos += startDescriptionInfoText1.textSize * 1.25f
        startDescriptionInfoText2.setNewCoordinates(xPos, yPos)
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
        inGameNewHighScoreImage?.isIgnored = true
        currentScoreText.isIgnored = false
        currentScoreText.text = currentTotalScore.toString()
        inGameCurrentScoreAnimatedText.text = currentTotalScore.toString()
        demoCircleManager.onStop()
        circleManager.onStop()
        circleManager.init()
    }

    private fun scored() {
        audioService.playTouchSound()
        currentTotalScore++
        currentScoreText.text = currentTotalScore.toString()
        inGameCurrentScoreAnimatedText.text = currentTotalScore.toString()
        inGameCurrentScoreAnimatedText.onStartDrawing()
        if (
            currentTotalScore > highScores[gameMode]!! &&
            inGameNewHighScoreImage != null &&
            inGameNewHighScoreImage.isIgnored
        ) {
            inGameNewHighScoreImage.isIgnored = false
            audioService.playHighScoreSound()
        }
    }

    private fun gameOver(xCenter: Float, yCenter: Float) {
        mainHandler.postDelayed({
            state = RESTART
        }, 750L)

        state = RESTART_DELAY
        restartDrawableManager.init()
        audioService.playGameOverSound()
        audioService.switchMusic(MusicType.MENU)
        Utils.vibrate(context)

        if (currentTotalScore > highScores[gameMode]!!) {
            highScoreDrawable?.text = currentTotalScore.toString()
            highScores[gameMode] = currentTotalScore
            highScoreViewModel.insert(HighScoreItem(gameMode, currentTotalScore))
            restartNewHighScoreInfoText.isIgnored = false
            restartMotivationInfoText.isIgnored = true
        } else {
            currentScoreText.isIgnored = false
            restartNewHighScoreInfoText.isIgnored = true
            restartMotivationInfoText.isIgnored = false
        }
        currentScoreText.isIgnored = currentTotalScore >= highScores[gameMode]!!
        restartDisappearedCircleMarkerImage?.setNewSize(
            x = (xCenter - restartDisappearedCircleMarkerImage.width / 2f).roundToInt(),
            y = (yCenter - restartDisappearedCircleMarkerImage.height / 2f).roundToInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        when (state) {
            START -> {
                demoCircleManager.onDraw(canvas)
                startDrawableManager.onDraw(canvas)
                gameModeButtonManager.onDraw(canvas)
            }
            GAME -> {
                circleManager.onDraw(canvas)
                inGameDrawableManager.onDraw(canvas)
            }
            RESTART, RESTART_DELAY -> {
                circleManager.onDraw(canvas)
                restartDrawableManager.onDraw(canvas)
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

    private fun setHighScoreDrawableAttributes() {
        val highScoreDrawableY: Float = if (currentTotalScore >= highScores[gameMode]!!) {
            getHighScoreDrawableYUp()
        } else {
            when (state) {
                START -> {
                    getHighScoreDrawableYUp()
                }
                RESTART, RESTART_DELAY -> {
                    currentScoreText.y!! + currentScoreText.textSize
                }
                else -> {
                    getHighScoreDrawableYUp()
                }
            }
        }
        highScoreDrawable?.setNewAttributes(
            width / 2f,
            highScoreDrawableY,
            Utils.getSize(Utils.MAX_IMAGE_SIZE, width),
            Utils.getSize(Utils.MAX_IMAGE_SIZE, width),
            Utils.getSize(Utils.MAX_DEFAULT_TEXT_SIZE, width),
        )
    }

    private fun getHighScoreDrawableYUp(): Float {
        val textBounds = Rect()
        currentScoreText.paint.getTextBounds(
            currentScoreText.text,
            0,
            currentScoreText.text.length,
            textBounds
        )
        return currentScoreText.y!! - (textBounds.height() / 2f)
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
                    if (touchY > centerY - viewCallback.startAnimatedText.textSize * 3f
                        && touchY < viewCallback.gameModeButtonEasy.centerY - viewCallback.gameModeButtonEasy.rect.height()
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
                    } else if (viewCallback.gameModeButtonHard.isInBoundary(
                            touchX,
                            touchY
                        )
                    ) {
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
                    // TODO move to common?
                    if (touchY > centerY - viewCallback.startAnimatedText.textSize * 3f
                        && touchY < viewCallback.gameModeButtonEasy.centerY - viewCallback.gameModeButtonEasy.rect.height()
                    ) {
                        viewCallback.initGame()
                    }
                    // TODO make it also work on START screen to exit app?
                    if (viewCallback.backButton?.isInBoundary(touchX, touchY) == true) {
                        viewCallback.state = START
                        viewCallback.audioService.playConfirmSound()
                        // TODO better solution? new restart method in circleManager or manager
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