package dev.androidbroadcast.analyticsproxy

interface AnalyticsTracker {

    fun trackEvent(eventName: String, params: Map<String, Any>?)
}