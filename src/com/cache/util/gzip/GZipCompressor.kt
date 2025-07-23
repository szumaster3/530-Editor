package com.cache.util.gzip

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

object GZipCompressor {
    @JvmStatic
    fun compress(data: ByteArray): ByteArray? {
        val compressedBytes = ByteArrayOutputStream()

        try {
            val var3 = GZIPOutputStream(compressedBytes)
            var3.write(data)
            var3.finish()
            var3.close()
            return compressedBytes.toByteArray()
        } catch (var31: IOException) {
            var31.printStackTrace()
            return null
        }
    }
}
