package me.king.jake.fis.camera

import android.hardware.Camera
import android.util.Size

class CameraSizePair {
    val preview: Size
    val picture: Size?

    constructor(previewSize: Camera.Size, pictureSize: Camera.Size?) {
        preview = Size(previewSize.width, previewSize.height)
        picture = pictureSize?.let { Size(it.width, it.height) }
    }

    constructor(previewSize: Size, pictureSize: Size?) {
        preview = previewSize
        picture = pictureSize
    }
}