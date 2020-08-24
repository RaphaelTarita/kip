package pipeline

abstract class Step<T>(protected val prev: Step<*>?, protected val action: (T) -> Unit) {
    protected fun apply(on: T) {
        action(on)
    }
}