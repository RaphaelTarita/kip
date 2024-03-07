package com.rtarita.kip.pipeline

import com.rtarita.kip.image.KipImage

class PipelineExecutor(private val load: () -> KipImage, private val save: (KipImage) -> Unit, private val log: (String) -> Unit) {
    private fun process(img: KipImage, step: ProcessingStep): PipelineStep {
        when (step.action) {
            is MutateAction -> img.performMutateAction(step.action.perform)
            is ColorAction -> img.performColorAction(step.action.perform)
            is CoordinateAction -> img.performCoordinateAction(step.action.perform)
            is PixelAccessAction -> img.performPixelAccessAction(step.action.perform)
            is CoordinatePixelAccessAction -> img.performCoordinatePixelAccessAction(step.action.perform)
        }
        return step.next
    }

    private fun branchAndCombine(img: KipImage, step: BranchingStep): Pair<KipImage, PipelineStep>? {
        val left = img
        val right = img.branch(false)

        val leftCombinator = executeStep(left, step.left)
        val rightCombinator = executeStep(right, step.right)
        require(leftCombinator === rightCombinator) { "branches must end in the same combinator" }
        if (leftCombinator == null) {
            finalizeImage(img)
            return null
        }
        val newImg = leftCombinator.combine(left, right)
        return newImg to leftCombinator.next
    }

    private tailrec fun executeStep(img: KipImage, step: PipelineStep): CombineStep? {
        return when (step) {
            is BranchEnd -> step.combineStep
            is BranchingStep -> {
                val (newImg, next) = branchAndCombine(img, step) ?: return null
                executeStep(newImg, next)
            }

            is ProcessingStep -> executeStep(img, process(img, step))
            PipelineEnd -> null
        }
    }

    private fun prepareImage(): KipImage {
        log("PREPARE: load image")
        return load()
    }

    private fun finalizeImage(img: KipImage) {
        log("FINALIZE: save image")
        save(img)
    }

    fun execute(pipeline: KipPipeline) {
        log("starting pipeline execution")
        log("=== execution plan ===")
        log(pipeline.print())
        val start = prepareImage()
        executeStep(start, pipeline.first)
        finalizeImage(start)
    }
}