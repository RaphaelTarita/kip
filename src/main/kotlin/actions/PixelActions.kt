package actions

import image.BoundPixelView
import image.PixelColour

fun colourIdentity(): (PixelColour) -> PixelColour = { it }

fun saturate(rate: Double): (PixelColour) -> PixelColour {
    return { colour ->
        colour.transform {
            (it + ((if (it <= 127) 0 else 255) - it) * rate).toInt()
        }
    }
}

fun desaturate(rate: Double): (PixelColour) -> PixelColour {
    return { colour ->
        val gray = colour.grayval()
        colour.transform {
            (it + (gray - it) * rate).toInt()
        }
    }
}

fun curves(curve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(curve)
    return { colour ->
        colour.transform(mapper)
    }
}

fun rCurve(rcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(rcurve)
    return {
        PixelColour(
            mapper(it.r),
            it.g,
            it.b,
            it.a
        )
    }
}

fun gCurve(gcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(gcurve)
    return {
        PixelColour(
            it.r,
            mapper(it.g),
            it.b,
            it.a
        )
    }
}

fun bCurve(bcurve: (Double) -> Double): (PixelColour) -> PixelColour {
    val mapper = toColourMapper(bcurve)
    return {
        PixelColour(
            it.r,
            it.g,
            mapper(it.b),
            it.a
        )
    }
}

fun grayscale(rweight: Double = 1.0, gweight: Double = 1.0, bweight: Double = 1.0): (PixelColour) -> PixelColour {
    return {
        val gray = ((it.r * rweight + it.g * gweight + it.b * bweight) / 3.0).coerceIn(0.0, 255.0).toInt()
        PixelColour(
            gray,
            gray,
            gray,
            it.a
        )
    }
}

fun smooth(ratio: Double = 0.2): (PixelColour, BoundPixelView) -> PixelColour {
    val inverse = (1 - ratio) / 4.0
    return { colour, view ->
        var res = PixelColour.BLACK
        if (view.x != 0) res += view.peekLeft() * inverse
        if (view.y != 0) res += view.peekUp() * inverse
        if (view.x != view.width - 1) res += view.peekRight() * inverse
        if (view.y != view.height - 1) res += view.peekDown() * inverse
        res += colour * ratio
        res
    }
}

private fun toColourMapper(curve: (Double) -> Double): (Int) -> Int {
    return {
        curve(it.toDouble()).toInt().coerceIn(0, 255)
    }
}