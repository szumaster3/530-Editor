package com.cache.util.gzip

import com.cache.io.Stream
import java.util.zip.Inflater

object GZipDecompressor {
    private val inflaterInstance = Inflater(true)

    @JvmStatic
    fun decompress(stream: Stream, data: ByteArray?): Boolean {
        val var2 = inflaterInstance
        val var3 = inflaterInstance
        synchronized(inflaterInstance) {
            if (stream.buffer[stream.getOffset()].toInt() == 31 && stream.buffer[stream.getOffset() + 1].toInt() == -117) {
                try {
                    inflaterInstance.setInput(
                        stream.buffer,
                        stream.getOffset() + 10,
                        -stream.getOffset() - 18 + stream.buffer.size
                    )
                    inflaterInstance.inflate(data)
                } catch (var5: Exception) {
                    inflaterInstance.reset()
                    return false
                }

                inflaterInstance.reset()
                return true
            } else {
                return false
            }
        }
    }
}
