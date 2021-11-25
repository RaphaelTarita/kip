package image

import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.div
import kotlin.io.path.extension

class KipImage private constructor(val keepSteps: Boolean = false, private val stack: MutableList<Step> = mutableListOf()) {
    private fun push(step: Step) {
        if (keepSteps) {
            stack.add(step)
        } else {
            stack[0] = step
        }
    }

    constructor(
        initWidth: Int,
        initHeight: Int,
        hasAlpha: Boolean = false,
        initData: ByteArray = ByteArray(initWidth * initHeight),
        keepSteps: Boolean = false
    ) : this(keepSteps) {
        stack += Step(initWidth, initHeight, hasAlpha, initData)
    }

    constructor(initImage: BufferedImage, keepSteps: Boolean = false) : this(keepSteps) {
        stack += Step(initImage)
    }

    fun step(action: (MutablePixelAccess) -> Unit) {
        val newStep = top().copy()
        action(newStep)
        push(newStep)
    }

    fun onEachPixel(action: (PixelColour) -> PixelColour) {
        val currentStep = top()
        val newStep = currentStep.next()
        repeat(currentStep.width * currentStep.height) {
            newStep.internalWrite(it * currentStep.unit, action(currentStep.internalRead(it * currentStep.unit)))
        }
        push(newStep)
    }

    fun onEachPixel(action: (x: Int, y: Int, PixelColour) -> PixelColour) {
        val currentStep = top()
        val newStep = currentStep.next()
        for (x in 0 until currentStep.width) {
            for (y in 0 until currentStep.height) {
                newStep.write(x, y, action(x, y, currentStep.read(x, y)))
            }
        }
        push(newStep)
    }

    fun onEachPixel(action: PixelAccess.(PixelColour) -> PixelColour) {
        val currentStep = top()
        val newStep = currentStep.next()
        repeat(currentStep.width * currentStep.height) {
            newStep.internalWrite(it * currentStep.unit, currentStep.action(currentStep.internalRead(it * currentStep.unit)))
        }
        push(newStep)
    }

    fun onEachPixel(action: PixelAccess.(x: Int, y: Int, PixelColour) -> PixelColour) {
        val currentStep = top()
        val newStep = currentStep.next()
        for (x in 0 until currentStep.width) {
            for (y in 0 until currentStep.height) {
                newStep.write(x, y, currentStep.action(x, y, currentStep.read(x, y)))
            }
        }
        push(newStep)
    }

    fun top(): Step = stack.lastOrNull() ?: Step.blank(0, 0)
    fun allSteps(): List<Step> = stack
    fun saveTo(path: Path, format: String = path.extension.uppercase()) {
        ImageIO.write(top().toBufferedImage(), format, path.toFile())
    }

    fun saveAllSteps(rootPath: Path, filename: String, format: String) {
        for ((idx, step) in stack.withIndex()) {
            ImageIO.write(step.toBufferedImage(), format, (rootPath / "${filename}_$idx.${format.lowercase()}").toFile())
        }
    }

    fun branch(transferSteps: Boolean = false): KipImage {
        return if (transferSteps) {
            KipImage(keepSteps, ArrayList(stack))
        } else {
            KipImage(keepSteps, mutableListOf(top()))
        }
    }
}