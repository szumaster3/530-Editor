package com.cache.io

class ByteStream(
    @JvmField var Buffer: ByteArray,
) {
    @JvmField
    var Offset: Int = 0

    fun readDWord(): Int =
        ((Buffer[(Offset++)].toInt() and 0xFF) shl 24) + ((Buffer[(Offset++)].toInt() and 0xFF) shl 16) +
                ((Buffer[(Offset++)].toInt() and 0xFF) shl 8) +
                (Buffer[(Offset++)].toInt() and 0xFF)

    fun readUnsignedByte(): Int = Buffer[(Offset++)].toInt() and 0xFF

    fun readUnsignedWord(): Int {
        if (this.Offset + 1 < Buffer.size) {
            return ((Buffer[(Offset++)].toInt() and 0xFF) shl 8) + (Buffer[(Offset++)].toInt() and 0xFF)
        }
        return 0
    }

    fun read3Bytes(): Int =
        ((Buffer[(Offset++)].toInt() and 0xFF) shl 16) + ((Buffer[(Offset++)].toInt() and 0xFF) shl 8) +
                (Buffer[(Offset++)].toInt() and 0xFF)

}
