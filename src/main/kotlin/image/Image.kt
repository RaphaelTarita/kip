package image

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs

class Image(val width: Int, val height: Int, private val pdata: ByteArray, private val pixelView: PixelView) {
    constructor(img: BufferedImage) : this(
        img.width,
        img.height,
        (img.raster.dataBuffer as DataBufferByte).data,
        PixelView(img)
    )

    constructor(path: String) : this(ImageIO.read(File(path)))

    fun onEachPixel(action: (PixelColour) -> PixelColour) {
        repeat(width * height) {
            pixelView.apply(pdata, action)
            pixelView.moveRight()
        }
    }

    fun onEachPixel(action: (Int, Int, PixelColour) -> PixelColour) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixelView.set(x, y)
                pixelView.apply(pdata) {
                    action(x, y, it)
                }
            }
        }
    }

    fun onEachPixel(action: (PixelColour, BoundPixelView) -> PixelColour) {
        val boundpw = BoundPixelView(pixelView, pdata)
        repeat(width * height) {
            pixelView.apply(pdata) { action(it, boundpw) }
            pixelView.moveRight()
        }
    }

    fun pixelData() = pdata

    infix fun minus(other: Image): Image {
        require(pdata.size == other.pdata.size)
        val result = ByteArray(pdata.size)
        for (i in pdata.indices) {
            result[i] = abs(pdata[i].toUnsignedInt() - other.pdata[i].toUnsignedInt()).toByte()
        }
        return Image(width, height, result, PixelView(0, 0, pixelView.hasAlpha(), width, height))
    }
}