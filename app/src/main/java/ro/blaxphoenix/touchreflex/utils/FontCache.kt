package ro.blaxphoenix.touchreflex.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import java.util.*

class FontCache {

    companion object {
        private val CACHE: Hashtable<Int, Typeface> = Hashtable()

        fun get(fontResource: Int, context: Context): Typeface? =
            CACHE[fontResource] ?: ResourcesCompat.getFont(context, fontResource)
    }

}