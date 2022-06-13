package com.example.touchreflex.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.touchreflex.TouchReflex
import com.example.touchreflex.utils.AudioService
import com.example.touchreflex.utils.MusicType

class MainActivity : AppCompatActivity() {

    private val highScoreViewModel: HighScoreViewModel by viewModels {
        HighScoreViewModel.HighScoreViewModelFactory((application as TouchReflex).repository)
    }

    private lateinit var audioService: AudioService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        audioService = AudioService(this)
        setContentView(ReflexAnimationView(this).setUpView(highScoreViewModel, audioService))
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

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}