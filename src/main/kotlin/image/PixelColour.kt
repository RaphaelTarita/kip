package image

class PixelColour(var r: Int = 0, var g: Int = 0, var b: Int = 0, var a: Int = 0) {
    companion object {
        private fun hexCol(hex: String, col: Int): Int {
            val pos = if (hex.startsWith('#')) 1 else 0 + col * 2
            return hex.substring(pos, pos + 2).toInt(16)
        }

        private fun hexHasAlpha(hex: String) = hex.length > 7

        private fun hexR(hex: String) = hexCol(hex, 0)
        private fun hexG(hex: String) = hexCol(hex, 1)
        private fun hexB(hex: String) = hexCol(hex, 2)
        private fun hexA(hex: String) = hexCol(hex, 3)

        fun fromBytes(r: Byte, g: Byte, b: Byte, a: Byte = -128): PixelColour {
            return PixelColour(
                r.toUnsignedInt(),
                g.toUnsignedInt(),
                b.toUnsignedInt(),
                a.toUnsignedInt()
            )
        }

        val BLACK = PixelColour(0, 0, 0, 255)
        val WHITE = PixelColour(255, 255, 255, 255)
    }

    constructor(original: PixelColour, onChannel: (Int) -> Int, onAlphaChannel: (Int) -> Int) : this(
        onChannel(original.r),
        onChannel(original.g),
        onChannel(original.b),
        onAlphaChannel(original.a)
    )

    constructor(original: PixelColour, onChannel: (Int) -> Int) : this(original, onChannel, onChannel)

    constructor(hex: String) : this(
        hexR(hex),
        hexG(hex),
        hexB(hex),
        if (hexHasAlpha(hex)) hexA(hex) else 0
    )

    fun transform(onChannel: (Int) -> Int): PixelColour {
        return PixelColour(this, onChannel)
    }

    fun transform(onChannel: (Int) -> Int, onAlphaChannel: (Int) -> Int): PixelColour {
        return PixelColour(this, onChannel, onAlphaChannel)
    }

    fun byteR() = r.toByte()
    fun byteG() = g.toByte()
    fun byteB() = b.toByte()
    fun byteA() = a.toByte()

    fun grayval() = (r + g + b) / 3

    fun grayscale(): PixelColour {
        val gray = grayval()
        return PixelColour(gray, gray, gray, a)
    }

    operator fun plus(other: PixelColour): PixelColour {
        return PixelColour(r + other.r, g + other.g, b + other.b, (a + other.a) / 2)
    }

    operator fun times(other: Number): PixelColour {
        val conv = other.toDouble()
        return PixelColour((r * conv).toInt(), (g * conv).toInt(), (b * conv).toInt(), a)
    }

    operator fun div(other: Number): PixelColour {
        val conv = other.toDouble()
        return PixelColour((r / conv).toInt(), (g / conv).toInt(), (b / conv).toInt(), a)
    }
}

fun String.toColor() = PixelColour(this)

fun Byte.toUnsignedInt() = this.toInt() and 0xff

