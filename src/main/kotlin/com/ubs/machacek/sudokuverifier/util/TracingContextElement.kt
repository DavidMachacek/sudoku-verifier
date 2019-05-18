package com.ubs.machacek.sudokuverifier.util

import brave.Tracing
import brave.propagation.CurrentTraceContext
import kotlinx.coroutines.ThreadContextElement
import org.springframework.stereotype.Component
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@Component
class TracingContextElement(
) : ThreadContextElement<CurrentTraceContext.Scope>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<TracingContextElement>

    val currentTraceContext = Tracing.current()?.currentTraceContext()

    override fun updateThreadContext(context: CoroutineContext): CurrentTraceContext.Scope {
        return currentTraceContext?.maybeScope(Tracing.currentTracer().nextSpan().context()) ?: CurrentTraceContext.Scope.NOOP
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: CurrentTraceContext.Scope) {
        oldState.close()
    }
}