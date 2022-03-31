package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

internal class AnalyticsProxyInvocationHandler(
    private val analyticsTracker: AnalyticsTracker
) : InvocationHandler {

    private val eventFactories: MutableMap<Method, EventFactory> = mutableMapOf()

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val eventFactory = eventFactories.getOrPut(method) { newEventFactory(method) }
        val (name, params) = eventFactory.create(args)
        analyticsTracker.trackEvent(name, params)
        return Unit
    }

    private fun newEventFactory(method: Method): EventFactory {
        val annotations = method.declaredAnnotations
        checkFunAnnotations(annotations)
        val eventName = annotations.firstNotNullOf { it as? EventName }.value

        val argNames = Array(method.parameterCount) { index ->
            val paramAnnotations: Array<Annotation> = method.parameterAnnotations[index]
            checkParamAnnotations(paramAnnotations)
            paramAnnotations.firstNotNullOf { it as? Param }.value
        }
        return EventFactory(eventName, argNames)
    }

    private data class Event(val name: String, val params: Map<String, Any>?)

    private class EventFactory(
        private val eventName: String,
        private val argNames: Array<String>
    ) {

        fun create(args: Array<out Any>?): Event {
            if (args.isNullOrEmpty()) {
                check(argNames.isEmpty())
                return Event(eventName, emptyMap());
            }

            check(args.size == argNames.size)
            return Event(eventName, argNames.associateWithIndexed { i, _ -> args[i] });
        }
    }
}

private fun checkFunAnnotations(annotations: Array<Annotation>?) {
    if (annotations.isNullOrEmpty()) error("No annotations")
    if (annotations.none { it is EventName }) error("No EventName annotation")
}

private fun checkParamAnnotations(annotations: Array<Annotation>?) {
    if (annotations.isNullOrEmpty()) error("No annotations")
    if (annotations.none { it is Param }) error("No Param annotation")
}

private fun <K, V> Array<K>.associateWithIndexed(valueSelector: (Int, K) -> V): Map<K, V> {
    var i = 0
    return associateWith { key ->
        val value = valueSelector(i, key)
        i++
        value
    }
}
