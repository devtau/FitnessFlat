package com.devtau.ironHeroes

import android.util.SparseIntArray


class JavaCode {
    fun toJSON(collection: Collection<Int>): String {
        val sb = StringBuilder()
        sb.append("[")
        val iterator = collection.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            sb.append(element)
            if (iterator.hasNext()) {
                sb.append(", ")
            }
        }
        sb.append("]")
        return sb.toString()
    }
}

fun main() {
    val ar = arrayOf(10, 20, 20, 10, 10, 30, 50, 10, 20)
    val result = sockMerchant(ar.size, ar)
    println(result)
}

fun sockMerchant(n: Int, ar: Array<Int>): Int {
    val frequencies = SparseIntArray()
    for (nextInputInt in ar) {
        val value = frequencies[nextInputInt]
        frequencies.put(nextInputInt, value + 1)
    }
    println(frequencies)
    var pairs = 0
    return pairs
}