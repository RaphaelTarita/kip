# kip

"kip" stands for Kotlin Image Processing. This is a personal toy project in which I played around with interacting and modifying images in Kotlin.

The main focus lies on programmatic definition of filters (like brighten, darken, saturate, color curve modifications etc.), which are composed in a
functional fashion, heavily utilizing lambdas and higher-order functions.

kip also provides a DSL for writing reusable image processing pipelines using the aforementioned filters.

For demonstration, here's some sample code that applies a custom bloom effect to an image:

```kotlin
fun main() {
    val pipeline = buildPipeline {
        branch { // branch the image
            left { // apply steps to "left" branch
                step(retainOnly(maxOver(180))) // only keep pixels which maximum color value is over 180
                step(smooth(50, circle(), linear(5.0))) // apply a circle blur with a radius of 50 and a linear falloff
            }
            combine { left, right ->
                left.add(right, OverflowHandling.NORMALIZE) // add together the processed image with the original, normalizing on overflow
            }
        }
    }

    executePipeline(pipeline) {
        load {
            KipImage(ImageIO.read(File("path/to/myimage.png")), keepSteps = true)
        }
        save {
            it.saveAllSteps(Path("path/to/myoutput"), "step", "PNG")
        }
        log {
            println(it)
        }
    }
}
```