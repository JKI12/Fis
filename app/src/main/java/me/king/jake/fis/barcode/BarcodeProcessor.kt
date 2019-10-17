package me.king.jake.fis.barcode

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import me.king.jake.fis.camera.FrameProcessorBase
import me.king.jake.fis.camera.WorkflowModel
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException
import java.lang.Exception

class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val workflowModel: WorkflowModel) : FrameProcessorBase<List<FirebaseVisionBarcode>>() {
    private val TAG = this.javaClass.canonicalName

    private val detector = FirebaseVision.getInstance().visionBarcodeDetector


    override fun detectBarcode(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> = detector.detectInImage(image)

    override fun onSuccess(
        image: FirebaseVisionImage,
        results: List<FirebaseVisionBarcode>,
        graphicOverlay: GraphicOverlay
    ) {
        Log.d(TAG, "Barcode result size: ${results.size}")
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