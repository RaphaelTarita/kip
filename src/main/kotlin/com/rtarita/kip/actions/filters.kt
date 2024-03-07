package com.rtarita.kip.actions

import com.rtarita.kip.image.PixelAccess
import com.rtarita.kip.image.PixelColor
import com.rtarita.kip.util.max
import com.rtarita.kip.util.min

inline fun invert(crossinline orig: PixelAccess.(PixelColor) -> Boolean): PixelAccess.(PixelColor) -> Boolean {
    return {
        !orig(it)
    }
}

fun grayscaleOver(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        ((it.r + it.g + it.b).toDouble() / 3.0) > threshold
    }
}

fun grayscaleUnder(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        ((it.r + it.g + it.b).toDouble() / 3.0) < threshold
    }
}

fun maxOver(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        max(it.r, it.g, it.b).toInt() > threshold
    }
}

fun maxUnder(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        max(it.r, it.g, it.b).toInt() < threshold
    }
}

fun minOver(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        min(it.r, it.g, it.b).toInt() > threshold
    }
}

fun minUnder(threshold: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        min(it.r, it.g, it.b).toInt() < threshold
    }
}

fun colorsOver(thresholdR: Int, thresholdG: Int, thresholdB: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        it.r.toInt() > thresholdR && it.g.toInt() > thresholdG && it.b.toInt() > thresholdB
    }
}

fun colorsUnder(thresholdR: Int, thresholdG: Int, thresholdB: Int): PixelAccess.(PixelColor) -> Boolean {
    return {
        it.r.toInt() < thresholdR && it.g.toInt() < thresholdG && it.b.toInt() < thresholdB
    }
}