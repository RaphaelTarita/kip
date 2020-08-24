package image

import java.awt.image.BufferedImage

class PixelView private constructor(
    private val hasAlpha: Boolean,
    override val width: Int,
    override val height: Int,
    private var begin: Int = 0
) : _PixelViewActions {
    private fun toLocation(x: Int, y: Int): Int = (y * width + x) * unit

    constructor(x: Int, y: Int, hasAlpha: Boolean, imgWidth: Int, imgHeight: Int) : this(
        hasAlpha,
        imgWidth,
        imgHeight,
        (y * imgWidth + x) * if (hasAlpha) 4 else 3
    )

    constructor(img: BufferedImage, x: Int = 0, y: Int = 0) : this(
        x,
        y,
        img.colorModel.hasAlpha(),
        img.width,
        img.height
    )

    private val unit = if (hasAlpha) 4 else 3

    private val red
        get() = begin
    private val green
        get() = begin + 1
    private val blue
        get() = begin + 2
    private val alpha
        get() = begin + 3

    override val x: Int
        get() = (begin / unit) % width
    override val y: Int
        get() = (begin / unit) / width

    fun hasAlpha() = hasAlpha

    override fun moveRight(steps: Int) {
        begin += unit * steps
    }

    override fun moveDown(steps: Int) {
        begin += unit * width * steps
    }

    override fun moveLeft(steps: Int) {
        begin -= unit * steps
    }

    override fun moveUp(steps: Int) {
        begin -= unit * width * steps
    }

    override fun set(x: Int, y: Int) {
        begin = toLocation(x, y)
    }

    override fun peekRight(imgData: ByteArray, steps: Int): PixelColour {
        moveRight(steps)
        return read(imgData).apply { moveLeft(steps) }
    }

    override fun peekDown(imgData: ByteArray, steps: Int): PixelColour {
        moveDown(steps)
        return read(imgData).apply { moveUp(steps) }
    }

    override fun peekLeft(imgData: ByteArray, steps: Int): PixelColour {
        moveLeft(steps)
        return read(imgData).apply { moveRight(steps) }
    }

    override fun peekUp(imgData: ByteArray, steps: Int): PixelColour {
        moveUp(steps)
        return read(imgData).apply { moveDown(steps) }
    }

    override fun peek(imgData: ByteArray, x: Int, y: Int): PixelColour {
        val beginOld = begin
        set(x, y)
        return read(imgData).apply { begin = beginOld }
    }

    override fun read(imgData: ByteArray): PixelColour {
        return PixelColour.fromBytes(
            imgData[red],
            imgData[green],
            imgData[blue],
            if (hasAlpha) imgData[alpha] else -128
        )
    }

    override fun write(imgData: ByteArray, colour: PixelColour) {
        imgData[red] = colour.byteR()
        imgData[green] = colour.byteG()
        imgData[blue] = colour.byteB()
        if (hasAlpha) {
            imgData[alpha] = colour.byteA()
        }
    }

    override fun apply(imgData: ByteArray, colourAction: (PixelColour) -> PixelColour) {
        write(imgData, colourAction(read(imgData)))
    }
}