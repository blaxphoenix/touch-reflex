package ro.blaxphoenix.touchreflex.utils

import android.view.animation.Interpolator

class ReverseInterpolator(private val delegateInterpolator: Interpolator) : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return 1 - delegateInterpolator.getInterpolation(input)
    }
}