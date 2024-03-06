package com.rtarita.kip.image

import com.rtarita.kip.util.channelToUInt
import com.rtarita.kip.util.div
import com.rtarita.kip.util.times

class PixelColour(var r: UInt = 0u, var g: UInt = 0u, var b: UInt = 0u, var a: UInt = 0u) {
    companion object {
        private val identity: (UInt) -> UInt = { it }
        private fun hexCol(hex: String, col: Int): UInt {
            val pos = if (hex.startsWith('#')) 1 else 0 + col * 2
            return hex.substring(pos, pos + 2).toUInt(16)
        }

        private fun hexHasAlpha(hex: String) = hex.length > 7

        private fun hexR(hex: String) = hexCol(hex, 0)
        private fun hexG(hex: String) = hexCol(hex, 1)
        private fun hexB(hex: String) = hexCol(hex, 2)
        private fun hexA(hex: String) = hexCol(hex, 3)

        fun fromBytes(r: Byte, g: Byte, b: Byte, a: Byte = -128): PixelColour {
            return PixelColour(
                r.channelToUInt(),
                g.channelToUInt(),
                b.channelToUInt(),
                a.channelToUInt()
            )
        }

        val BLACK = PixelColour(0u, 0u, 0u, 255u)
        val WHITE = PixelColour(255u, 255u, 255u, 255u)
    }

    constructor(
        original: PixelColour,
        onR: (UInt) -> UInt = identity,
        onG: (UInt) -> UInt = identity,
        onB: (UInt) -> UInt = identity,
        onAlpha: (UInt) -> UInt = identity
    ) : this(
        onR(original.r),
        onG(original.g),
        onB(original.b),
        onAlpha(original.a)
    )

    constructor(original: PixelColour, onChannel: (UInt) -> UInt, onAlphaChannel: (UInt) -> UInt) : this(
        original,
        onChannel,
        onChannel,
        onChannel,
        onAlphaChannel
    )

    constructor(original: PixelColour, onChannel: (UInt) -> UInt) : this(original, onChannel, onChannel)

    constructor(hex: String) : this(
        hexR(hex),
        hexG(hex),
        hexB(hex),
        if (hexHasAlpha(hex)) hexA(hex) else 0u
    )

    fun transform(onChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onChannel)
    }

    fun transform(onChannel: (UInt) -> UInt, onAlphaChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onChannel, onAlphaChannel)
    }

    fun rTransform(onRedChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onR = onRedChannel)
    }

    fun gTransform(onGreenChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onG = onGreenChannel)
    }

    fun bTransform(onBlueChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onB = onBlueChannel)
    }

    fun alphaTransform(onAlphaChannel: (UInt) -> UInt): PixelColour {
        return PixelColour(this, onAlpha = onAlphaChannel)
    }

    fun byteR() = r.toByte()
    fun byteG() = g.toByte()
    fun byteB() = b.toByte()
    fun byteA() = a.toByte()

    fun grayval() = (r + g + b) / 3u

    fun grayscale(): PixelColour {
        val gray = grayval()
        return PixelColour(gray, gray, gray, a)
    }

    operator fun plus(other: PixelColour): PixelColour {
        return PixelColour(r + other.r, g + other.g, b + other.b, (a + other.a) / 2u)
    }

    operator fun times(other: Number): PixelColour {
        val conv = other.toDouble()
        return PixelColour(
            (r * conv).toUInt(),
            (g * conv).toUInt(),
            (b * conv).toUInt(),
            a
        )
    }

    operator fun div(other: Number): PixelColour {
        val conv = other.toDouble()
        return PixelColour(
            (r / conv).toUInt(),
            (g / conv).toUInt(),
            (b / conv).toUInt(),
            a
        )
    }

    operator fun component1(): UInt = r
    operator fun component2(): UInt = g
    operator fun component3(): UInt = b
    operator fun component4(): UInt = a

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is PixelColour) return false
        return other.r == r &&
                other.g == g &&
                other.b == b &&
                other.a == a
    }

    override fun hashCode(): Int {
        return (r + g + b + a).toInt()
    }
}

fun String.toColour() = PixelColour(this)

