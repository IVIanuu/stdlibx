/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.stdlibx/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

inline fun <T> Iterable<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in this) {
        list.add(item)
        if (!predicate(item))
            break
    }

    return list
}

inline fun <T> Array<out T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in this) {
        list.add(item)
        if (!predicate(item))
            break
    }
    return list
}

fun <T> Sequence<T>.takeUntil(predicate: (T) -> Boolean): Sequence<T> {
    return TakeUntilSequence(this, predicate)
}

private class TakeUntilSequence<T>(
    private val sequence: Sequence<T>,
    private val predicate: (T) -> Boolean
) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator = sequence.iterator()

        private var nextItem: T? = null
        var nextState: Int = -1 // -1 for unknown, 0 for done, 1 for continue

        private fun calcNext() {
            if (iterator.hasNext()) {
                val item = iterator.next()
                nextItem = item
                if (predicate(item)) {
                    nextState = 1
                    return
                }
            }
            nextState = 0
        }

        override fun next(): T {
            if (nextState == -1) calcNext()
            if (nextState == 0)
                throw NoSuchElementException()
            @Suppress("UNCHECKED_CAST")
            val result = nextItem as T

            nextItem = null
            nextState = -1
            return result
        }

        override fun hasNext(): Boolean {
            if (nextState == -1) calcNext()
            return nextState == 1 || nextItem != null
        }
    }
}

inline fun <T> List<T>.takeLastUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in reversed()) {
        list.add(item)
        if (!predicate(item))
            break
    }
    return list
}

inline fun <T> Array<out T>.takeLastUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in reversed()) {
        list.add(item)
        if (!predicate(item))
            break
    }

    return list
}

inline fun <T> Iterable<T>.dropUntil(predicate: (T) -> Boolean): List<T> {
    var yielding = false
    val list = mutableListOf<T>()
    for (item in this)
        if (yielding)
            list.add(item)
        else if (!predicate(item)) {
            yielding = true
        }
    return list
}

inline fun <T> Array<out T>.dropUntil(predicate: (T) -> Boolean): List<T> {
    var yielding = false
    val list = mutableListOf<T>()
    for (item in this)
        if (yielding)
            list.add(item)
        else if (!predicate(item)) {
            yielding = true
        }
    return list
}

fun <T> Sequence<T>.dropUntil(predicate: (T) -> Boolean): Sequence<T> {
    return DropUntilSequence(this, predicate)
}

private class DropUntilSequence<T>(
    private val sequence: Sequence<T>,
    private val predicate: (T) -> Boolean
) : Sequence<T> {

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator = sequence.iterator()
        var dropState: Int = -1 // -1 for not dropping, 0 for normal iteration

        private fun drop() {
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (!predicate(item)) {
                    dropState = 0
                    return
                }
            }

            dropState = 0
        }

        override fun next(): T {
            if (dropState == -1) drop()
            return iterator.next()
        }

        override fun hasNext(): Boolean {
            if (dropState == -1) drop()
            return dropState == 1 || iterator.hasNext()
        }
    }
}

inline fun <T, R> Iterable<T>.firstNotNullResultOrNull(transform: (T) -> R?): R? {
    for (element in this) {
        val result = transform(element)
        if (result != null) return result
    }
    return null
}

inline fun <T, R> Array<T>.firstNotNullResultOrNull(transform: (T) -> R?): R? {
    for (element in this) {
        val result = transform(element)
        if (result != null) return result
    }
    return null
}

inline fun <T, R> Sequence<T>.firstNotNullResultOrNull(transform: (T) -> R?): R? {
    for (element in this) {
        val result = transform(element)
        if (result != null) return result
    }
    return null
}

inline fun <T, R> Iterable<T>.firstNotNullResult(transform: (T) -> R?): R =
    firstNotNullResultOrNull(transform) ?: throw NoSuchElementException("No element not null result found")

inline fun <T, R> Array<T>.firstNotNullResultOr(transform: (T) -> R?): R =
    firstNotNullResultOrNull(transform) ?: throw NoSuchElementException("No element not null result found")

inline fun <T, R> Sequence<T>.firstNotNullResultOr(transform: (T) -> R?): R =
    firstNotNullResultOrNull(transform) ?: throw NoSuchElementException("No element not null result found")

inline fun <reified T : Any> Iterable<*>.firstIsInstanceOrNull(): T? {
    for (element in this) if (element is T) return element
    return null
}

inline fun <reified T : Any> Array<*>.firstIsInstanceOrNull(): T? {
    for (element in this) if (element is T) return element
    return null
}

inline fun <reified T : Any> Sequence<*>.firstIsInstanceOrNull(): T? {
    for (element in this) if (element is T) return element
    return null
}

inline fun <reified T> Iterable<*>.firstIsInstance(): T {
    for (element in this) if (element is T) return element
    throw NoSuchElementException("No element of given type found")
}

inline fun <reified T> Array<*>.firstIsInstance(): T {
    for (element in this) if (element is T) return element
    throw NoSuchElementException("No element of given type found")
}

inline fun <reified T> Sequence<*>.firstIsInstance(): T {
    for (element in this) if (element is T) return element
    throw NoSuchElementException("No element of given type found")
}

inline fun <reified T : Any> Iterable<*>.lastIsInstanceOrNull(): T? =
    reversed().firstIsInstanceOrNull()

inline fun <reified T : Any> Array<*>.lastIsInstanceOrNull(): T? =
    reversed().firstIsInstanceOrNull()