package me.king.jake.fis.fragments

import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.king.jake.fis.R
import me.king.jake.fis.camera.CameraSource
import me.king.jake.fis.camera.WorkflowModel
import me.king.jake.fis.views.CameraSourcePeview
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException
import java.util.*

class BarcodeScannerFragment : Fragment() {
    private val TAG = this.javaClass.canonicalName

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePeview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null

    private var infoChip: Chip? = null
    private var flashFab: FloatingActionButton? = null

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

        flashFab = view.findViewById(R.id.fab_flash)

        flashFab!!.setOnClickListener {
            if (it.isSelected) {
                it.isSelected = false
                cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
            } else {
                it.isSelected = true
                cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
            }
        }

        infoChip = view.findViewById(R.id.info_chip)

        setupWorkflowModel()
    }

    private fun setupWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState

            when (workflowState) {
                WorkflowModel.WorkflowState.DETECTING -> {
                    infoChip?.visibility = View.VISIBLE
                    infoChip?.setText(R.string.move_closer)
                    startCameraPreview()
                }
                WorkflowModel.WorkflowState.CONFIRMING -> {
                    infoChip?.visibility = View.VISIBLE
                    infoChip?.setText(R.string.move_closer)
                    startCameraPreview()
                }
                WorkflowModel.WorkflowState.SEARCHING -> {
                    infoChip?.visibility = View.VISIBLE
                    infoChip?.setText(R.string.searching)
                    stopCameraPreview()
                }
                WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.SEARCHED -> {
                    infoChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> infoChip?.visibility = View.GONE
            }
        })
    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
//        cameraSource?.setFrameProcessor()
        workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return

        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return

        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashFab?.isSelected = false
            preview?.stop()
        }
    }
}