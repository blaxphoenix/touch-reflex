package ro.blaxphoenix.touchreflex.utils

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.annotation.IntRange
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import ro.blaxphoenix.touchreflex.R
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt
import kotlin.random.Random

class Utils {
    companion object {
        @IntRange(from = 1, to = 15)
        const val MAX_NUMBER_OF_CIRCLES_AT_ONCE: Int = 12
        const val DEFAULT_SCREEN_WIDTH: Int = 1440

        fun nextFloat(min: Float, max: Float): Float =
            Random.nextFloat() * (max - min) + min

        fun nextLongWithMargin(value: Long, margin: Long = (value * .05f).roundToLong()): Long =
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

        fun vibrate(
            context: Context,
            lengthMillis: Long = 200,
            @IntRange(from = 0, to = 255) amplitude: Int = 100
        ) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(lengthMillis, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(lengthMillis)
            }
        }

        fun setPaintShadowLayer(paint: Paint, parentView: View) =
            paint.setShadowLayer(
                ComponentSizeCache.getSize(ComponentSizeCache.SizeType.MAX_SHADOW_PARAMS),
                ComponentSizeCache.getSize(ComponentSizeCache.SizeType.MAX_SHADOW_PARAMS),
                ComponentSizeCache.getSize(ComponentSizeCache.SizeType.MAX_SHADOW_PARAMS),
                if (Build.VERSION.SDK_INT >= 23) {
                    parentView.resources.getColor(R.color.grey, null)
                } else {
                    @Suppress("DEPRECATION")
                    parentView.resources.getColor(R.color.grey)
                }
            )
    }
}