package image

import util.OverflowHandling
import util.coerce
import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs

class Step(
    val width: Int,
    val height: Int,
    val hasAlpha: Boolean = false,
    private val data: ByteArray = ByteArray(width * height * if (hasAlpha) 4 else 3)
) : MutablePixelAccess {
    companion object {
        fun blank(width: Int, height: Int, hasAlpha: Boolean = false): Step = Step(width, height, hasAlpha)
    }

    val unit = if (hasAlpha) 4 else 3
    private fun toLocation(x: Int, y: Int): Int = (y * width + x) * unit
    private fun toLocationCoerced(x: Int, y: Int) = toLocation(x.coerce(0, width - 1), y.coerce(0, height - 1))

    constructor(img: BufferedImage) : this(
        img.width,
        img.height,
        img.colorModel.hasAlpha(),
        (img.raster.dataBuffer as DataBufferByte).data,
    )

    constructor(path: String) : this(ImageIO.read(File(path)))

    fun next(): Step = blank(width, height, hasAlpha)
    fun copy(): Step = Step(width, height, hasAlpha, data.copyOf())

    internal fun internalRead(location: Int): PixelColour {
        return PixelColour.fromBytes(
            data[location],
            data[location + 1],
            data[location + 2],
            if (hasAlpha) data[location + 3] else -128
        )
    }

    internal fun internalWrite(location: Int, c: PixelColour) {
        data[location] = c.byteR()
        data[location + 1] = c.byteG()
        data[location + 2] = c.byteB()
        if (hasAlpha) {
            data[location + 3] = c.byteA()
        }
    }

    internal fun internalTransform(location: Int, action: (PixelColour) -> PixelColour) {
        internalWrite(location, action(internalRead(location)))
    }

    override fun read(x: Int, y: Int): PixelColour = internalRead(toLocation(x, y))
    override fun write(x: Int, y: Int, c: PixelColour) = internalWrite(toLocation(x, y), c)
    override fun transform(x: Int, y: Int, action: (PixelColour) -> PixelColour) = internalTransform(toLocation(x, y), action)

    override fun coerceX(x: Int): Int = x.coerce(0, width - 1)
    override fun coerceY(y: Int): Int = y.coerce(0, height - 1)

    override fun readCoerce(x: Int, y: Int): PixelColour = internalRead(toLocationCoerced(x, y))
    override fun writeCoerce(x: Int, y: Int, c: PixelColour) = internalWrite(toLocationCoerced(x, y), c)
    override fun transformCoerce(x: Int, y: Int, action: (PixelColour) -> PixelColour) = internalTransform(toLocationCoerced(x, y), action)

    fun onEachPixel(action: PixelAccess.(PixelColour) -> PixelColour) {
        repeat(width * height) { location ->
            internalTransform(location * unit) { this.action(it) }
        }
    }

    fun onEachPixel(action: PixelAccess.(Int, Int, PixelColour) -> PixelColour) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                transform(x, y) { this.action(x, y, it) }
            }
        }
    }

    fun pixelData() = data

    private fun addInternal(other: Step, calculate: (Int, Int) -> Int): ByteArray {
        val result = ByteArray(data.size)
        for (i in data.indices) {
            result[i] = calculate(data[i].toUByte().toInt(), other.data[i].toUByte().toInt()).toByte()
        }
        return result
    }

    private fun maxAdded(other: Step): Double {
        var max = 0
        for (i in data.indices) {
            val added = data[i].toUByte().toInt() + other.data[i].toUByte().toInt()
            if (added > max) {
                max = added
            }
        }
        return max.toDouble()
    }

    fun add(other: Step, overflowHandling: OverflowHandling = OverflowHandling.CLAMP): Step {
        require(data.size == other.data.size)
        val result = when (overflowHandling) {
            OverflowHandling.NONE -> addInternal(other) { a, b -> a + b }
            OverflowHandling.CLAMP -> addInternal(other) { a, b -> (a + b).coerce(0, 255) }
            OverflowHandling.AVERAGE -> addInternal(other) { a, b -> (a + b) / 2 }
            OverflowHandling.NORMALIZE -> {
                val factor = 255.0 / maxAdded(other)
                addInternal(other) { a, b -> ((a + b) * factor).toInt() }
            }
        }
        return Step(width, height, hasAlpha, result)
    }

    fun subtract(other: Step): Step {
        require(data.size == other.data.size)
        val result = ByteArray(data.size)
        for (i in data.indices) {
            result[i] = abs(data[i].toInt() - other.data[i].toInt()).toByte()
        }
        return Step(width, height, hasAlpha, result)
    }

    fun toBufferedImage(): BufferedImage {
        return BufferedImage(width, height, if (hasAlpha) BufferedImage.TYPE_4BYTE_ABGR else BufferedImage.TYPE_3BYTE_BGR).apply {
            data = Raster.createRaster(
                sampleModel,
                DataBufferByte(this@Step.data, this@Step.data.size),
                Point()
            )
        }
    }
}