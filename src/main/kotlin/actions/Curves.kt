package actions

import kotlin.math.*

fun curveIdentity(): (Double) -> Double = { it }

fun sine(offset: Double = 0.5): (Double) -> Double {
    val xoff = offset * PI
    val yoff = 1.0
    val xdiv = 255.0 / PI
    val ymul = 255.0 / 2.0
    return { x ->
        (sin(x / xdiv - xoff) + yoff) * ymul
    }
}

fun logistic(steepness: Double = 0.03, offset: Double = 0.5): (Double) -> Double {
    val offsetPremultiplied = offset * 255.0
    return { x ->
        (1.0 / (1.0 + exp(-(x - offsetPremultiplied) * steepness))) * 255
    }
}

fun tangenth(steepness: Double = 0.015, offset: Double = 0.5): (Double) -> Double {
    val offsetPremultiplied = offset * 255.0
    return { x ->
        (tanh((x - offsetPremultiplied) * steepness) + 1) * 127.5
    }
}

fun arcustan(steepness: Double = 0.025, offset: Double = 0.5): (Double) -> Double {
    val offsetPremultiplied = offset * 255.0
    return { x ->
        (atan((x - offsetPremultiplied) * steepness) / PI + 0.5) * 255.0
    }
}

fun gd(steepness: Double = 0.025, offset: Double = 0.5): (Double) -> Double {
    val offsetPremultiplied = offset * 255.0
    val steepnessPredivided = steepness / 2.0
    val coefficient = 2.0 / PI
    return { x ->
        (coefficient * atan(tanh((x - offsetPremultiplied) * steepnessPredivided)) + 0.5) * 255.0
    }
}

fun smoothstep(steepness: Int = 1, offset: Double = 0.0): (Double) -> Double {
    val coefficients: IntArray = if (steepness < 7) smoothstepCoefficients[steepness] else coefficientsFor(steepness)

    return { x ->
        var res = 0.0
        for ((k, c) in coefficients.withIndex()) {
            res += c * (x / 255.0).pow(steepness + k + 1)
        }
        res * 255.0
    }
}

fun coefficientsFor(n: Int): IntArray {
    val res = IntArray(n + 1)
    for (k in res.indices) {
        res[k] = altsgn(k) * ((n + k) choose k) * ((2 * n + 1) choose (n - k))
    }
    return res
}

private fun altsgn(i: Int): Int {
    return if (i % 2 == 0) 1 else -1
}

private infix fun Int.choose(k: Int): Int {
    return when {
        k == 0 -> 1
        k > this / 2 -> this choose (this - k)
        else -> this * ((this - 1) choose (k - 1)) / k
    }
}

private val smoothstepCoefficients: Array<IntArray> = arrayOf(
    intArrayOf(1),                                            // steepness = 0
    intArrayOf(3, -2),                                        // steepness = 1
    intArrayOf(10, -15, 6),                                   // steepness = 2
    intArrayOf(35, -84, 70, -20),                             // steepness = 3
    intArrayOf(126, -420, 540, -315, 70),                     // steepness = 4
    intArrayOf(462, -1980, 3465, -3080, 1386, -252),          // steepness = 5
    intArrayOf(1716, -9009, 20020, -24024, 16380, -6006, 924) // steepness = 6
)