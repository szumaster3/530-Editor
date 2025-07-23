package com.cache.util

object ArrayUtils {
    fun <T> isEmpty(array: Array<T?>): Boolean {
        for (i in array.indices) {
            if (array[i] != null) {
                return false
            }
        }
        return true
    }

    fun toByteArray(s: String): ByteArray {
        val data = ByteArray(s.length)

        for (i in data.indices) {
            data[i] = s[i].code.toByte()
        }

        return data
    }

    fun <T1, T2> arraysMatch(array1: Array<T1>, array2: Array<T2>): Boolean {
        if (array1.size != array2.size) {
            return false
        }

        for (i in array1.indices) {
            if (array1[i] !== array2[i]) {
                return false
            }
        }
        return true
    }
}
