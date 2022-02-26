package com.example.touchreflex.draw.circle

import com.example.touchreflex.draw.CustomDrawable

interface CircleManager : CustomDrawable {
    fun init(): CircleManager
    fun onTouch(touchX: Float, touchY: Float)
    fun onPause()
}