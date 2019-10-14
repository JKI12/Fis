package me.king.jake.fis.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import me.king.jake.fis.camera.CameraSource

class GraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val lock = Any()

    private var previewWidth: Int = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight: Int = 0
    private var heightScaleFactor = 1.0f
    private val graphics = ArrayList<Graphic>()

    abstract class Graphic protected constructor(protected val overlay: GraphicOverlay) {
        protected val context: Context = overlay.context
        abstract fun draw(canvas: Canvas)
    }

    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }

        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) {
            graphics.add(graphic)
        }
    }

    fun setCameraInfo(cameraSource: CameraSource) {
        val previewSize = cameraSource.previewSize ?: return

        previewWidth = previewSize.height
        previewHeight = previewSize.width
    }

    fun translateX(x: Float): Float = x * widthScaleFactor
    fun transalteY(y: Float): Float = y * heightScaleFactor

    fun translateRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        transalteY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        transalteY(rect.bottom.toFloat())
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (previewWidth > 0 && previewHeight > 0) {
            widthScaleFactor = width.toFloat() / previewWidth
            heightScaleFactor = height.toFloat() / previewHeight
        }

        synchronized(lock) {
            graphics.forEach { it.draw(canvas) }
        }
    }
}