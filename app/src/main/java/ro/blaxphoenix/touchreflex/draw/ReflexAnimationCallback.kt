package ro.blaxphoenix.touchreflex.draw

interface ReflexAnimationCallback {
    fun onScored()
    fun onGameOver(xCenter: Float, yCenter: Float)
}