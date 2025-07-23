package com.cache.util.crc32

import java.util.zip.CRC32

object CRC32HGenerator {
    val CRC32Instance: CRC32 = CRC32()

    @JvmStatic
    fun getHash(data: ByteArray): Int {
        return getHash(data, 0, data.size)
    }

    @JvmStatic
    fun getHash(data: ByteArray?, offset: Int, length: Int): Int {
        val var3 = CRC32Instance
        val var4 = CRC32Instance
        synchronized(CRC32Instance) {
            CRC32Instance.update(data, offset, length)
            val hash = CRC32Instance.value.toInt()
            CRC32Instance.reset()
            return hash
        }
    }
}
