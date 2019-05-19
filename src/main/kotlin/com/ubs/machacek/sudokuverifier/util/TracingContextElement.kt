package com.ubs.machacek.sudokuverifier.util

import brave.Tracing
import brave.propagation.CurrentTraceContext
import kotlinx.coroutines.ThreadContextElement
import org.springframework.stereotype.Component
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Reads current trace context and transform it for nested (child) coroutine context
 */
@Component
class TracingContextElement: ThreadContextElement<CurrentTraceContext.Scope>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<TracingContextElement>

    override fun updateThreadContext(context: CoroutineContext): CurrentTraceContext.Scope {
        return Tracing.current()?.currentTraceContext()?.maybeScope(Tracing.currentTracer().nextSpan().context()) ?: CurrentTraceContext.Scope.NOOP
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: CurrentTraceContext.Scope) {
        oldState.close()
    }
}