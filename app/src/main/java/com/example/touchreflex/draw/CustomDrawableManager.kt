package com.example.touchreflex.draw

interface CustomDrawableManager : CustomDrawable {
    fun init(): CustomDrawableManager
    fun onTouch(touchX: Float, touchY: Float)
    fun onPause()
    fun onStop()
}