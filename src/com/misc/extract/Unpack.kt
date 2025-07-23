package com.misc.extract

import com.cache.store.FileOperations.WriteFile
import com.cache.io.ByteStream
import com.misc.extract.Core.checkNames
import com.misc.extract.PackJAG.method73
import launcher.Main
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.util.zip.GZIPInputStream

object Unpack {
    fun unpack(
        paramByteStream: ByteStream,
        paramString: String,
    ): ByteArray {
        var arrayOfByte2: ByteArray
        if ((paramByteStream.readUnsignedByte() == 31) &&
            (paramByteStream.readUnsignedByte() == 139) &&
            (paramByteStream.readUnsignedByte() == 8)
        ) {
            var i = 0
            val arrayOfByte1 = ByteArray(9999999)
            try {
                val localGZIPInputStream = GZIPInputStream(ByteArrayInputStream(paramByteStream.Buffer))
                while (true) {
                    if (i == arrayOfByte1.size) throw RuntimeException("buffer overflow!")
                    val k = localGZIPInputStream.read(arrayOfByte1, i, arrayOfByte1.size - i)
                    if (k == -1) break
                    i += k
                }
            } catch (localIOException: IOException) {
            }
            arrayOfByte2 = ByteArray(i)
            for (k in 0 until i) {
                arrayOfByte2[k] = arrayOfByte1[k]
            }
            return arrayOfByte2
        }
        val localJagFile = JagFile(paramByteStream.Buffer)
        for (j in 0 until localJagFile.dataSize) {
            arrayOfByte2 = ByteArray(localJagFile.fileSize!![j])
            val arrayOfByte3 = localJagFile.readfile(j, arrayOfByte2)
            if (arrayOfByte3 != null) {
                WriteFile(paramString + "/" + checkNames(localJagFile.filename!![j]), arrayOfByte3)
                Main.log(this.javaClass.name, "Extracted :" + checkNames(localJagFile.filename!![j]))
            }
        }
        return paramByteStream.Buffer
    }

    fun unpackSecondMethod(paramByteStream: ByteStream): ByteArray {
        val i = paramByteStream.readUnsignedByte()
        val j = paramByteStream.readDWord()
        val k = paramByteStream.readDWord()
        if ((k > 0) && (k < 10000000)) {
            var arrayOfByte = ByteArray(k)
            when (i) {
                1 -> method73(arrayOfByte, k, paramByteStream.Buffer, j, 9)
                2 -> {
                    try {
                        val localDataInputStream =
                            DataInputStream(GZIPInputStream(ByteArrayInputStream(paramByteStream.Buffer, 9, k)))
                        localDataInputStream.readFully(arrayOfByte)
                        localDataInputStream.close()
                    } catch (localIOException: IOException) {
                    }
                    arrayOfByte = paramByteStream.Buffer
                }

                else -> arrayOfByte = paramByteStream.Buffer
            }

            return arrayOfByte
        }

        return paramByteStream.Buffer
    }
}
