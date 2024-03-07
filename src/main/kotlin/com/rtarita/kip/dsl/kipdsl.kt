package com.rtarita.kip.dsl

import com.rtarita.kip.image.KipImage
import com.rtarita.kip.image.MutablePixelAccess
import com.rtarita.kip.image.PixelAccess
import com.rtarita.kip.image.PixelColor
import com.rtarita.kip.pipeline.*

internal sealed interface IntermediateToken

@JvmInline
internal value class ProcessingToken(val action: PipelineAction) : IntermediateToken
internal data class BranchingToken(
    val left: List<IntermediateToken>,
    val right: List<IntermediateToken>,
    val combinator: (KipImage, KipImage) -> KipImage
) : IntermediateToken

class KipDslBranchingScope {
    @PublishedApi
    internal val leftScope = KipDslPipelineScope()

    @PublishedApi
    internal val rightScope = KipDslPipelineScope()

    @PublishedApi
    internal var combinator: (KipImage, KipImage) -> KipImage = { left, _ -> left }

    inline fun left(action: KipDslPipelineScope.() -> Unit) {
        leftScope.action()
    }

    inline fun right(action: KipDslPipelineScope.() -> Unit) {
        rightScope.action()
    }

    fun combine(combinator: (KipImage, KipImage) -> KipImage) {
        this.combinator = combinator
    }

    @PublishedApi
    internal fun build() = BranchingToken(leftScope.tokenList, rightScope.tokenList, combinator)
}

@JvmInline
value class KipDslPipelineScope @PublishedApi internal constructor(@PublishedApi internal val tokenList: MutableList<IntermediateToken> = mutableListOf()) {
    fun paint(perform: (MutablePixelAccess) -> Unit) {
        tokenList += ProcessingToken(MutateAction(perform))
    }

    fun step(perform: (PixelColor) -> PixelColor) {
        tokenList += ProcessingToken(ColorAction(perform))
    }

    fun step(perform: (x: Int, y: Int, PixelColor) -> PixelColor) {
        tokenList += ProcessingToken(CoordinateAction(perform))
    }

    fun step(perform: PixelAccess.(PixelColor) -> PixelColor) {
        tokenList += ProcessingToken(PixelAccessAction(perform))
    }

    fun step(perform: PixelAccess.(x: Int, y: Int, PixelColor) -> PixelColor) {
        tokenList += ProcessingToken(CoordinatePixelAccessAction(perform))
    }

    inline fun branch(action: KipDslBranchingScope.() -> Unit) {
        val scope = KipDslBranchingScope()
        scope.action()
        tokenList += scope.build()
    }

    private fun collapse(list: List<IntermediateToken>, cap: PipelineStep): PipelineStep = list.foldRight(cap) { token, step ->
        when (token) {
            is ProcessingToken -> ProcessingStep(token.action, step)
            is BranchingToken -> {
                val combinator = CombineStep(token.combinator, step)
                val left = collapse(token.left, BranchEnd(combinator))
                val right = collapse(token.right, BranchEnd(combinator))
                BranchingStep(left, right)
            }
        }
    }

    @PublishedApi
    internal fun build() = KipPipeline(collapse(tokenList, PipelineEnd))
}

class KipDslExecutorScope {
    private var loadFunction: () -> KipImage = { KipImage.blank(1000, 1000, hasAlpha = true, keepSteps = true) }
    private var saveFunction: (KipImage) -> Unit = { /* noop */ }
    private var logFunction: (String) -> Unit = { /* noop */ }

    fun load(loadFunction: () -> KipImage) {
        this.loadFunction = loadFunction
    }

    fun save(saveFunction: (KipImage) -> Unit) {
        this.saveFunction = saveFunction
    }

    fun log(logFunction: (String) -> Unit) {
        this.logFunction = logFunction
    }

    @PublishedApi
    internal fun build() = PipelineExecutor(loadFunction, saveFunction, logFunction)
}

inline fun buildPipeline(action: KipDslPipelineScope.() -> Unit): KipPipeline {
    val scope = KipDslPipelineScope()
    scope.action()
    return scope.build()
}

inline fun executePipeline(pipeline: KipPipeline, action: KipDslExecutorScope.() -> Unit) {
    val scope = KipDslExecutorScope()
    scope.action()
    scope.build().execute(pipeline)
}