package com.example.touchreflex.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.touchreflex.R
import com.example.touchreflex.TouchReflex
import com.example.touchreflex.utils.AudioService
import com.example.touchreflex.utils.GameState.START
import com.example.touchreflex.utils.MusicType
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val highScoreViewModel: HighScoreViewModel by viewModels {
        HighScoreViewModel.HighScoreViewModelFactory((application as TouchReflex).repository)
    }
    private lateinit var audioService: AudioService
    private lateinit var reflexAnimationView: ReflexAnimationView

    private val mainHandler = Handler(Looper.getMainLooper())

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        audioService = AudioService(this)
        reflexAnimationView = ReflexAnimationView(this).setUpView(highScoreViewModel, audioService)
        setContentView(reflexAnimationView)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()
        audioService
            .switchMusic(MusicType.MENU)
            .start()
    }

    override fun onStop() {
        super.onStop()
        audioService.pause()
    }

    override fun onBackPressed() {
        if (reflexAnimationView.state == START) {
            if (backPressedOnce) {
                super.onBackPressed()
                finish()
            }
            backPressedOnce = true
            mainHandler.postDelayed({ backPressedOnce = false }, 2000)
            // TODO: customize Snackbar
            Snackbar.make(
                this,
                reflexAnimationView,
                this.getText(R.string.back_button_snack_bar),
                2000
            ).show()
        } else {
            reflexAnimationView.onBackPressed()
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}