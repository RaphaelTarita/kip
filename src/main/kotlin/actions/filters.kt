package actions

import image.PixelAccess
import image.PixelColour
import util.max
import util.min

inline fun invert(crossinline orig: PixelAccess.(PixelColour) -> Boolean): PixelAccess.(PixelColour) -> Boolean {
    return {
        !orig(it)
    }
}

fun grayscaleOver(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        ((it.r + it.g + it.b).toDouble() / 3.0) > threshold
    }
}

fun grayscaleUnder(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        ((it.r + it.g + it.b).toDouble() / 3.0) < threshold
    }
}

fun maxOver(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        max(it.r, it.g, it.b).toInt() > threshold
    }
}

fun maxUnder(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        max(it.r, it.g, it.b).toInt() < threshold
    }
}

fun minOver(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        min(it.r, it.g, it.b).toInt() > threshold
    }
}

fun minUnder(threshold: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        min(it.r, it.g, it.b).toInt() < threshold
    }
}

fun coloursOver(thresholdR: Int, thresholdG: Int, thresholdB: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        it.r.toInt() > thresholdR && it.g.toInt() > thresholdG && it.b.toInt() > thresholdB
    }
}

fun coloursUnder(thresholdR: Int, thresholdG: Int, thresholdB: Int): PixelAccess.(PixelColour) -> Boolean {
    return {
        it.r.toInt() < thresholdR && it.g.toInt() < thresholdG && it.b.toInt() < thresholdB
    }
}