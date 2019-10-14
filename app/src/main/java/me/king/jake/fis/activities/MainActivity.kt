package me.king.jake.fis.activities

import android.os.Bundle
import android.util.Log
import me.king.jake.fis.R
import me.king.jake.fis.camera.CameraSource
import me.king.jake.fis.views.CameraSourcePeview
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException

class MainActivity : BaseActivity() {
    private val TAG = this.javaClass.canonicalName

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePeview? = null
    private var graphicOverlay: GraphicOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_barcode_camera)

        preview = findViewById(R.id.camera_preview)

        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            cameraSource = CameraSource(this)
        }

        startCameraPreview()
    }

    override fun onPause() {
        super.onPause()
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    private fun startCameraPreview() {
        val cameraSource = this.cameraSource ?: return

        try {
            preview?.start(cameraSource)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start camera preview", e)
            cameraSource.release()
            this.cameraSource = null
        }
    }

    private fun stopCameraPreview() {
        preview?.stop()
    }
}
