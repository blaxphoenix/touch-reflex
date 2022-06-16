package com.example.touchreflex.db

import com.example.touchreflex.draw.circle.CircleManagerSettings

enum class GameMode(val settings: CircleManagerSettings) {
    DEFAULT(CircleManagerSettings.DEFAULT),
    HARD(CircleManagerSettings.HARD)
}