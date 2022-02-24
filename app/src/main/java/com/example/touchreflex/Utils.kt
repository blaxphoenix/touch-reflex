package com.example.touchreflex

import kotlin.random.Random

class Utils {

    companion object {
        fun nextFloat(min: Float, max: Float): Float {
            return Random.nextFloat() * (max - min) + min
        }

        fun nextLongWithMargin(value: Long, margin: Long = 150L): Long {
            return Random.nextLong(value - margin, value + margin)
        }
    }

}