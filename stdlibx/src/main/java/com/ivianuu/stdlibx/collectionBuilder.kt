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

// todo builder inference

inline class ListBuilder<E>(private val list: MutableList<E>) : MutableList<E> {

    fun build(): List<E> = list

    override val size: Int get() = list.size

    override fun contains(element: E): Boolean = list.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = list.containsAll(elements)

    override fun get(index: Int): E = list[index]

    override fun indexOf(element: E): Int = list.indexOf(element)

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): MutableIterator<E> = list.iterator()

    override fun lastIndexOf(element: E): Int = list.lastIndexOf(element)

    override fun add(element: E): Boolean = list.add(element)

    override fun add(index: Int, element: E) = list.add(index, element)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = list.addAll(index, elements)

    override fun addAll(elements: Collection<E>): Boolean = list.addAll(elements)

    override fun clear() = list.clear()

    override fun listIterator(): MutableListIterator<E> = list.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = list.listIterator(index)

    override fun remove(element: E): Boolean = list.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = list.removeAll(elements)

    override fun removeAt(index: Int): E = list.removeAt(index)

    override fun retainAll(elements: Collection<E>): Boolean = list.retainAll(elements)

    override fun set(index: Int, element: E): E = list.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        list.subList(fromIndex, toIndex)

}

fun <E> list(block: ListBuilder<E>.() -> Unit): List<E> =
    ListBuilder<E>(mutableListOf()).apply(block).build()

fun <E> mutableList(block: ListBuilder<E>.() -> Unit): MutableList<E> =
    ListBuilder<E>(mutableListOf()).apply(block).build() as MutableList<E>

fun <E> arrayList(block: ListBuilder<E>.() -> Unit): ArrayList<E> =
    ListBuilder<E>(arrayListOf()).apply(block).build() as ArrayList<E>

inline class SetBuilder<E>(private val set: MutableSet<E>) : MutableSet<E> {

    fun build(): Set<E> = set

    override val size: Int
        get() = set.size

    override fun add(element: E): Boolean = set.add(element)

    override fun addAll(elements: Collection<E>): Boolean = set.addAll(elements)

    override fun clear() = set.clear()

    override fun iterator(): MutableIterator<E> = set.iterator()

    override fun remove(element: E): Boolean = set.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = set.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = set.retainAll(elements)

    override fun contains(element: E): Boolean = set.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = set.containsAll(elements)

    override fun isEmpty(): Boolean = set.isEmpty()

}

fun <E> set(block: SetBuilder<E>.() -> Unit): Set<E> =
    SetBuilder<E>(mutableSetOf()).apply(block).build()

fun <E> mutableSet(block: SetBuilder<E>.() -> Unit): MutableSet<E> =
    SetBuilder<E>(mutableSetOf()).apply(block).build() as MutableSet<E>

fun <E> hashSet(block: SetBuilder<E>.() -> Unit): HashSet<E> =
    SetBuilder<E>(hashSetOf()).apply(block).build() as HashSet<E>

fun <E> linkedSet(block: SetBuilder<E>.() -> Unit): LinkedHashSet<E> =
    SetBuilder<E>(linkedSetOf()).apply(block).build() as LinkedHashSet<E>

inline class MapBuilder<K, V>(private val map: MutableMap<K, V>) : MutableMap<K, V> {

    infix fun K.to(value: V) {
        put(this, value)
    }

    fun build(): Map<K, V> = map

    override val size: Int
        get() = map.size

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries

    override val keys: MutableSet<K>
        get() = map.keys

    override val values: MutableCollection<V>
        get() = map.values

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: K): V? = map.get(key)

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun clear() = map.clear()

    override fun put(key: K, value: V): V? = map.put(key, value)

    override fun putAll(from: Map<out K, V>) = map.putAll(from)

    override fun remove(key: K): V? = map.remove(key)

}

inline fun <K, V> map(block: MapBuilder<K, V>.() -> Unit): Map<K, V> =
    MapBuilder<K, V>(mutableMapOf()).apply(block).build()

inline fun <K, V> mutableMap(block: MapBuilder<K, V>.() -> Unit): MutableMap<K, V> =
    MapBuilder<K, V>(mutableMapOf()).apply(block).build() as MutableMap<K, V>

inline fun <K, V> hashMap(block: MapBuilder<K, V>.() -> Unit): Map<K, V> =
    MapBuilder<K, V>(hashMapOf()).apply(block).build() as HashMap<K, V>

inline fun <K, V> linkedMap(block: MapBuilder<K, V>.() -> Unit): Map<K, V> =
    MapBuilder<K, V>(linkedMapOf()).apply(block).build() as LinkedHashMap<K, V>