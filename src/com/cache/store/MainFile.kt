package com.cache.store

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * The type Main file.
 */
class MainFile internal constructor(
    /**
     * Gets id.
     *
     * @return the id
     */
    @JvmField val id: Int,
    data: RandomAccessFile,
    index: RandomAccessFile,
    private val newProtocol: Boolean,
) {
    private val index: FileChannel = index.channel

    private val data: FileChannel = data.channel

    /**
     * Gets archive.
     *
     * @param id the id
     * @return the archive
     */
    fun getArchive(id: Int): Archive? = this.getArchive(id, null)

    /**
     * Gets archive.
     *
     * @param id   the id
     * @param keys the keys
     * @return the archive
     */
    fun getArchive(
        id: Int,
        keys: IntArray?,
    ): Archive? {
        val data = this.getArchiveData(id)
        return if (data == null) null else Archive(id, data, keys)
    }

    /**
     * Get archive data byte [ ].
     *
     * @param archiveId the archive id
     * @return the byte [ ]
     */
    fun getArchiveData(archiveId: Int): ByteArray? {
        val var2 = this.data
        val var3 = this.data
        synchronized(this.data) {
            try {
                tempBuffer.position(0).limit(6)
                index.read(tempBuffer, archiveId * 6L)
                tempBuffer.flip()
                val var16 = getMediumInt(tempBuffer)
                var block = getMediumInt(tempBuffer)
                val var10000: Any?
                val var100001: ByteArray?
                if (var16 < 0) {
                    var10000 = null
                    var100001 = var10000
                    return var100001
                } else if (block <= 0 || block.toLong() > data.size() / 520L) {
                    var10000 = null
                    var100001 = var10000
                    return var100001
                } else {
                    val fileBuffer = ByteBuffer.allocate(var16)
                    var remaining = var16
                    var chunk = 0
                    val blockLen = if (this.newProtocol && archiveId > '\uffff'.code) 510 else 512
                    val headerLen = if (this.newProtocol && archiveId > '\uffff'.code) 10 else 8

                    while (remaining > 0) {
                        if (block == 0) {
                            println(archiveId.toString() + ", " + this.newProtocol)
                            var10000 = null
                            var100001 = var10000
                            return var100001
                        }

                        val blockSize = if (remaining > blockLen) blockLen else remaining
                        tempBuffer.position(0).limit(blockSize + headerLen)
                        data.read(tempBuffer, block * 520L)
                        tempBuffer.flip()
                        val currentFile: Int
                        val currentChunk: Int
                        val nextBlock: Int
                        val currentIndex: Int
                        if (this.newProtocol && archiveId > '\uffff'.code) {
                            currentFile = tempBuffer.getInt()
                            currentChunk = tempBuffer.getShort().toInt() and '\uffff'.code
                            nextBlock = getMediumInt(tempBuffer)
                            currentIndex = tempBuffer.get().toInt() and 255
                        } else {
                            currentFile = tempBuffer.getShort().toInt() and '\uffff'.code
                            currentChunk = tempBuffer.getShort().toInt() and '\uffff'.code
                            nextBlock = getMediumInt(tempBuffer)
                            currentIndex = tempBuffer.get().toInt() and 255
                        }

                        if ((archiveId == currentFile || archiveId > '\uffff'.code) && chunk == currentChunk && this.id == currentIndex) {
                            if (nextBlock >= 0 && nextBlock.toLong() <= data.size() / 520L) {
                                fileBuffer.put(tempBuffer)
                                remaining -= blockSize
                                block = nextBlock
                                ++chunk
                                continue
                            }

                            var10000 = null
                            var100001 = var10000
                            return var100001
                        }

                        var10000 = null
                        var100001 = var10000
                        return var100001
                    }

                    val var18 = fileBuffer.flip().array() as ByteArray
                    return var18
                }
            } catch (var181: Exception) {
                return null
            }
        }
    }

    /**
     * Put archive boolean.
     *
     * @param archive the archive
     * @return the boolean
     */
    fun putArchive(archive: Archive): Boolean = this.putArchiveData(archive.id, archive.data!!)

    /**
     * Put archive data boolean.
     *
     * @param id      the id
     * @param archive the archive
     * @return the boolean
     */
    fun putArchiveData(
        id: Int,
        archive: ByteArray,
    ): Boolean {
        val buffer = ByteBuffer.wrap(archive)
        var done = this.putArchiveData(id, buffer, archive.size, true)
        if (!done) {
            done = this.putArchiveData(id, buffer, archive.size, false)
        }

        return done
    }

    /**
     * Put archive data boolean.
     *
     * @param archiveId the archive id
     * @param archive   the archive
     * @param size      the size
     * @param exists    the exists
     * @return the boolean
     */
    fun putArchiveData(
        archiveId: Int,
        archive: ByteBuffer,
        size: Int,
        exists: Boolean,
    ): Boolean {
        var exists = exists
        val var5 = this.data
        val var6 = this.data
        synchronized(this.data) {
            try {
                val var10000: Boolean
                var var16: Int
                val var100001: Boolean
                if (exists) {
                    if ((archiveId * 6L + 6) > index.size()) {
                        var10000 = false
                        var100001 = var10000
                        return var100001
                    }

                    tempBuffer.position(0).limit(6)
                    index.read(tempBuffer, archiveId * 6L)
                    tempBuffer.flip().position(3)
                    var16 = getMediumInt(tempBuffer)
                    if (var16 <= 0 || var16.toLong() > data.size() / 520L) {
                        var10000 = false
                        var100001 = var10000
                        return var100001
                    }
                } else {
                    var16 = (data.size() + 520L - 1L).toInt() / 520
                    if (var16 == 0) {
                        var16 = 1
                    }
                }

                tempBuffer.position(0)
                putMediumInt(tempBuffer, size)
                putMediumInt(tempBuffer, var16)
                tempBuffer.flip()
                index.write(tempBuffer, archiveId * 6L)
                var remaining = size
                var chunk = 0
                val blockLen = if (this.newProtocol && archiveId > '\uffff'.code) 510 else 512

                val headerLen = if (this.newProtocol && archiveId > '\uffff'.code) 10 else 8
                while (remaining > 0) {
                    var nextBlock = 0
                    var blockSize: Int
                    if (exists) {
                        tempBuffer.position(0).limit(headerLen)
                        data.read(tempBuffer, var16 * 520L)
                        tempBuffer.flip()
                        val currentChunk: Int
                        val currentIndex: Int
                        if (this.newProtocol && archiveId > '\uffff'.code) {
                            blockSize = tempBuffer.getInt()
                            currentChunk = tempBuffer.getShort().toInt() and '\uffff'.code
                            nextBlock = getMediumInt(tempBuffer)
                            currentIndex = tempBuffer.get().toInt() and 255
                        } else {
                            blockSize = tempBuffer.getShort().toInt() and '\uffff'.code
                            currentChunk = tempBuffer.getShort().toInt() and '\uffff'.code
                            nextBlock = getMediumInt(tempBuffer)
                            currentIndex = tempBuffer.get().toInt() and 255
                        }

                        if (archiveId != blockSize && archiveId <= '\uffff'.code || chunk != currentChunk || this.id != currentIndex) {
                            var10000 = false
                            var100001 = var10000
                            return var100001
                        }

                        if (nextBlock < 0 || nextBlock.toLong() > data.size() / 520L) {
                            var10000 = false
                            var100001 = var10000
                            return var100001
                        }
                    }

                    if (nextBlock == 0) {
                        exists = false
                        nextBlock = ((data.size() + 520L - 1L) / 520L).toInt()
                        if (nextBlock == 0) {
                            nextBlock = 1
                        }

                        if (nextBlock == var16) {
                            ++nextBlock
                        }
                    }

                    if (remaining <= blockLen) {
                        nextBlock = 0
                    }

                    tempBuffer.position(0).limit(520)
                    if (this.newProtocol && archiveId > '\uffff'.code) {
                        tempBuffer.putInt(archiveId)
                        tempBuffer.putShort(chunk.toShort())
                        putMediumInt(tempBuffer, nextBlock)
                        tempBuffer.put(id.toByte())
                    } else {
                        tempBuffer.putShort(archiveId.toShort())
                        tempBuffer.putShort(chunk.toShort())
                        putMediumInt(tempBuffer, nextBlock)
                        tempBuffer.put(id.toByte())
                    }

                    blockSize = if (remaining > blockLen) blockLen else remaining
                    archive.limit(archive.position() + blockSize)
                    tempBuffer.put(archive)
                    tempBuffer.flip()
                    data.write(tempBuffer, var16 * 520L)
                    remaining -= blockSize
                    var16 = nextBlock
                    ++chunk
                }

                var10000 = true
                return var10000
            } catch (var17: Exception) {
                return false
            }
        }
    }

    @get:Throws(IOException::class)
    val archivesCount: Int
        /**
         * Gets archives count.
         *
         * @return the archives count
         * @throws IOException the io exception
         */
        get() {
            val var1 = this.index
            val var2 = this.index
            synchronized(this.index) {
                return (index.size() / 6L).toInt()
            }
        }

    companion object {
        /**
         * The constant IDX_BLOCK_LEN.
         */
        const val IDX_BLOCK_LEN: Int = 6

        /**
         * The constant HEADER_LEN.
         */
        const val HEADER_LEN: Int = 8

        /**
         * The constant EXPANDED_HEADER_LEN.
         */
        const val EXPANDED_HEADER_LEN: Int = 10

        /**
         * The constant BLOCK_LEN.
         */
        const val BLOCK_LEN: Int = 512

        /**
         * The constant EXPANDED_BLOCK_LEN.
         */
        const val EXPANDED_BLOCK_LEN: Int = 510

        /**
         * The constant TOTAL_BLOCK_LEN.
         */
        const val TOTAL_BLOCK_LEN: Int = 520
        private val tempBuffer: ByteBuffer = ByteBuffer.allocateDirect(520)

        private fun getMediumInt(buffer: ByteBuffer): Int =
            (buffer.get().toInt() and 255) shl 16 or ((buffer.get().toInt() and 255) shl 8) or (
                    buffer
                        .get()
                        .toInt() and 255
                    )

        private fun putMediumInt(
            buffer: ByteBuffer,
            `val`: Int,
        ) {
            buffer.put((`val` shr 16).toByte())
            buffer.put((`val` shr 8).toByte())
            buffer.put(`val`.toByte())
        }
    }
}
