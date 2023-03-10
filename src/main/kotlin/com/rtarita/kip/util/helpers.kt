package com.rtarita.kip.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

fun max(r: UInt, g: UInt, b: UInt, a: UInt = 0u): UInt {
    return max(max(max(r, g), b), a)
}

fun min(r: UInt, g: UInt, b: UInt): UInt {
    return min(min(r, g), b)
}

fun min(r: UInt, g: UInt, b: UInt, a: UInt): UInt {
    return min(min(min(r, g), b), a)
}

fun Int.coerce(lower: Int, upper: Int): Int {
    return min(max(this, lower), upper)
}

fun Int.pow(other: Double): Double = toDouble().pow(other)
fun Int.pow(other: Int): Double = toDouble().pow(other)
fun cbrt(a: Double): Double = Math.cbrt(a)
fun cbrt(a: Int): Double = Math.cbrt(a.toDouble())

fun UInt.coerce(lower: UInt, upper: UInt): UInt {
    return min(max(this, lower), upper)
}

fun Double.coerce(lower: Double, upper: Double): Double {
    return min(max(this, lower), upper)
}

operator fun UInt.compareTo(other: Double): Int = toDouble().compareTo(other)
operator fun UInt.plus(other: Double): Double = toDouble() + other
operator fun UInt.minus(other: Double): Double = toDouble() + other
operator fun UInt.times(other: Double): Double = toDouble() * other
operator fun UInt.div(other: Double): Double = toDouble() / other

fun Double.roundToUInt(): UInt = roundToInt().toUInt()

fun Byte.channelToUInt() = toUByte().toUInt()