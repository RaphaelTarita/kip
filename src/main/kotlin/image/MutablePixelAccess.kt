package image

interface MutablePixelAccess : PixelAccess {
    fun write(x: Int, y: Int, c: PixelColour)
    fun transform(x: Int, y: Int, action: (PixelColour) -> PixelColour)

    fun writeCoerce(x: Int, y: Int, c: PixelColour)
    fun transformCoerce(x: Int, y: Int, action: (PixelColour) -> PixelColour)
}