package com.example.touchreflex.draw.circle

import android.view.View

class DemoCompositeCircleDrawableManager(parentView: View) :
    InfiniteCompositeCircleDrawableManager(parentView) {

    init {
        alpha = 0x66
    }

    override fun onPause() =
        circles.forEach { if (it.isDone) circles.remove(it) }

}