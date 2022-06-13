package com.example.touchreflex.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.touchreflex.R
import com.example.touchreflex.TouchReflex

class MainActivity : AppCompatActivity() {

    private val highScoreViewModel: HighScoreViewModel by viewModels {
        HighScoreViewModel.HighScoreViewModelFactory((application as TouchReflex).repository)
    }
    private var musicMP: MediaPlayer? = null

    override fun onStart() {
        super.onStart()
        setUpMediaPlayer()
        hideSystemBars()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ReflexAnimationView(this).setUpView(highScoreViewModel))
    }

    override fun onStop() {
        super.onStop()
        musicMP?.pause()
    }

    private fun setUpMediaPlayer() {
        if (musicMP == null) {
            musicMP = MediaPlayer.create(this, R.raw.alive_music)
            musicMP?.isLooping = true
        }
        musicMP?.start()
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}