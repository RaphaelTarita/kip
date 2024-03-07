package com.rtarita.kip.pipeline

import com.rtarita.kip.image.MutablePixelAccess
import com.rtarita.kip.image.PixelAccess
import com.rtarita.kip.image.PixelColor

internal sealed interface PipelineAction {
    override fun toString(): String
}

@JvmInline
internal value class MutateAction(val perform: (MutablePixelAccess) -> Unit) : PipelineAction {
    override fun toString(): String = "[mutate]"
}

@JvmInline
internal value class ColorAction(val perform: (PixelColor) -> PixelColor) : PipelineAction {
    override fun toString(): String = "[color]"
}

@JvmInline
internal value class CoordinateAction(val perform: (x: Int, y: Int, PixelColor) -> PixelColor) : PipelineAction {
    override fun toString(): String = "[color+location]"
}

@JvmInline
internal value class PixelAccessAction(val perform: PixelAccess.(PixelColor) -> PixelColor) : PipelineAction {
    override fun toString(): String = "[color+surround]"
}

@JvmInline
internal value class CoordinatePixelAccessAction(val perform: PixelAccess.(x: Int, y: Int, PixelColor) -> PixelColor) : PipelineAction {
    override fun toString(): String = "[color+location+surround]"
}