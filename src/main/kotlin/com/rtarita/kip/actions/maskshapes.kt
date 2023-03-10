package com.rtarita.kip.actions

import com.rtarita.kip.util.cbrt
import kotlin.math.abs

fun square(): (r: Int, x: Int, y: Int) -> Boolean {
    return { _, _, _ ->
        true
    }
}

fun insetHorizontal(inset: Double): (r: Int, x: Int, y: Int) -> Boolean {
    val inv = 1 - inset
    return { r, _, y ->
        abs(y) <= r * inv
    }
}

fun insetVertical(inset: Double): (r: Int, x: Int, y: Int) -> Boolean {
    val inv = 1 - inset
    return { r, x, _ ->
        abs(x) <= r * inv
    }
}

fun circle(): (r: Int, x: Int, y: Int) -> Boolean {
    return { r, x, y ->
        x * x + y * y <= r * r
    }
}

fun star(): (r: Int, x: Int, y: Int) -> Boolean {
    return { r, x, y ->
        cbrt(x * x) + cbrt(y * y) <= cbrt(r * r)
    }
}

fun cross(): (r: Int, x: Int, y: Int) -> Boolean {
    return { _, x, y ->
        x == 0 || y == 0
    }
}

fun ellipsisHorizontal(inset: Double): (r: Int, x: Int, y: Int) -> Boolean {
    val i2inv = (1 - inset) * (1 - inset)
    return { r, x, y ->
        (x * x) + ((y * y) / i2inv) <= r * r
    }
}

fun ellipsisVertical(inset: Double): (r: Int, x: Int, y: Int) -> Boolean {
    val i2inv = (1 - inset) * (1 - inset)
    return { r, x, y ->
        ((x * x) / i2inv) + (y * y) <= r * r
    }
}