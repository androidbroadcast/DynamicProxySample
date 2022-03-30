package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.Proxy
import kotlin.properties.Delegates

class AnalyticsProxy(private val analyticsTracker: AnalyticsTracker) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            AnalyticsProxyInvocationHandler(analyticsTracker)
        ) as T
    }

    class Builder() {

        private var analyticsTracker: AnalyticsTracker by Delegates.notNull()

        fun analyticsTracker(analyticsTracker: AnalyticsTracker): Builder {
            this.analyticsTracker = analyticsTracker
            return this
        }

        fun build(): AnalyticsProxy {
            return AnalyticsProxy(analyticsTracker)
        }
    }
}

inline fun <reified T : Any> AnalyticsProxy.create(): T {
    return create(T::class.java)
}
