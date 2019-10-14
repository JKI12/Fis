package me.king.jake.fis.fragments

import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.king.jake.fis.R
import me.king.jake.fis.camera.CameraSource
import me.king.jake.fis.views.CameraSourcePeview
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException

class BarcodeScannerFragment : Fragment() {
    private val TAG = this.javaClass.canonicalName

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePeview? = null
    private var graphicOverlay: GraphicOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_barcode_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preview = view.findViewById(R.id.camera_preview)

        graphicOverlay = view.findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            cameraSource = CameraSource(this)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_flash).setOnClickListener {
            if (it.isSelected) {
                it.isSelected = false
                cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
            } else {
                it.isSelected = true
                cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
            }
        }
    }

    override fun onResume() {
        super.onResume()
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