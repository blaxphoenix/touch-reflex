package ro.blaxphoenix.touchreflex.draw.image

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import ro.blaxphoenix.touchreflex.draw.CustomDrawable

class SimpleImage(
    drawable: Drawable,
    @ColorInt color: Int,
    private var x: Int,
    private var y: Int,
    private var width: Int,
    private var height: Int,
    var isIgnored: Boolean = false
) : CustomDrawable {

    @ColorInt
    var color: Int = color
        set(value) {
            field = value
            DrawableCompat.setTint(wrappedDrawable, color)
        }

    private val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)

    init {
        DrawableCompat.setTint(wrappedDrawable, color)
        wrappedDrawable.setBounds(x, y, x + width, y + height)
        // TODO shadow
    }

    override fun onStartDrawing() {}

    override fun onDraw(canvas: Canvas) {
        if (!isIgnored) {
            wrappedDrawable.draw(canvas)
        }
    }

    override fun onDisable() {}

    override fun isInBoundary(touchX: Float, touchY: Float): Boolean =
        touchX >= x && touchX <= width && touchY >= y && touchY <= height

    fun setNewSize(x: Int = this.x, y: Int = this.y, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        wrappedDrawable.setBounds(x, y, x + width, y + height)
    }

}