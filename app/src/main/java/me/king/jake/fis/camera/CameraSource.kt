package me.king.jake.fis.camera

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import me.king.jake.fis.R
import me.king.jake.fis.Utils
import me.king.jake.fis.settings.PreferenceUtils
import me.king.jake.fis.views.GraphicOverlay
import java.io.IOException
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

@Suppress("DEPRECATION")
class CameraSource(private val graphicOverlay: GraphicOverlay) {
    private var TAG = this.javaClass.canonicalName

    private var camera: Camera? = null

    @FirebaseVisionImageMetadata.Rotation
    private var rotation = 0

    internal var previewSize: Size? = null
        private set

    private var processingThread: Thread? = null
    private val processingRunnable = FrameProcessingRunnable()

    private val processorLock = Object()
    private var frameProcessor: FrameProcessor? = null

    private val bytesToByteBuffer = IdentityHashMap<ByteArray, ByteBuffer>()
    private val context: Context = graphicOverlay.context

    @Synchronized
    @Throws(IOException::class)
    internal fun start(surfaceHolder: SurfaceHolder) {
        if (camera !== null) {
            return
        }

        camera = createCamera()
            .apply {
                setPreviewDisplay(surfaceHolder)
                startPreview()
            }

        processingThread = Thread(processingRunnable)
            .apply {
                processingRunnable.setActive(true)
                start()
            }
    }

    @Synchronized
    internal fun stop() {
        processingRunnable.setActive(false)

        processingThread?.let {
            try {
                it.join()
            } catch (e: InterruptedException) {
                Log.e(TAG, "Frame processing thread interrupted on stop")
            }

            processingThread = null
        }

        camera?.let {
            it.stopPreview()
            it.setPreviewCallbackWithBuffer(null)

            try {
                it.setPreviewDisplay(null)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear camera preview: $e")
            }

            it.release()
            camera = null
        }

        bytesToByteBuffer.clear()
    }

    fun release() {
        graphicOverlay.clear()

        synchronized(processorLock) {
            stop()
            frameProcessor?.stop()
        }
    }

    fun setFrameProcessor(processor: FrameProcessor) {
        graphicOverlay.clear()

        synchronized(processorLock) {
            frameProcessor?.stop()
            frameProcessor = processor
        }
    }

    fun updateFlashMode(flashMode: String) {
        val parameters = camera?.parameters
        parameters?.flashMode = flashMode
        camera?.parameters = parameters
    }

    @Throws(IOException::class)
    private fun createCamera(): Camera {
        val camera = Camera.open() ?: throw IOException("No back facing camera")
        val parameters = camera.parameters
        setPreviewAndPictureSize(camera, parameters)
        setRotation(camera, parameters)

        val previewFpsRange = selectPreviewFpsRange(camera) ?: throw IOException("Could not find suitable preview fps range")

        parameters.setPreviewFpsRange(
            previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
            previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
        )

        parameters.previewFormat = IMAGE_FORMAT

        if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        } else {
            Log.i(TAG, "Camera auto focus not supported")
        }

        camera.parameters = parameters

        camera.setPreviewCallbackWithBuffer(processingRunnable::setNextFrame)

        previewSize?.let {
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
        }

        return camera
    }

    @Throws(IOException::class)
    private fun setPreviewAndPictureSize(
        camera: Camera,
        parameters: Camera.Parameters
    ) {
        val sizePair = PreferenceUtils.getUserSpecifiedPreviewSize(context) ?:
            selectSizePair(camera, (graphicOverlay.height.toFloat() / graphicOverlay.width.toFloat())) ?:
            throw IOException("Could not find suitable preview size")

        previewSize = sizePair.preview.also {
            Log.v(TAG, "Preview Size: $it")
            parameters.setPreviewSize(it.width, it.height)
            PreferenceUtils.saveStringPreference(context, R.string.pref_key_rear_camera_preview_size, it.toString())
        }

        sizePair.picture?.let {
            Log.v(TAG, "Camera picture size: $it")
            parameters.setPictureSize(it.width, it.height)
            PreferenceUtils.saveStringPreference(context, R.string.pref_key_rear_camera_picture_size, it.toString())
        }
    }

    private fun setRotation(camera: Camera, parameters: Camera.Parameters) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val degrees = when (val deviceRotation = windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> {
                Log.e(TAG, "Bad device rotation: $deviceRotation")
                0
            }
        }

        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(CAMERA_FACING_BACK, cameraInfo)

        val angle = (cameraInfo.orientation - degrees + 360) % 360
        this.rotation = angle / 90

        camera.setDisplayOrientation(angle)
        parameters.setRotation(angle)
    }

    private fun createPreviewBuffer(previewSize: Size): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(IMAGE_FORMAT)
        val sizeInBits = previewSize.height.toLong() * previewSize.width.toLong() * bitsPerPixel.toLong()
        val bufferSize = ceil(sizeInBits / 8.0).toInt() + 1

        val byteArray = ByteArray(bufferSize)
        val byteBuffer = ByteBuffer.wrap(byteArray)

        check(!(!byteBuffer.hasArray() || !byteBuffer.array()!!.contentEquals(byteArray))) {
            "Failed to create valid buffer"
        }

        bytesToByteBuffer[byteArray] = byteBuffer
        return byteArray
    }

    private inner class FrameProcessingRunnable internal constructor() : Runnable {
        private val lock = Object()
        private var active = true

        private var pendingFrameData: ByteBuffer? = null

        internal fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }

        internal fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(lock) {
                pendingFrameData?.let {
                    camera.addCallbackBuffer(it.array())
                    pendingFrameData = null
                }

                if (!bytesToByteBuffer.containsKey(data)) {
                    Log.d(TAG, "Skipping frame. Could not find ByteBuffer associated with the image data from the camera")
                    return
                }

                pendingFrameData = bytesToByteBuffer[data]
                lock.notifyAll()
            }
        }

        override fun run() {
            var data: ByteBuffer?

            while (true) {
                synchronized(lock) {
                    while (active && pendingFrameData == null) {
                        try {
                            lock.wait()
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Frame processing loop terminated", e)
                            return
                        }
                    }

                    if (!active) {
                        return
                    }

                    data = pendingFrameData
                    pendingFrameData = null
                }

                try {
                    synchronized(processorLock) {}
                    val frameMetadata = FrameMetadata(previewSize!!.width, previewSize!!.height, rotation)
                    data?.let {
                        frameProcessor?.process(it, frameMetadata, graphicOverlay)
                    }
                } catch (t: Exception) {
                    Log.e(TAG, "Exception from receiver", t)
                } finally {
                    data?.let {
                        camera?.addCallbackBuffer(it.array())
                    }
                }
            }
        }
    }

    companion object {
        private const val IMAGE_FORMAT = ImageFormat.NV21
        const val CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK

        private const val MIN_CAMERA_PREVIEW_WIDTH = 400
        private const val MAX_CAMERA_PREVIEW_WIDTH = 1300
        private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH = 640
        private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT = 360
        private const val REQUESTED_CAMERA_FPS = 30.0f

        private fun selectSizePair(camera: Camera, aspectRatio: Float) : CameraSizePair? {
            val validPreviewSizes = Utils.generateValidPreviewSizeList(camera)
            var selectedPair: CameraSizePair? = null

            var minAspectRatioDiff = Float.MAX_VALUE

            for (sizePair in validPreviewSizes) {
                val previewSize = sizePair.preview
                if (previewSize.width < MIN_CAMERA_PREVIEW_WIDTH || previewSize.width > MAX_CAMERA_PREVIEW_WIDTH) {
                    continue
                }

                val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()
                val aspectRatioDiff = abs(aspectRatio - previewAspectRatio)
                if (abs(aspectRatioDiff - minAspectRatioDiff) < Utils.ASPECT_RATIO_TOLERANCE) {
                    if (selectedPair == null || selectedPair.preview.width < sizePair.preview.width) {
                        selectedPair = sizePair
                    }
                } else if (aspectRatioDiff < minAspectRatioDiff) {
                    minAspectRatioDiff = aspectRatioDiff
                    selectedPair = sizePair
                }
            }

            if (selectedPair == null) {
                var minDiff = Integer.MAX_VALUE
                for (sizePair in validPreviewSizes) {
                    val size = sizePair.preview
                    val diff = abs(size.width - DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH) +
                                abs(size.height - DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT)

                    if (diff < minDiff) {
                        selectedPair = sizePair
                        minDiff = diff
                    }
                }
            }

            return selectedPair
        }

        private fun selectPreviewFpsRange(camera: Camera): IntArray? {
            val desiredPreviewFpsScaled = (REQUESTED_CAMERA_FPS * 1000f).toInt()

            var selectedFpsRange: IntArray? = null
            var minDiff = Integer.MAX_VALUE

            for (range in camera.parameters.supportedPreviewFpsRange) {
                val deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                val diff = abs(deltaMin) + abs(deltaMax)
                if (diff < minDiff) {
                    selectedFpsRange = range
                    minDiff = diff
                }
            }

            return selectedFpsRange
        }
    }
}