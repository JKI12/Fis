package me.king.jake.fis.camera

import me.king.jake.fis.views.GraphicOverlay
import java.nio.ByteBuffer

interface  FrameProcessor {
    fun process(
        byteBuffer: ByteBuffer,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    )

    fun stop() {}
}
