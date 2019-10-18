package me.king.jake.fis.graphics

import android.graphics.*
import android.graphics.Paint.Style
import androidx.core.content.ContextCompat
import me.king.jake.fis.R
import me.king.jake.fis.Utils
import me.king.jake.fis.views.GraphicOverlay
import me.king.jake.fis.views.GraphicOverlay.Graphic

interface abstract open class BarcodeGraphicBase(overlay: GraphicOverlay): Graphic(overlay) {
    private val boxPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_stroke)
        style = Style.STROKE
        strokeWidth = context.resources.getDimensionPixelOffset(R.dimen.reticle_stroke).toFloat()
    }

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_background)
    }

    private val eraserPaint = Paint().apply {
        strokeWidth = boxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    internal val boxCornerRadius = context.resources.getDimensionPixelOffset(R.dimen.reticle_corner_radius).toFloat()

    val pathPaint = Paint().apply {
        color = Color.WHITE
        style = Style.STROKE
        strokeWidth = boxPaint.strokeWidth
        pathEffect = CornerPathEffect(boxCornerRadius)
    }

    val boxRect = Utils.getBarcodeReticleBox(overlay)

    override fun draw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        eraserPaint.style = Style.FILL
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)
        eraserPaint.style = Style.STROKE
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint)
    }

}