package com.example.touchreflex.draw

import android.graphics.Canvas

interface CustomDrawable {
    fun onStartDrawing()
    fun onDraw(canvas: Canvas)
    fun onDisable()
}