package me.king.jake.fis.camera

import android.os.SystemClock
import android.util.Log
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import me.king.jake.fis.views.GraphicOverlay
import java.lang.Exception
import java.nio.ByteBuffer

abstract class FrameProcessorBase<T> : FrameProcessor {
    private val TAG = this.javaClass.canonicalName

    @GuardedBy("this")
    private var latestFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var latestFrameMetaData: FrameMetadata? = null

    @GuardedBy("this")
    private var processingFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var processingFrameMetadata: FrameMetadata? = null

    @Synchronized
    override fun process(
        data: ByteBuffer,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        latestFrame = data
        latestFrameMetaData = frameMetadata

        if (processingFrame == null && processingFrameMetadata == null) {
            processLatestFrame(graphicOverlay)
        }
    }

    @Synchronized
    private fun processLatestFrame(graphicOverlay: GraphicOverlay) {
        processingFrame = latestFrame
        processingFrameMetadata = latestFrameMetaData
        latestFrame = null
        latestFrameMetaData = null

        val frame = processingFrame ?: return
        val frameMetadata = processingFrameMetadata ?: return
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation)
            .build()

        val image = FirebaseVisionImage.fromByteBuffer(frame, metadata)
        val startMs = SystemClock.elapsedRealtime()

        detectBarcode(image)
            .addOnSuccessListener { results ->
                Log.d(TAG, "Latency is ${SystemClock.elapsedRealtime() - startMs}")
                this@FrameProcessorBase.onSuccess(image, results, graphicOverlay)
                processLatestFrame(graphicOverlay)
            }
            .addOnFailureListener { this@FrameProcessorBase.onFailure(it) }
    }

    protected abstract fun detectBarcode(image: FirebaseVisionImage): Task<T>

    protected abstract fun onSuccess(
        image: FirebaseVisionImage,
        results: T,
        graphicOverlay: GraphicOverlay
    )

    protected abstract fun onFailure(e: Exception)
}