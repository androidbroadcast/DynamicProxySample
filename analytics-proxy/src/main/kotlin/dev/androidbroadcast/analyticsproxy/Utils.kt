package dev.androidbroadcast.analyticsproxy

internal fun <K, V> Array<K>.associateWithIndexed(valueSelector: (Int, K) -> V): Map<K, V> {
    var i = 0
    return associateWith { key ->
        val value = valueSelector(i, key)
        i++
        value
    }
}
