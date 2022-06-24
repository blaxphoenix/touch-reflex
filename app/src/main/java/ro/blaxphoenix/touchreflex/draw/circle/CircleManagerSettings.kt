package ro.blaxphoenix.touchreflex.draw.circle

import androidx.annotation.IntRange
import com.google.common.collect.RangeMap
import ro.blaxphoenix.touchreflex.utils.Utils

@Suppress("UnstableApiUsage")
enum class CircleManagerSettings(
    @IntRange(from = 1) val numberOfCirclesToStartWith: Int,
    val startCircleDuration: Long,
    val minCircleDuration: Long,
    val startCircleInterval: Long,
    val minCircleInterval: Long,
    val circleDurationDecelerationMap: RangeMap<Int, Long>,
    val circleIntervalDecelerationMap: RangeMap<Int, Long>
) {
    EASY(
        4,
        3500,
        100,
        1750,
        100,
        Utils.buildRangeMap(
            listOf(
                Pair(7, 100),
                Pair(15, 250),
                Pair(25, 500),
                Pair(50, 1000),
                Pair(75, 5000),
                Pair(100, 10000)
            )
        ),
        Utils.buildRangeMap(
            listOf(
                Pair(7, 150),
                Pair(15, 250),
                Pair(25, 500),
                Pair(50, 1250),
                Pair(75, 7500),
                Pair(100, 12500)
            )
        )
    ),
    HARD(
        4,
        2500,
        100,
        1000,
        100,
        Utils.buildRangeMap(
            listOf(
                Pair(7, 75),
                Pair(15, 175),
                Pair(25, 350),
                Pair(50, 800),
                Pair(75, 3000),
                Pair(100, 8000)
            )
        ),
        Utils.buildRangeMap(
            listOf(
                Pair(7, 100),
                Pair(15, 250),
                Pair(25, 500),
                Pair(50, 1000),
                Pair(75, 5000),
                Pair(100, 10000)
            )
        )
    )
}