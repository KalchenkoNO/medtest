package com.ce2af4a3.tests.utils

fun <T> Sequence<T>.chunked(predicate: (T, T) -> Boolean): Sequence<List<T>> {
    val underlyingSequence = this
    if (!underlyingSequence.iterator().hasNext())
        return emptySequence()
    return sequence {
        val buffer = mutableListOf<T>()
        var last: T? = null
        underlyingSequence.forEach { current ->
            val shouldSplit = last?.let { predicate(it, current) } ?: false
            last = current
            if (shouldSplit) {
                yield(buffer.toList())
                buffer.clear()
            }
            buffer.add(current)
        }
        yield(buffer)
    }
}