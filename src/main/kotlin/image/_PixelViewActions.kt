package image

interface _PixelViewActions {
    val width: Int
    val height: Int
    val x: Int
    val y: Int

    fun moveRight(steps: Int = 1)
    fun moveDown(steps: Int = 1)
    fun moveLeft(steps: Int = 1)
    fun moveUp(steps: Int = 1)
    fun set(x: Int, y: Int)
    fun peekRight(imgData: ByteArray, steps: Int = 1): PixelColour
    fun peekDown(imgData: ByteArray, steps: Int = 1): PixelColour
    fun peekLeft(imgData: ByteArray, steps: Int = 1): PixelColour
    fun peekUp(imgData: ByteArray, steps: Int = 1): PixelColour
    fun peek(imgData: ByteArray, x: Int, y: Int): PixelColour
    fun read(imgData: ByteArray): PixelColour
    fun write(imgData: ByteArray, colour: PixelColour)
    fun apply(imgData: ByteArray, colourAction: (PixelColour) -> PixelColour)
}