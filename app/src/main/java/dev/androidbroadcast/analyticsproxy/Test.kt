package dev.androidbroadcast.analyticsproxy

fun main() {
    val analyticsTracker = object : AnalyticsTracker {

        override fun trackEvent(eventName: String, params: Map<String, Any>?) {
            if (params.isNullOrEmpty()) {
                println(eventName)
            } else {
                println("$eventName(${params})")
            }
        }
    }

    val analyticsProxy = AnalyticsProxy.Builder()
        .analyticsTracker(analyticsTracker)
        .build()

    val appAnalytics: AppAnalytics = analyticsProxy.create()
    appAnalytics.trackAppStarted()
    appAnalytics.trackClick(count = 2)
}