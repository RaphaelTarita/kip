package com.rtarita.kip.image

interface MutablePixelAccess : PixelAccess {
    fun write(x: Int, y: Int, c: PixelColor)
    fun transform(x: Int, y: Int, action: (PixelColor) -> PixelColor)

    fun writeCoerce(x: Int, y: Int, c: PixelColor)
    fun transformCoerce(x: Int, y: Int, action: (PixelColor) -> PixelColor)
}