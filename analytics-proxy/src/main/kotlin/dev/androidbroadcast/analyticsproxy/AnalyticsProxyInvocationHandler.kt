package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

internal class AnalyticsProxyInvocationHandler(
    private val analyticsTracker: AnalyticsTracker
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val annotations = method.declaredAnnotations
        checkFunAnnotations(annotations)
        val eventName = annotations.find { it is EventName } as EventName?
        if (eventName != null) {
            if (args == null || args.isEmpty()) {
                analyticsTracker.trackEvent(eventName.value, params = null)
                return Unit
            }

            val eventArgs = args.mapIndexed { index, arg ->
                val paramAnnotations: Array<Annotation> = method.parameterAnnotations[index]
                checkParamAnnotations(paramAnnotations)
                (paramAnnotations.firstNotNullOf { it as? Param }.value) to arg
            }.toMap()
            analyticsTracker.trackEvent(eventName.value, params = eventArgs)
            return Unit
        }

        error("No event name. Add @EventName annotation to ${method.name}")
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