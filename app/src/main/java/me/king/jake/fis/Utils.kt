package me.king.jake.fis

import android.graphics.RectF
import android.hardware.Camera
import android.text.InputType
import android.util.Log
import android.widget.TextView
import me.king.jake.fis.camera.CameraSizePair
import me.king.jake.fis.views.GraphicOverlay
import kotlin.math.abs

object Utils {
    const val ASPECT_RATIO_TOLERANCE = 0.01f
    private var TAG = this.javaClass.canonicalName

    fun generateValidPreviewSizeList(camera: Camera): List<CameraSizePair> {
        val parameters = camera.parameters
        val supportedPreviewSizes = parameters.supportedPreviewSizes
        val supportedPictureSizes = parameters.supportedPictureSizes
        val validPreviewSizes = ArrayList<CameraSizePair>()

        for(previewSize in supportedPreviewSizes) {
            val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()

            for (pictureSize in supportedPictureSizes) {
                val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()

                if (abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                    validPreviewSizes.add(CameraSizePair(previewSize, pictureSize))
                    break
                }
            }
        }

        if (validPreviewSizes.isEmpty()) {
            Log.w(TAG, "No Preview sizes have a corresponding same aspect ratio picture size")

            for (previewSize in supportedPreviewSizes) {
                validPreviewSizes.add(CameraSizePair(previewSize, null))
            }
        }

        return validPreviewSizes
    }

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        overlay.context
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight= overlay.height.toFloat()
        val boxWidth = overlayWidth * 80 / 100
        val boxHeight = overlayHeight * 30 / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2

        return RectF(
            cx - boxWidth / 2,
            cy - boxHeight / 2,
            cx + boxWidth / 2,
            cy + boxHeight / 2
        )
    }

    fun disableTextView(view: TextView) {
        view.apply {
            inputType = InputType.TYPE_NULL
            isClickable = false
            isFocusable = false
        }
    }

    fun enableTextView(view: TextView, newInputType: Int = InputType.TYPE_CLASS_TEXT) {
        view.apply {
            inputType = newInputType
            isClickable = true
            isFocusable = true
        }
    }
}