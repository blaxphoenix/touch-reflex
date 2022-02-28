package com.example.touchreflex.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.touchreflex.R

class MainActivity : AppCompatActivity() {

    private var musicMP: MediaPlayer? = null

    override fun onStart() {
        super.onStart()
        setUpMediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customView = ReflexAnimationView(this)
        setContentView(customView)
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