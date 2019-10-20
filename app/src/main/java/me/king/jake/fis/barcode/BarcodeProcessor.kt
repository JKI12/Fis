package me.king.jake.fis.barcode

import android.animation.ValueAnimator
import android.os.Handler
import android.util.Log
import androidx.core.animation.addListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import me.king.jake.fis.Api
import me.king.jake.fis.animations.CameraReticleAnimator
import me.king.jake.fis.camera.FrameProcessorBase
import me.king.jake.fis.camera.WorkflowModel
import me.king.jake.fis.graphics.BarcodeLoading
import me.king.jake.fis.graphics.BarcodeReticle
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException

class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val workflowModel: WorkflowModel) : FrameProcessorBase<List<FirebaseVisionBarcode>>() {
    private val TAG = this.javaClass.canonicalName

    private val cameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

    private val detector : FirebaseVisionBarcodeDetector by lazy {
        FirebaseVision.getInstance().visionBarcodeDetector
    }

    override fun detectBarcode(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> = detector.detectInImage(image)

    override fun onSuccess(
        image: FirebaseVisionImage,
        results: List<FirebaseVisionBarcode>,
        graphicOverlay: GraphicOverlay
    ) {
        if (!workflowModel.isCameraLive) {
            return
        }

        val barcodeInCentre = results.firstOrNull { barcode ->
            val boundingBox = barcode.boundingBox ?: return@firstOrNull false
            val box = graphicOverlay.translateRect(boundingBox)
            box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
        }

        graphicOverlay.clear()

        if (barcodeInCentre == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticle(graphicOverlay, cameraReticleAnimator))
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        } else {
            cameraReticleAnimator.cancel()
            val loadingAnimator = createLoadingAnimator(graphicOverlay)
            loadingAnimator.start()
            graphicOverlay.add(BarcodeLoading(graphicOverlay, loadingAnimator))
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHING)

            if (barcodeInCentre.rawValue !== null) {
                try {
                    val mainThreadHandler = Handler(graphicOverlay.context.mainLooper)

                    Api.sendBarcode(barcodeInCentre.rawValue!!) {
                        mainThreadHandler.post {
                            workflowModel.detectedBarcode.value = it
                            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.SEARCHED)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message ?: "There has been an error")
                }
            }
        }

        graphicOverlay.invalidate()
    }

    private fun createLoadingAnimator(graphicOverlay: GraphicOverlay) : ValueAnimator {
        val endProgress = 1f

        return ValueAnimator.ofFloat(0f, endProgress).apply {
            duration = 2000
            addUpdateListener {
                if (workflowModel.workflowState.value == WorkflowModel.WorkflowState.SEARCHING) {
                    graphicOverlay.invalidate()
                }
            }
            addListener({
                it.start()
            })
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed!", e)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close barcode detector", e)
        }
    }
}