package com.rtarita.kip.actions

import com.rtarita.kip.image.PixelAccess
import com.rtarita.kip.image.PixelColour
import com.rtarita.kip.util.coerce
import com.rtarita.kip.util.plus
import com.rtarita.kip.util.roundToUInt
import com.rtarita.kip.util.times
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun toColourMapper(curve: (Double) -> Double): (UInt) -> UInt {
    return {
        curve(it.toDouble()).coerce(0.0, 255.0).roundToUInt()
    }
}

fun colourIdentity(): (PixelColour) -> PixelColour = { it }

fun invert(): (PixelColour) -> PixelColour = { colour ->
    colour.transform {
        255u - it
    }
}

fun brighten(rate: Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper { (it + ((255 - it) * rate)) }
    return { colour ->
        colour.transform {
            mapper(it)
        }
    }
}

private fun saturateColour(
    rate: Double,
    split: Double,
    transformChannel: PixelColour.((UInt) -> UInt) -> PixelColour
): (PixelColour) -> PixelColour {
    val absSplit = split * 255
    val mapper = toColourMapper { (it + ((if (it <= absSplit) 0.0 else 255.0) - it.toInt()) * rate) }
    return { colour ->
        colour.transformChannel(mapper)
    }
}

fun desaturateColour(rate: Double, transformChannel: PixelColour.((UInt) -> UInt) -> PixelColour): (PixelColour) -> PixelColour {
    return { colour ->
        val gray = colour.grayval()
        colour.transformChannel {
            (it + (gray.toInt() - it.toInt()) * rate).toUInt()
        }
    }
}

fun saturate(rate: Double, split: Double = 0.5): (PixelColour) -> PixelColour {
    return saturateColour(rate, split, PixelColour::transform)
}

fun rSaturate(rate: Double, split: Double = 0.5): (PixelColour) -> PixelColour {
    return saturateColour(rate, split, PixelColour::rTransform)
}

fun gSaturate(rate: Double, split: Double = 0.5): (PixelColour) -> PixelColour {
    return saturateColour(rate, split, PixelColour::gTransform)
}

fun bSaturate(rate: Double, split: Double = 0.5): (PixelColour) -> PixelColour {
    return saturateColour(rate, split, PixelColour::bTransform)
}


fun desaturate(rate: Double): (PixelColour) -> PixelColour {
    return desaturateColour(rate, PixelColour::transform)
}

fun rDesaturate(rate: Double): (PixelColour) -> PixelColour {
    return desaturateColour(rate, PixelColour::rTransform)
}

fun gDesaturate(rate: Double): (PixelColour) -> PixelColour {
    return desaturateColour(rate, PixelColour::gTransform)
}

fun bDesaturate(rate: Double): (PixelColour) -> PixelColour {
    return desaturateColour(rate, PixelColour::bTransform)
}

fun curves(curve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(curve)
    return { colour ->
        colour.transform(mapper)
    }
}

fun rCurve(rcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(rcurve)
    return { colour ->
        colour.rTransform(mapper)
    }
}

fun gCurve(gcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(gcurve)
    return { colour ->
        colour.gTransform(mapper)
    }
}

fun bCurve(bcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(bcurve)
    return { colour ->
        colour.bTransform(mapper)
    }
}

fun grayscale(rweight: Double = 1.0, gweight: Double = 1.0, bweight: Double = 1.0): (PixelColour) -> PixelColour {
    return {
        val gray = ((it.r * rweight + it.g * gweight + it.b * bweight) / 3.0).coerce(0.0, 255.0).toUInt()
        PixelColour(
            gray,
            gray,
            gray,
            it.a
        )
    }
}

fun smooth(
    radius: Int = 1,
    mask: (r: Int, x: Int, y: Int) -> Boolean = circle(),
    intensity: (r: Int, max: Int) -> Double = uniform(),
): PixelAccess.(x: Int, y: Int, PixelColour) -> PixelColour {
    val pixels = selectWithRadiusInformation(radius, selectorOf(mask))
    val globalWeight = 1.0 / pixels.size
    return { x, y, _ ->
        var r = 0.0
        var g = 0.0
        var b = 0.0
        var a = 0.0
        for (coord in pixels) {
            val colour = readCoerce(x + coord.first, y + coord.second)
            val weight = intensity(coord.third, radius)
            r += colour.r * weight * globalWeight
            g += colour.g * weight * globalWeight
            b += colour.b * weight * globalWeight
            a += colour.a * weight * globalWeight
        }

        PixelColour(
            r.roundToUInt().coercePixel(),
            g.roundToUInt().coercePixel(),
            b.roundToUInt().coercePixel(),
            a.roundToUInt().coercePixel()
        )
    }
}

fun pixelContextFilter(
    radius: Int = 1,
    mask: (r: Int, x: Int, y: Int) -> Boolean = circle(),
    transform: (List<Triple<Int, Int, PixelColour>>, PixelColour) -> PixelColour
): PixelAccess.(PixelColour) -> PixelColour {
    val pixels = selectorOf(mask)(radius) - (0 to 0)
    return {
        val list = pixels.map { (x, y) -> Triple(x, y, readCoerce(x, y)) }
        transform(list, it)
    }
}

fun <T, R> memoized(action: (T) -> (R)): (T) -> (R) {
    val cache = hashMapOf<T, R>()
    return { colour ->
        cache.computeIfAbsent(colour, action)
    }
}

fun retainOnly(selector: PixelAccess.(PixelColour) -> Boolean): PixelAccess.(PixelColour) -> PixelColour {
    return {
        if (selector(it)) it else PixelColour.BLACK
    }
}

private fun UInt.coercePixel() = coerce(0u, 255u)

private fun selectorOf(twoDimensionalSelector: (r: Int, x: Int, y: Int) -> Boolean): (r: Int) -> List<Pair<Int, Int>> {
    return { r ->
        val res = mutableListOf<Pair<Int, Int>>()
        for (y in -r..r) {
            for (x in -r..r) {
                if (twoDimensionalSelector(r, x, y)) {
                    res.add(x to y)
                }
            }
        }
        res
    }
}

private fun selectWithRadiusInformation(radius: Int, mask: (r: Int) -> List<Pair<Int, Int>>): List<Triple<Int, Int, Int>> {
    val selection = mask(radius)
    val res = mutableListOf<Triple<Int, Int, Int>>()
    for (coord in selection) {
        val x2 = coord.first.toDouble() * coord.first
        val y2 = coord.second.toDouble() * coord.second
        res.add(Triple(coord.first, coord.second, sqrt(x2 + y2).roundToInt()))
    }
    return res
}