package com.example.touchreflex.db

import com.example.touchreflex.R
import com.example.touchreflex.draw.circle.CircleManagerSettings

enum class GameMode(val nameResourceId: Int, val settings: CircleManagerSettings) {
    EASY(R.string.game_mode_easy, CircleManagerSettings.EASY),
    HARD(R.string.game_mode_hard, CircleManagerSettings.HARD)
}