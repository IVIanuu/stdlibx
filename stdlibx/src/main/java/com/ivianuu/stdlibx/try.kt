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

package com.ivianuu.stdlibx

inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}

inline fun tryOrPrint(block: () -> Unit) = try {
    block()
} catch (e: Exception) {
    e.printStackTrace()
}

inline fun trySilently(block: () -> Unit) = try {
    block()
} catch (e: Exception) {
}

inline fun <T> tryOrElse(defaultValue: T, block: () -> T): T = tryOrNull(block)
    ?: defaultValue