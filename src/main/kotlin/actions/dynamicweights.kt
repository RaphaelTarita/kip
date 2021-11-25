package actions

import kotlin.math.E
import kotlin.math.ln
import kotlin.math.log

fun uniform(multiplier: Double = 1.0): (r: Int, max: Int) -> Double {
    return { _, _ ->
        multiplier
    }
}

fun linear(value: Double = 1.0): (r: Int, max: Int) -> Double {
    return { r, max ->
        value - (value / max) * r
    }
}

fun inv(value: Double = 1.0): (r: Int, max: Int) -> Double {
    return { r, max ->
        (value * max) / (if (r == 0) 1.0 else r.toDouble()) - value
    }
}

fun flippedLog(base: Double = E): (r: Int, max: Int) -> Double {
    return { r, max ->
        -log(if (r == 0) 1.0 else r.toDouble(), base) + log(max.toDouble(), base)
    }
}

fun flippedLogOptimized(max: Int, base: Double = E): (r: Int, max: Int) -> Double {
    val baseLog = ln(base)
    val offset = ln(max.toDouble()) / baseLog
    return { r, _ ->
        (-ln(if (r == 0) 1.0 else r.toDouble()) / baseLog) + offset
    }
}

fun flip(weight: (r: Int, max: Int) -> Double): (r: Int, max: Int) -> Double {
    return { r, max ->
        weight(max - r, max)
    }
}