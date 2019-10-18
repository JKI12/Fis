package me.king.jake.fis.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import androidx.core.content.ContextCompat
import me.king.jake.fis.R
import me.king.jake.fis.animations.CameraReticleAnimator
import me.king.jake.fis.views.GraphicOverlay

internal class BarcodeReticle(overlay: GraphicOverlay, private val animator: CameraReticleAnimator):
    BarcodeGraphicBase(overlay) {

    private val ripplePaint = Paint().apply {
        style = Style.STROKE
        color = ContextCompat.getColor(context, R.color.barcode_reticle_ripple)
    }

    private val rippleSizeOffset: Int
    private val rippleStrokeWidth: Int
    private val rippleAlpha = ripplePaint.alpha

    init {
        val resources = overlay.resources
        rippleSizeOffset = resources.getDimensionPixelOffset(R.dimen.reticle_ripple_size_offset)
        rippleStrokeWidth = resources.getDimensionPixelOffset(R.dimen.reticle_ripple_stroke_width)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        ripplePaint.alpha = (rippleAlpha * animator.rippleAlphaScale).toInt()
        ripplePaint.strokeWidth = rippleStrokeWidth * animator.rippleStrokeWidthScale
        val offset = rippleSizeOffset * animator.rippleSizeScale
        val rippleRect = RectF(
            boxRect.left - offset,
            boxRect.top - offset,
            boxRect.right + offset,
            boxRect.bottom + offset
        )

        canvas.drawRoundRect(rippleRect, boxCornerRadius, boxCornerRadius, ripplePaint)
    }
}