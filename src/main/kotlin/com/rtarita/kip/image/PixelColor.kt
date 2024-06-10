package com.rtarita.kip.image

import com.rtarita.kip.util.channelToUInt
import com.rtarita.kip.util.div
import com.rtarita.kip.util.times

class PixelColor(val r: UInt = 0u, val g: UInt = 0u, val b: UInt = 0u, val a: UInt = 0u) {
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

        fun fromBytes(r: Byte, g: Byte, b: Byte, a: Byte = -128): PixelColor {
            return PixelColor(
                r.channelToUInt(),
                g.channelToUInt(),
                b.channelToUInt(),
                a.channelToUInt()
            )
        }

        val BLACK = PixelColor(0u, 0u, 0u, 255u)
        val WHITE = PixelColor(255u, 255u, 255u, 255u)
    }

    constructor(
        original: PixelColor,
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

    constructor(original: PixelColor, onChannel: (UInt) -> UInt, onAlphaChannel: (UInt) -> UInt) : this(
        original,
        onChannel,
        onChannel,
        onChannel,
        onAlphaChannel
    )

    constructor(original: PixelColor, onChannel: (UInt) -> UInt) : this(original, onChannel, onChannel)

    constructor(hex: String) : this(
        hexR(hex),
        hexG(hex),
        hexB(hex),
        if (hexHasAlpha(hex)) hexA(hex) else 0u
    )

    fun transform(onChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onChannel)
    }

    fun transform(onChannel: (UInt) -> UInt, onAlphaChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onChannel, onAlphaChannel)
    }

    fun rTransform(onRedChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onR = onRedChannel)
    }

    fun gTransform(onGreenChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onG = onGreenChannel)
    }

    fun bTransform(onBlueChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onB = onBlueChannel)
    }

    fun alphaTransform(onAlphaChannel: (UInt) -> UInt): PixelColor {
        return PixelColor(this, onAlpha = onAlphaChannel)
    }

    fun byteR() = r.toByte()
    fun byteG() = g.toByte()
    fun byteB() = b.toByte()
    fun byteA() = a.toByte()

    fun grayval() = (r + g + b) / 3u

    fun grayscale(): PixelColor {
        val gray = grayval()
        return PixelColor(gray, gray, gray, a)
    }

    operator fun plus(other: PixelColor): PixelColor {
        return PixelColor(r + other.r, g + other.g, b + other.b, (a + other.a) / 2u)
    }

    operator fun times(other: Number): PixelColor {
        val conv = other.toDouble()
        return PixelColor(
            (r * conv).toUInt(),
            (g * conv).toUInt(),
            (b * conv).toUInt(),
            a
        )
    }

    operator fun div(other: Number): PixelColor {
        val conv = other.toDouble()
        return PixelColor(
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
        if (other !is PixelColor) return false
        return other.r == r &&
                other.g == g &&
                other.b == b &&
                other.a == a
    }

    override fun hashCode(): Int {
        return (r + g + b + a).toInt()
    }
}

fun String.toColor() = PixelColor(this)

