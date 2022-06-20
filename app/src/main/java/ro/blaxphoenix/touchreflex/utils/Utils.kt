package ro.blaxphoenix.touchreflex.utils

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt
import kotlin.random.Random

class Utils {
    companion object {
        const val MAX_NUMBER_OF_CIRCLES_AT_ONCE = 12
        @Suppress("MemberVisibilityCanBePrivate")
        const val DEFAULT_SCREEN_WIDTH: Int = 1080
        const val MAX_CIRCLE_RADIUS: Float = 120f
        const val MAX_SMALL_TEXT_SIZE: Float = 60f
        const val MAX_DEFAULT_TEXT_SIZE: Float = 100f
        const val MAX_BUTTON_TEXT_SIZE: Float = MAX_DEFAULT_TEXT_SIZE
        const val MAX_BUTTON_WIDTH: Float = 450f
        const val MAX_BUTTON_HEIGHT: Float = MAX_BUTTON_WIDTH / 2
        const val MAX_IMAGE_SIZE: Float = 100f

        // TODO better solution, reCalculate() and then cache values (enum map?)
        fun getSize(maxSize: Float, width: Int, modifier: Float = 0.88f): Float =
            if (width >= DEFAULT_SCREEN_WIDTH) {
                maxSize
            } else {
                width.toFloat() / DEFAULT_SCREEN_WIDTH * maxSize * modifier
            }

        fun nextFloat(min: Float, max: Float): Float =
            Random.nextFloat() * (max - min) + min

        fun nextLongWithMargin(value: Long, margin: Long = (value * 0.05f).roundToLong()): Long =
            max(0L, Random.nextLong(value - margin, value + margin))

        fun isInBoundaryCircle(x1: Float, y1: Float, x2: Float, y2: Float, r: Float): Boolean =
            (x1 - x2).pow(2) + (y1 - y2).pow(2) <= r.pow(2)

        fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
            sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

        /**
         * pair = <rangeEnd, decelerationValue>
         *     firstPrevious = 0
         *     closedOpen(previous, rangeEnd)
         */
        @Suppress("UnstableApiUsage")
        fun buildRangeMap(configList: List<Pair<Int, Long>>): RangeMap<Int, Long> {
            val map: RangeMap<Int, Long> = TreeRangeMap.create()
            var rangeStart = 0
            configList.forEach {
                val (rangeEnd, value) = it
                map.put(Range.closedOpen(rangeStart, rangeEnd), value)
                rangeStart = rangeEnd
            }
            return map
        }
    }
}