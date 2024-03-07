package com.rtarita.kip.image

interface PixelAccess {
    fun read(x: Int, y: Int): PixelColor

    fun coerceX(x: Int): Int
    fun coerceY(y: Int): Int
    fun coerce(x: Int, y: Int): Pair<Int, Int> = coerceX(x) to coerceY(y)

    fun readCoerce(x: Int, y: Int): PixelColor
}