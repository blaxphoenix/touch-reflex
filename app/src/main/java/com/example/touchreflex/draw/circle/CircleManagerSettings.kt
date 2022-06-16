package com.example.touchreflex.draw.circle

import com.example.touchreflex.db.GameMode
import java.util.*

enum class CircleManagerSettings(
    val gameMode: GameMode,
    val startCircleDuration: Long,
    val startCircleInterval: Long,
    val minCircleDuration: Long,
    val minCircleInterval: Long,
    val circleDurationModifier1: Long,
    val circleDurationModifier2: Long,
    val circleIntervalModifier1: Long,
    val circleIntervalModifier2: Long
) {
    EASY(
        GameMode.EASY,
        3500L,
        1750L,
        2250L,
        750L,
        80L,
        120L,
        60L,
        120L
    ),
    HARD(
        GameMode.HARD,
        2000L,
        1000L,
        1500L,
        500L,
        50L,
        80L,
        40L,
        80L
    )

    // TODO cross reference map?
}