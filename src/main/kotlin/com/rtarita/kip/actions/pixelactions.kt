package com.rtarita.kip.actions

import com.rtarita.kip.image.PixelAccess
import com.rtarita.kip.image.PixelColor
import com.rtarita.kip.util.coerce
import com.rtarita.kip.util.plus
import com.rtarita.kip.util.roundToUInt
import com.rtarita.kip.util.times
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun toColorMapper(curve: (Double) -> Double): (UInt) -> UInt {
    return {
        curve(it.toDouble()).coerce(0.0, 255.0).roundToUInt()
    }
}

fun colorIdentity(): (PixelColor) -> PixelColor = { it }

fun invert(): (PixelColor) -> PixelColor = { color ->
    color.transform {
        255u - it
    }
}

fun brighten(rate: Double): (PixelColor) -> PixelColor {
    val mapper = toColorMapper { (it + ((255 - it) * rate)) }
    return { color ->
        color.transform {
            mapper(it)
        }
    }
}

private fun saturateColor(
    rate: Double,
    split: Double,
    transformChannel: PixelColor.((UInt) -> UInt) -> PixelColor
): (PixelColor) -> PixelColor {
    val absSplit = split * 255
    val mapper = toColorMapper { (it + ((if (it <= absSplit) 0.0 else 255.0) - it.toInt()) * rate) }
    return { color ->
        color.transformChannel(mapper)
    }
}

fun desaturateColor(rate: Double, transformChannel: PixelColor.((UInt) -> UInt) -> PixelColor): (PixelColor) -> PixelColor {
    return { color ->
        val gray = color.grayval()
        color.transformChannel {
            (it + (gray.toInt() - it.toInt()) * rate).toUInt()
        }
    }
}

fun saturate(rate: Double, split: Double = 0.5): (PixelColor) -> PixelColor {
    return saturateColor(rate, split, PixelColor::transform)
}

fun rSaturate(rate: Double, split: Double = 0.5): (PixelColor) -> PixelColor {
    return saturateColor(rate, split, PixelColor::rTransform)
}

fun gSaturate(rate: Double, split: Double = 0.5): (PixelColor) -> PixelColor {
    return saturateColor(rate, split, PixelColor::gTransform)
}

fun bSaturate(rate: Double, split: Double = 0.5): (PixelColor) -> PixelColor {
    return saturateColor(rate, split, PixelColor::bTransform)
}


fun desaturate(rate: Double): (PixelColor) -> PixelColor {
    return desaturateColor(rate, PixelColor::transform)
}

fun rDesaturate(rate: Double): (PixelColor) -> PixelColor {
    return desaturateColor(rate, PixelColor::rTransform)
}

fun gDesaturate(rate: Double): (PixelColor) -> PixelColor {
    return desaturateColor(rate, PixelColor::gTransform)
}

fun bDesaturate(rate: Double): (PixelColor) -> PixelColor {
    return desaturateColor(rate, PixelColor::bTransform)
}

fun curves(curve: (Double) -> Double): (PixelColor) -> PixelColor {
    val mapper = toColorMapper(curve)
    return { color ->
        color.transform(mapper)
    }
}

fun rCurve(rcurve: (Double) -> Double): (PixelColor) -> PixelColor {
    val mapper = toColorMapper(rcurve)
    return { color ->
        color.rTransform(mapper)
    }
}

fun gCurve(gcurve: (Double) -> Double): (PixelColor) -> PixelColor {
    val mapper = toColorMapper(gcurve)
    return { color ->
        color.gTransform(mapper)
    }
}

fun bCurve(bcurve: (Double) -> Double): (PixelColor) -> PixelColor {
    val mapper = toColorMapper(bcurve)
    return { color ->
        color.bTransform(mapper)
    }
}

fun grayscale(rweight: Double = 1.0, gweight: Double = 1.0, bweight: Double = 1.0): (PixelColor) -> PixelColor {
    return {
        val gray = ((it.r * rweight + it.g * gweight + it.b * bweight) / 3.0).coerce(0.0, 255.0).toUInt()
        PixelColor(
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
): PixelAccess.(x: Int, y: Int, PixelColor) -> PixelColor {
    val pixels = selectWithRadiusInformation(radius, selectorOf(mask))
    val globalWeight = 1.0 / pixels.size
    return { x, y, _ ->
        var r = 0.0
        var g = 0.0
        var b = 0.0
        var a = 0.0
        for (coord in pixels) {
            val color = readCoerce(x + coord.first, y + coord.second)
            val weight = intensity(coord.third, radius)
            r += color.r * weight * globalWeight
            g += color.g * weight * globalWeight
            b += color.b * weight * globalWeight
            a += color.a * weight * globalWeight
        }

        PixelColor(
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
    transform: (List<Triple<Int, Int, PixelColor>>, PixelColor) -> PixelColor
): PixelAccess.(PixelColor) -> PixelColor {
    val pixels = selectorOf(mask)(radius) - (0 to 0)
    return {
        val list = pixels.map { (x, y) -> Triple(x, y, readCoerce(x, y)) }
        transform(list, it)
    }
}

fun <T, R> memoized(action: (T) -> (R)): (T) -> (R) {
    val cache = hashMapOf<T, R>()
    return { color ->
        cache.computeIfAbsent(color, action)
    }
}

fun retainOnly(selector: PixelAccess.(PixelColor) -> Boolean): PixelAccess.(PixelColor) -> PixelColor {
    return {
        if (selector(it)) it else PixelColor.BLACK
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