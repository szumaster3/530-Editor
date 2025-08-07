package cache.alex.util

import java.io.EOFException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


object FileChannelUtils {
    
    @Throws(IOException::class)
    fun readFully(channel: FileChannel, buffer: ByteBuffer, ptr: Long) {
        var ptr = ptr
        while (buffer.remaining() > 0) {
            val read = channel.read(buffer, ptr).toLong()
            if (read == -1L) {
                throw EOFException()
            } else {
                ptr += read
            }
        }
    }
}
