package me.king.jake.fis.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import me.king.jake.fis.R
import me.king.jake.fis.camera.CameraSource
import java.io.IOException
import java.lang.Exception

class CameraSourcePeview(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {
    private val TAG = this.javaClass.canonicalName

    private val surfaceView: SurfaceView = SurfaceView(context).apply {
        holder.addCallback(SurfaceCallback())
        addView(this)
    }

    private var graphicOverlay: GraphicOverlay? = null
    private var startRequested = false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null
    private var cameraPreviewSize: Size? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource) {
        this.cameraSource = cameraSource
        startRequested = true
        startIfReady()
    }

    fun stop() {
        cameraSource?.let {
            it.stop()
            cameraSource = null
            startRequested = false
        }
    }

    @Throws(IOException::class)
    fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource?.start(surfaceView.holder)
            requestLayout()

            graphicOverlay?.let { overlay ->
                cameraSource?.let {
                    overlay.setCameraInfo(it)
                }

                overlay.clear()
            }

            startRequested = false
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top

        cameraSource?.previewSize?.let {
            cameraPreviewSize = it
        }

        val previewSizeRatio = cameraPreviewSize?.let { size ->
            size.height.toFloat() / size.width
        } ?: layoutWidth.toFloat() / layoutHeight

        val childHeight = (layoutWidth / previewSizeRatio).toInt()

        if (childHeight <= layoutHeight) {
            for (i in 0 until childCount) {
                getChildAt(i).layout(0, 0, layoutWidth, layoutHeight)
            }
        } else {
            val excessLenInHalf = (childHeight - layoutHeight) / 2
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                when (childView.id) {
                    R.id.static_overlay_container -> {
                        childView.layout(0, 0, layoutWidth, layoutHeight)
                    }
                    else -> {
                        childView.layout(
                            0, -excessLenInHalf, layoutWidth, layoutHeight + excessLenInHalf)
                    }
                }
            }
        }

        try {
            startIfReady()
        } catch (e: Exception) {
            Log.e(TAG, "Could not start camera source", e)
        }
    }

    private inner class SurfaceCallback :SurfaceHolder.Callback {
        override fun surfaceChanged(holdr: SurfaceHolder?, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(surface: SurfaceHolder?) {
            surfaceAvailable = false
        }

        override fun surfaceCreated(surface: SurfaceHolder?) {
            surfaceAvailable = true

            try {
                startIfReady()
            } catch (e: Exception) {
                Log.e(TAG, "Could not start camera", e)
            }
        }
    }
}