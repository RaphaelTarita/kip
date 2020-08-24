package image

class BoundPixelView(private val pixelView: PixelView, private val data: ByteArray) : _PixelViewActions by pixelView {
    fun peekRight(steps: Int = 1) = pixelView.peekRight(data, steps)
    fun peekDown(steps: Int = 1) = pixelView.peekDown(data, steps)
    fun peekLeft(steps: Int = 1) = pixelView.peekLeft(data, steps)
    fun peekUp(steps: Int = 1) = pixelView.peekUp(data, steps)
    fun peek(x: Int, y: Int) = pixelView.peek(data, x, y)
    fun read() = pixelView.read(data)
    fun write(colour: PixelColour) = pixelView.write(data, colour)
    fun apply(colourAction: (PixelColour) -> PixelColour) = pixelView.apply(data, colourAction)
}