package pipeline

import image.Image

class ImageStep(prev: Step<*>?, action: (Image) -> Unit = identity) : Step<Image>(prev, action) {
    companion object {
        private val identity: (Image) -> Unit = { }
    }

    infix fun then(action: (Image) -> Unit): ImageStep {
        return ImageStep(this, action)
    }
}