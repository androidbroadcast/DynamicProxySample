package dev.androidbroadcast.analyticsproxy

interface AppAnalytics {

    @EventName("app_started")
    fun trackAppStarted()

    @EventName("click")
    fun trackClick(@Param("count") count: Int)
}