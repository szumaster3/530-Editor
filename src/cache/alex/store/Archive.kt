package cache.alex.store

import cache.alex.io.InputStream
import cache.alex.io.OutputStream
import cache.alex.util.bzip2.BZip2Compressor
import cache.alex.util.bzip2.BZip2Decompressor
import cache.alex.util.crc32.CRC32HGenerator
import cache.alex.util.gzip.GZipCompressor
import cache.alex.util.gzip.GZipDecompressor
import cache.alex.util.whirlpool.Whirlpool


class Archive {
    
    val id: Int
    
    
    var revision: Int = 0

    
    var compression: Int = 0
        private set

    
    var data: ByteArray? = null
        private set
    
    
    var keys: IntArray? = null

    
    constructor(id: Int, archive: ByteArray, keys: IntArray?) {
        this.id = id
        this.keys = keys
        this.decompress(archive)
    }

    
    constructor(id: Int, compression: Int, revision: Int, data: ByteArray?) {
        this.id = id
        this.compression = compression
        this.revision = revision
        this.data = data
    }

    
    fun compress(): ByteArray {
        val stream = OutputStream()
        stream.writeByte(this.compression)
        var compressedData1: ByteArray?
        when (this.compression) {
            0 -> {
                compressedData1 = this.data
                stream.writeInt(data!!.size)
            }

            1 -> {
                val compressed: Any? = null
                compressedData1 = BZip2Compressor.compress(this.data!!)
                stream.writeInt(compressedData1!!.size)
                stream.writeInt(data!!.size)
                compressedData1 = GZipCompressor.compress(this.data!!)
                stream.writeInt(compressedData1!!.size)
                stream.writeInt(data!!.size)
            }

            else -> {
                compressedData1 = GZipCompressor.compress(this.data!!)
                stream.writeInt(compressedData1!!.size)
                stream.writeInt(data!!.size)
            }
        }

        stream.writeBytes(compressedData1)
        if (this.keys != null && keys!!.size == 4) {
            stream.encodeXTEA(this.keys, 5, stream.offset)
        }

        if (this.revision != -1) {
            stream.writeShort(this.revision)
        }

        val compressed1 = ByteArray(stream.offset)
        stream.offset = 0
        stream.getBytes(compressed1, 0, compressed1.size)
        return compressed1
    }

    private fun decompress(archive: ByteArray) {
        val stream = InputStream(archive)
        if (this.keys != null && keys!!.size == 4) {
            stream.decodeXTEA(this.keys)
        }

        this.compression = stream.readUnsignedByte()
        val compressedLength = stream.readInt()
        if (compressedLength >= 0 && compressedLength <= 1000000) {
            val length: Int
            when (this.compression) {
                0 -> {
                    this.data = ByteArray(compressedLength)
                    this.checkRevision(compressedLength, archive, stream.offset)
                    stream.readBytes(this.data, 0, compressedLength)
                }

                1 -> {
                    length = stream.readInt()
                    if (length <= 0) {
                        this.data = null
                    } else {
                        this.data = ByteArray(length)
                        this.checkRevision(compressedLength, archive, stream.offset)
                        BZip2Decompressor.decompress(this.data, archive, compressedLength, 9)
                    }
                }

                else -> {
                    length = stream.readInt()
                    if (length > 0 && length <= 1000000000) {
                        this.data = ByteArray(length)
                        this.checkRevision(compressedLength, archive, stream.offset)
                        if (!GZipDecompressor.decompress(stream, this.data)) {
                            this.data = null
                        }
                    } else {
                        this.data = null
                    }
                }
            }
        } else {
            throw RuntimeException("INVALID ARCHIVE HEADER")
        }
    }

    private fun checkRevision(
        compressedLength: Int,
        archive: ByteArray,
        o: Int,
    ) {
        val stream = InputStream(archive)
        val offset = stream.offset
        if (stream.length - (compressedLength + o) >= 2) {
            stream.offset = stream.length - 2
            this.revision = stream.readUnsignedShort()
            stream.offset = offset
        } else {
            this.revision = -1
        }
    }

    
    fun editNoRevision(
        data: ByteArray?,
        mainFile: MainFile,
    ): Array<Any>? {
        this.data = data
        if (this.compression == 1) {
            this.compression = 2
        }

        val compressed = this.compress()
        return if (!mainFile.putArchiveData(this.id, compressed)) {
            null
        } else {
            arrayOf(
                CRC32HGenerator.getHash(compressed),
                Whirlpool.whirlpool(compressed, 0, compressed.size),
            )
        }
    }

    val decompressedLength: Int
        
        get() = data!!.size
}
