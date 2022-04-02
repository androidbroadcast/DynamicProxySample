package dev.androidbroadcast.analyticsproxy

import java.lang.reflect.Proxy
import kotlin.properties.Delegates

class AnalyticsProxy private constructor(
    private val analyticsTracker: AnalyticsTracker,
    private val cached: Boolean
) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(clazz: Class<T>): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf(clazz),
            AnalyticsProxyInvocationHandler(analyticsTracker, cached)
        ) as T
    }

    class Builder {

        private var analyticsTracker: AnalyticsTracker by Delegates.notNull()
        private var cached = true

        fun analyticsTracker(analyticsTracker: AnalyticsTracker): Builder {
            this.analyticsTracker = analyticsTracker
            return this
        }

        fun cached(cached: Boolean): Builder {
            this.cached = cached
            return this
        }

        fun build(): AnalyticsProxy {
            return AnalyticsProxy(analyticsTracker, cached)
        }
    }
}

inline fun <reified T : Any> AnalyticsProxy.create(): T {
    return create(T::class.java)
}
