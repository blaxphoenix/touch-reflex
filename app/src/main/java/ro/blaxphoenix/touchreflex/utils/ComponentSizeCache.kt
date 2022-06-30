package ro.blaxphoenix.touchreflex.utils

import java.util.*

class ComponentSizeCache {
    enum class SizeType(val size: Float) {
        MAX_CIRCLE_RADIUS(120f),
        MAX_SMALL_TEXT_SIZE(60f),
        MAX_DEFAULT_TEXT_SIZE(100f),
        MAX_BUTTON_TEXT_SIZE(MAX_DEFAULT_TEXT_SIZE.size),
        MAX_BUTTON_WIDTH(450f),
        MAX_BUTTON_HEIGHT(MAX_BUTTON_WIDTH.size / 2),
        MAX_IMAGE_SIZE(100f),
        MAX_SHADOW_PARAMS(6f)
    }

    companion object {
        private val CACHE: EnumMap<SizeType, Float> =
            EnumMap(SizeType.values().associateWith { it.size })

        fun update(width: Int) {
            CACHE.putAll(SizeType.values().associateWith { computeSize(it.size, width) })
        }

        fun getSize(sizeType: SizeType): Float = CACHE[sizeType]!!

        private fun computeSize(maxSize: Float, width: Int): Float =
            if (width >= Utils.DEFAULT_SCREEN_WIDTH) {
                maxSize
            } else {
                width.toFloat() / Utils.DEFAULT_SCREEN_WIDTH * maxSize
            }
    }

}