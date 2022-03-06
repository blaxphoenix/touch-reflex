package com.example.touchreflex.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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

}