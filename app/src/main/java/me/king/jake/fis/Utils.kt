package me.king.jake.fis

import android.hardware.Camera
import android.util.Log
import me.king.jake.fis.camera.CameraSizePair
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
}