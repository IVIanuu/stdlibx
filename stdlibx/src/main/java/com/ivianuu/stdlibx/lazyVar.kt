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

import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface LazyVar<T> : ReadWriteProperty<Any, T> {
    var value: T
    fun isInitialized(): Boolean

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T = value
}

fun <T> lazyVar(initializer: () -> T): LazyVar<T> = SynchronizedLazyVarImpl(initializer)

fun <T> lazyVar(mode: LazyThreadSafetyMode, initializer: () -> T): LazyVar<T> =
    when (mode) {
        LazyThreadSafetyMode.SYNCHRONIZED -> SynchronizedLazyVarImpl(initializer)
        LazyThreadSafetyMode.PUBLICATION -> SafePublicationLazyVarImpl(initializer)
        LazyThreadSafetyMode.NONE -> UnsafeLazyVarImpl(initializer)
    }

fun <T> lazyVar(lock: Any?, initializer: () -> T): LazyVar<T> =
    SynchronizedLazyVarImpl(initializer, lock)

fun <T> unsafeLazyVar(initializer: () -> T): LazyVar<T> = UnsafeLazyVarImpl(initializer)

private object UNINITIALIZED_VALUE

private class SynchronizedLazyVarImpl<T>(initializer: () -> T, lock: Any? = null) : LazyVar<T>,
    Serializable {
    private var initializer: (() -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override var value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }
        set(value) {
            synchronized(lock) {
                _value = value
                initializer = null
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "LazyVar value not initialized yet."

}


private class SafePublicationLazyVarImpl<T>(initializer: () -> T) : LazyVar<T>, Serializable {
    @Volatile private var initializer: (() -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    // this final field is required to enable safe publication of constructed instance
    private val final: Any = UNINITIALIZED_VALUE

    override var value: T
        get() {
            val value = _value
            if (value !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return value as T
            }

            val initializerValue = initializer
            // if we see null in initializer here, it means that the value is already set by another thread
            if (initializerValue != null) {
                val newValue = initializerValue()
                if (valueUpdater.compareAndSet(this, UNINITIALIZED_VALUE, newValue)) {
                    initializer = null
                    return newValue
                }
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }
        set(value) {
            initializer = null
            _value = value
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "LazyVar value not initialized yet."

    companion object {
        private val valueUpdater =
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater(
                SafePublicationLazyVarImpl::class.java,
                Any::class.java,
                "_value"
            )
    }
}

private class UnsafeLazyVarImpl<T>(initializer: () -> T) : LazyVar<T>, Serializable {
    private var initializer: (() -> T)? = initializer
    private var _value: Any? = UNINITIALIZED_VALUE

    override var value: T
        get() {
            if (_value === UNINITIALIZED_VALUE) {
                _value = initializer!!()
                initializer = null
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }
        set(value) {
            initializer = null
            _value = value
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "LazyVar value not initialized yet."

}