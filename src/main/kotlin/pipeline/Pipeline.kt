package pipeline

import image.Image

class Pipeline {
    fun activate(on: Image): ImageStep {
        return ImageStep(null)
    }
}