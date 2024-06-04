# kip

"kip" stands for Kotlin Image Processing. This is a personal toy project in which I played around with interacting and modifying images in Kotlin.

The main focus lies on programmatic definition of filters (like brighten, darken, saturate, color curve modifications etc.), which are composed in a
functional fashion, heavily utilizing lambdas and higher-order functions.

kip also provides a DSL for writing reusable image processing pipelines using the aforementioned filters.

For demonstration, here's some sample code that applies a custom bloom effect to an image:

```kotlin
fun main() {
    val pipeline = buildPipeline {
        branch {
            left {
                branch {
                    left {
                        step(smooth(100, cross(), uniform(3.0)))
                    }
                    right {
                        step(smooth(60, star(), uniform(3.0)))
                    }
                    combine { left, right -> left.add(right, OverflowHandling.CLAMP) }
                }
            }
            combine { left, right -> left.add(right, OverflowHandling.CLAMP) }
        }
    }

    executePipeline(pipeline) {
        load {
            KipImage(ImageIO.read(File("test-images/input/test.png")), keepSteps = true)
        }
        save {
            it.saveAllSteps(Path("test-images/output/"), "step", "PNG")
        }
        log {
            println(it)
        }
    }
}
```