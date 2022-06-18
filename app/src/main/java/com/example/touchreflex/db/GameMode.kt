package com.example.touchreflex.db

import androidx.annotation.ColorRes
import com.example.touchreflex.R
import com.example.touchreflex.draw.circle.CircleManagerSettings
import com.example.touchreflex.utils.MusicType

enum class GameMode(
    val nameResourceId: Int,
    val settings: CircleManagerSettings,
    @ColorRes val colorPrimary: Int,
    @ColorRes val colorSecondary: Int,
    @ColorRes val colorAccent: Int,
    val musicType: MusicType
) {
    EASY(
        R.string.game_mode_easy,
        CircleManagerSettings.EASY,
        R.color.blue,
        R.color.blue_light_2,
        R.color.blue_heavy_1,
        MusicType.EASY
    ),
    HARD(
        R.string.game_mode_hard,
        CircleManagerSettings.HARD,
        R.color.red,
        R.color.red_light_2,
        R.color.red_heavy_1,
        MusicType.HARD
    )
}