package com.example.touchreflex.utils

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Utils {

    companion object {
        fun nextFloat(min: Float, max: Float): Float =
            Random.nextFloat() * (max - min) + min

        fun nextLongWithMargin(value: Long, margin: Long = 150L): Long =
            Random.nextLong(value - margin, value + margin)

        fun isInBoundaryCircle(x1: Float, x2: Float, y1: Float, y2: Float, r: Float): Boolean =
            (x1 - x2).pow(2) + (y1 - y2).pow(2) <= r.pow(2)

        fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
            sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

    }

}