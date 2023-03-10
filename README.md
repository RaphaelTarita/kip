# kip

"kip" stands for Kotlin Image Processing. This is a personal toy project in which I played around with interacting and modifying images in Kotlin.

The main focus lies on programmatic definition of filters (like brighten, darken, saturate, color curve modifications etc.), which are composed in a
functional fashion, heavily utilizing lambdas and higher-order functions.

For demonstration, here's some sample code that applies a custom bloom effect to an image:

```kotlin
fun main() {
    val source = ImageIO.read(File("path/to/myimage.png"))
    val img = KipImage(source, keepSteps = true)

    val orig = img.branch(transferSteps = false) // create a copy of the original
    img.onEachPixel(retainOnly(maxOver(180))) // only keep pixels which maximum color value is over 180
    img.onEachPixel(smooth(50, circle(), linear(5.0))) // apply a circle blur with a radius of 50 and a linear falloff
    img.plus(orig, OverflowHandling.NORMALIZE) // add together the processed image with the original, normalizing on overflow

    img.saveAllSteps(Path("path/to/myoutput/"), "step", "PNG")
}
```