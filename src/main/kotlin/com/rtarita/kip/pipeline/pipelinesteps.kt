package com.rtarita.kip.pipeline

import com.rtarita.kip.image.KipImage

internal data class CombineStep(val combine: (KipImage, KipImage) -> KipImage, val next: PipelineStep) {
    fun print(idx: Int, branchPrefix: String) = "$branchPrefix$idx: COMBINE\n${next.print(idx + 1, branchPrefix)}"
}

internal sealed interface PipelineStep {
    fun print(idx: Int, branchPrefix: String = ""): String
}

@JvmInline
internal value class BranchEnd(val combineStep: CombineStep) : PipelineStep {
    override fun print(idx: Int, branchPrefix: String) = "$branchPrefix$idx: BRANCH-END"
}

internal data class ProcessingStep(val action: PipelineAction, val next: PipelineStep) : PipelineStep {
    override fun print(idx: Int, branchPrefix: String) = "$branchPrefix$idx: PROCESS $action\n${next.print(idx + 1, branchPrefix)}"
}

internal data class BranchingStep(val left: PipelineStep, val right: PipelineStep) : PipelineStep {
    private fun findCombinator(step: PipelineStep): CombineStep? {
        return when (step) {
            is BranchEnd -> step.combineStep
            is BranchingStep -> findCombinator(findCombinator(step)?.next ?: return null)
            PipelineEnd -> null
            is ProcessingStep -> findCombinator(step.next)
        }
    }

    override fun print(idx: Int, branchPrefix: String): String {
        val leftPrefix = "$branchPrefix[$idx-left]"
        val rightPrefix = "$branchPrefix[$idx-right]"
        val combinator = findCombinator(left)
        return "$branchPrefix$idx: BRANCH\n${left.print(0, leftPrefix)}\n${right.print(0, rightPrefix)}" +
                if (combinator != null) "\n${combinator.print(idx + 1, branchPrefix)}" else ""
    }
}

internal data object PipelineEnd : PipelineStep {
    override fun print(idx: Int, branchPrefix: String) = "$branchPrefix$idx: END"
}

@JvmInline
value class KipPipeline internal constructor(@PublishedApi internal val first: PipelineStep) {
    fun print() = first.print(1)
}