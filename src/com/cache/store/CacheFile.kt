package com.cache.store

import com.cache.io.ByteStream
import java.io.RandomAccessFile

class CacheFile(
    paramArrayOfByte: ByteArray,
    var dataFile: RandomAccessFile,
) {
    var numberOfFiles: Int = (paramArrayOfByte.size / 6)
    var fileLength: IntArray = IntArray(this.numberOfFiles)

    var fileStart: IntArray = IntArray(this.numberOfFiles)

    init {
        val localByteStream = ByteStream(paramArrayOfByte)
        for (i in 0 until this.numberOfFiles) {
            fileLength[i] = localByteStream.read3Bytes()
            fileStart[i] = localByteStream.read3Bytes()
        }
    }

    private fun readBlock(paramInt: Int): ByteArray? {
        try {
            val arrayOfByte = ByteArray(520)
            dataFile.seek((paramInt * 520).toLong())
            dataFile.readFully(arrayOfByte)
            return arrayOfByte
        } catch (localException: Exception) {
        }
        return null
    }

    fun getFile(paramInt: Int): ByteStream? {
        if (fileStart[paramInt] <= 0) return null
        val arrayOfByte1 = ByteArray(fileLength[paramInt])
        var i = fileStart[paramInt]
        var j = 0
        while (true) {
            val arrayOfByte2 = readBlock(i) ?: return null
            i =
                ((arrayOfByte2[4].toInt() and 0xFF) shl 16) + ((arrayOfByte2[5].toInt() and 0xFF) shl 8) +
                        (arrayOfByte2[6].toInt() and 0xFF)
            if (fileLength[paramInt] - j - 8 > 512) {
                System.arraycopy(arrayOfByte2, 8, arrayOfByte1, j, 512)
            } else {
                System.arraycopy(arrayOfByte2, 8, arrayOfByte1, j, fileLength[paramInt] - j - 8)
                return ByteStream(arrayOfByte1)
            }
            j += 512
        }
    }
}
