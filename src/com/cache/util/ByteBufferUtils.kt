package com.cache.util

import com.cache.util.whirlpool.Whirlpool
import java.nio.ByteBuffer
import java.util.*
import java.util.zip.CRC32
import java.util.zip.Checksum

/**
 * Contains [ByteBuffer]-related utility methods.
 *
 * @author Graham
 * @author `Discardedx2
 */
object ByteBufferUtils {
    /**
     * The modified set of 'extended ASCII' characters used by the client.
     */
    private val CHARACTERS = charArrayOf(
        '\u20AC',
        '\u0000',
        '\u201A',
        '\u0192',
        '\u201E',
        '\u2026',
        '\u2020',
        '\u2021',
        '\u02C6',
        '\u2030',
        '\u0160',
        '\u2039',
        '\u0152',
        '\u0000',
        '\u017D',
        '\u0000',
        '\u0000',
        '\u2018',
        '\u2019',
        '\u201C',
        '\u201D',
        '\u2022',
        '\u2013',
        '\u2014',
        '\u02DC',
        '\u2122',
        '\u0161',
        '\u203A',
        '\u0153',
        '\u0000',
        '\u017E',
        '\u0178'
    )

    /**
     * Gets a null-terminated string from the specified buffer, using a
     * modified ISO-8859-1 character set.
     *
     * @param buf The buffer.
     * @return The decoded string.
     */
    fun getJagexString(buf: ByteBuffer): String {
        val bldr = StringBuilder()
        var b: Int
        while ((buf.get().also { b = it.toInt() }).toInt() != 0) {
            if (b >= 127 && b < 160) {
                val curChar = CHARACTERS[b - 128]
                if (curChar.code != 0) {
                    bldr.append(curChar)
                }
            } else {
                bldr.append(b.toChar())
            }
        }
        return bldr.toString()
    }

    /**
     * Reads a 'tri-byte' from the specified buffer.
     *
     * @param buf The buffer.
     * @return The value.
     */
    fun getMedium(buf: ByteBuffer): Int {
        return ((buf.get().toInt() and 0xFF) shl 16) or ((buf.get().toInt() and 0xFF) shl 8) or (buf.get()
            .toInt() and 0xFF)
    }

    /**
     * Writes a 'tri-byte' to the specified buffer.
     *
     * @param buf   The buffer.
     * @param value The value.
     */
    fun putTriByte(buf: ByteBuffer, value: Int) {
        buf.put((value shr 16).toByte())
        buf.put((value shr 8).toByte())
        buf.put(value.toByte())
    }

    /**
     * Calculates the CRC32 checksum of the specified buffer.
     *
     * @param buffer The buffer.
     * @return The CRC32 checksum.
     */
    fun getCrcChecksum(buffer: ByteBuffer): Int {
        val crc: Checksum = CRC32()
        for (i in 0 until buffer.limit()) {
            crc.update(buffer[i].toInt())
        }
        return crc.value.toInt()
    }

    /**
     * Calculates the whirlpool digest of the specified buffer.
     *
     * @param buf The buffer.
     * @return The 64-byte whirlpool digest.
     */
    fun getWhirlpoolDigest(buf: ByteBuffer): ByteArray {
        val bytes = ByteArray(buf.limit())
        buf[bytes]
        return Whirlpool.whirlpool(bytes, 0, bytes.size)
    }

    /**
     * Converts the contents of the specified byte buffer to a string, which is
     * formatted similarly to the output of the [Arrays.toString]
     * method.
     *
     * @param buffer The buffer.
     * @return The string.
     */
    fun toString(buffer: ByteBuffer): String {
        val builder = StringBuilder("[")
        for (i in 0 until buffer.limit()) {
            var hex = Integer.toHexString(buffer[i].toInt() and 0xFF).uppercase(Locale.getDefault())
            if (hex.length == 1) hex = "0$hex"

            builder.append("0x").append(hex)
            if (i != buffer.limit() - 1) {
                builder.append(", ")
            }
        }
        builder.append("]")
        return builder.toString()
    }
}
