package ro.blaxphoenix.touchreflex.draw.circle

import ro.blaxphoenix.touchreflex.db.GameMode
import ro.blaxphoenix.touchreflex.utils.Utils
import com.google.common.collect.RangeMap

enum class CircleManagerSettings(
    val gameMode: GameMode,
    val startCircleDuration: Long,
    val minCircleDuration: Long,
    val startCircleInterval: Long,
    val minCircleInterval: Long,
    val circleDurationDecelerationMap: RangeMap<Int, Long>,
    val circleIntervalDecelerationMap: RangeMap<Int, Long>
) {
    EASY(
        GameMode.EASY,
        3500L,
        2000L,
        1750L,
        500L,
        Utils.buildRangeMap(
            listOf(
                Pair(70, 80),
                Pair(85, 100),
                Pair(100, 200)
            )
        ),
        Utils.buildRangeMap(
            listOf(
                Pair(50, 60),
                Pair(75, 80),
                Pair(100, 120)
            )
        )
    ),
    HARD(
        GameMode.HARD,
        2500L,
        1500L,
        1000L,
        500L,
        Utils.buildRangeMap(
            listOf(
                Pair(70, 80),
                Pair(85, 100),
                Pair(100, 200)
            )
        ),
        Utils.buildRangeMap(
            listOf(
                Pair(50, 60),
                Pair(75, 80),
                Pair(100, 200)
            )
        )
    )
}