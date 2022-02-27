package com.example.touchreflex.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.touchreflex.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customView = ReflexAnimationView(this)
        setContentView(customView)

        val mp = MediaPlayer.create(this, R.raw.alive_music)
        mp.start()
    }
}