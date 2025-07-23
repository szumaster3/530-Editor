package com.cache.store

import com.cache.io.InputStream
import com.cache.io.OutputStream
import java.util.*

/**
 * The type Reference table.
 */
class ReferenceTable internal constructor(
    private val archive: Archive,
) {
    private var revision = 0

    /**
     * Is named boolean.
     *
     * @return the boolean
     */
    var isNamed: Boolean = false
        private set
    private var usesWhirpool = false

    /**
     * Get archives archive reference [ ].
     *
     * @return the archive reference [ ]
     */
    lateinit var archives: Array<ArchiveReference?>
        private set

    /**
     * Get valid archive ids int [ ].
     *
     * @return the int [ ]
     */
    lateinit var validArchiveIds: IntArray
        private set
    private var updatedRevision = false
    private var needsArchivesSort = false

    /**
     * Instantiates a new Reference table.
     *
     * @param archive the archive
     */
    init {
        this.decodeHeader()
    }

    var keys: IntArray?
        /**
         * Get keys int [ ].
         *
         * @return the int [ ]
         */
        get() = archive.keys
        /**
         * Sets keys.
         *
         * @param keys the keys
         */
        set(keys) {
            archive.keys = keys
        }

    /**
     * Sort archives.
     */
    fun sortArchives() {
        Arrays.sort(this.validArchiveIds)
        this.needsArchivesSort = false
    }

    /**
     * Add empty archive reference.
     *
     * @param archiveId the archive id
     */
    fun addEmptyArchiveReference(archiveId: Int) {
        this.needsArchivesSort = true
        val newValidArchiveIds = validArchiveIds.copyOf(validArchiveIds.size + 1)
        newValidArchiveIds[newValidArchiveIds.size - 1] = archiveId
        this.validArchiveIds = newValidArchiveIds
        val reference: ArchiveReference?
        if (archives.size <= archiveId) {
            val newArchives = archives.copyOf(archiveId + 1)
            newArchives[archiveId] = ArchiveReference()
            reference = newArchives[archiveId]
            this.archives = newArchives
        } else {
            archives[archiveId] = ArchiveReference()
            reference = archives[archiveId]
        }

        reference!!.reset()
    }

    /**
     * Sort table.
     */
    fun sortTable() {
        if (this.needsArchivesSort) {
            this.sortArchives()
        }

        for (index in validArchiveIds.indices) {
            val archive = archives[validArchiveIds[index]]
            if (archive!!.isNeedsFilesSort) {
                archive.sortFiles()
            }
        }
    }

    /**
     * Encode header object [ ].
     *
     * @param mainFile the main file
     * @return the object [ ]
     */
    fun encodeHeader(mainFile: MainFile): Array<Any>? {
        val stream = OutputStream()
        val protocol = this.protocol
        stream.writeByte(protocol)
        if (protocol >= 6) {
            stream.writeInt(this.revision)
        }

        stream.writeByte((if (this.isNamed) 1 else 0) or (if (this.usesWhirpool) 2 else 0))
        if (protocol >= 7) {
            stream.writeBigSmart(validArchiveIds.size)
        } else {
            stream.writeShort(validArchiveIds.size)
        }

        var data: Int
        var archive: Int
        data = 0
        while (data < validArchiveIds.size) {
            archive = validArchiveIds[data]
            if (data != 0) {
                archive -= validArchiveIds[data - 1]
            }

            if (protocol >= 7) {
                stream.writeBigSmart(archive)
            } else {
                stream.writeShort(archive)
            }
            ++data
        }

        if (this.isNamed) {
            data = 0
            while (data < validArchiveIds.size) {
                stream.writeInt(archives[validArchiveIds[data]]!!.nameHash)
                ++data
            }
        }

        if (this.usesWhirpool) {
            data = 0
            while (data < validArchiveIds.size) {
                stream.writeBytes(archives[validArchiveIds[data]]!!.whirpool)
                ++data
            }
        }

        data = 0
        while (data < validArchiveIds.size) {
            stream.writeInt(archives[validArchiveIds[data]]!!.cRC)
            ++data
        }

        data = 0
        while (data < validArchiveIds.size) {
            stream.writeInt(archives[validArchiveIds[data]]!!.revision)
            ++data
        }

        data = 0
        while (data < validArchiveIds.size) {
            archive = archives[validArchiveIds[data]]!!.validFileIds.size
            if (protocol >= 7) {
                stream.writeBigSmart(archive)
            } else {
                stream.writeShort(archive)
            }
            ++data
        }

        var index2: Int
        var var8: ArchiveReference?
        data = 0
        while (data < validArchiveIds.size) {
            var8 = archives[validArchiveIds[data]]

            index2 = 0
            while (index2 < var8!!.validFileIds.size) {
                var var9 = var8.validFileIds[index2]
                if (index2 != 0) {
                    var9 -= var8.validFileIds[index2 - 1]
                }

                if (protocol >= 7) {
                    stream.writeBigSmart(var9)
                } else {
                    stream.writeShort(var9)
                }
                ++index2
            }
            ++data
        }

        if (this.isNamed) {
            data = 0
            while (data < validArchiveIds.size) {
                var8 = archives[validArchiveIds[data]]

                index2 = 0
                while (index2 < var8!!.validFileIds.size) {
                    stream.writeInt(var8.files[var8.validFileIds[index2]]!!.nameHash)
                    ++index2
                }
                ++data
            }
        }

        val var91 = ByteArray(stream.offset)
        stream.offset = 0
        stream.getBytes(var91, 0, var91.size)
        return this.archive.editNoRevision(var91, mainFile)
    }

    val protocol: Int
        /**
         * Gets protocol.
         *
         * @return the protocol
         */
        get() {
            if (archives.size > '\uffff'.code) {
                return 7
            } else {
                for (index in validArchiveIds.indices) {
                    if (index > 0 && validArchiveIds[index] - validArchiveIds[index - 1] > '\uffff'.code
                    ) {
                        return 7
                    }

                    if (archives[validArchiveIds[index]]!!.validFileIds.size > '\uffff'.code) {
                        return 7
                    }
                }

                return if (this.revision == 0) 5 else 6
            }
        }

    /**
     * Update revision.
     */
    fun updateRevision() {
        if (!this.updatedRevision) {
            ++this.revision
            this.updatedRevision = true
        }
    }

    private fun decodeHeader() {
        val stream = InputStream(archive.data)
        val protocol = stream.readUnsignedByte()
        if (protocol >= 5 && protocol <= 7) {
            if (protocol >= 6) {
                this.revision = stream.readInt()
            }

            val hash = stream.readUnsignedByte()
            this.isNamed = (1 and hash) != 0
            this.usesWhirpool = (2 and hash) != 0
            val validArchivesCount = if (protocol >= 7) stream.readBigSmart() else stream.readUnsignedShort()
            this.validArchiveIds = IntArray(validArchivesCount)
            var lastArchiveId = 0
            var biggestArchiveId = 0

            var index: Int
            var archive: Int
            index = 0
            while (index < validArchivesCount) {
                lastArchiveId += if (protocol >= 7) stream.readBigSmart() else stream.readUnsignedShort()
                archive = lastArchiveId
                if (archive > biggestArchiveId) {
                    biggestArchiveId = archive
                }

                validArchiveIds[index] = archive
                ++index
            }

            this.archives = arrayOfNulls(biggestArchiveId + 1)

            index = 0
            while (index < validArchivesCount) {
                archives[validArchiveIds[index]] = ArchiveReference()
                ++index
            }

            if (this.isNamed) {
                index = 0
                while (index < validArchivesCount) {
                    archives[validArchiveIds[index]]!!.nameHash = stream.readInt()
                    ++index
                }
            }

            if (this.usesWhirpool) {
                index = 0
                while (index < validArchivesCount) {
                    val index2 = ByteArray(64)
                    stream.getBytes(index2, 0, 64)
                    archives[validArchiveIds[index]]!!.whirpool = index2
                    ++index
                }
            }

            index = 0
            while (index < validArchivesCount) {
                archives[validArchiveIds[index]]!!.setCrc(stream.readInt())
                ++index
            }

            index = 0
            while (index < validArchivesCount) {
                archives[validArchiveIds[index]]!!.revision = stream.readInt()
                ++index
            }

            index = 0
            while (index < validArchivesCount) {
                archives[validArchiveIds[index]]!!.validFileIds =
                    IntArray(if (protocol >= 7) stream.readBigSmart() else stream.readUnsignedShort())
                ++index
            }

            var var14: ArchiveReference?
            var var13: Int
            index = 0
            while (index < validArchivesCount) {
                archive = 0
                var13 = 0
                var14 = archives[validArchiveIds[index]]
                var index21 = 0
                while (index21 < var14!!.validFileIds.size) {
                    archive += if (protocol >= 7) stream.readBigSmart() else stream.readUnsignedShort()
                    val fileId = archive
                    if (fileId > var13) {
                        var13 = fileId
                    }

                    var14.validFileIds[index21] = fileId
                    ++index21
                }

                var14.files = arrayOfNulls(var13 + 1)

                index21 = 0
                while (index21 < var14.validFileIds.size) {
                    var14.files[var14.validFileIds[index21]] = FileReference()
                    ++index21
                }
                ++index
            }

            if (this.isNamed) {
                index = 0
                while (index < validArchivesCount) {
                    var14 = archives[validArchiveIds[index]]

                    var13 = 0
                    while (var13 < var14!!.validFileIds.size) {
                        var14.files[var14.validFileIds[var13]]!!.nameHash = stream.readInt()
                        ++var13
                    }
                    ++index
                }
            }
        } else {
            throw RuntimeException("INVALID PROTOCOL")
        }
    }

    /**
     * Gets revision.
     *
     * @return the revision
     */
    fun getRevision(): Int = this.revision

    /**
     * Sets revision.
     *
     * @param revision the revision
     */
    fun setRevision(revision: Int) {
        this.updatedRevision = true
        this.revision = revision
    }

    /**
     * Uses whirpool boolean.
     *
     * @return the boolean
     */
    fun usesWhirpool(): Boolean = this.usesWhirpool

    fun removeArchive(archiveId: Int) {
        val temp = arrayOfNulls<ArchiveReference>(archives.size - 1)
        val temp2 = IntArray(validArchiveIds.size - 1)
        var count = 0
        for (i in archives.indices) {
            if (i != archiveId) {
                temp[count] = archives[i]
                count++
            }
        }
        for (i in 0 until validArchiveIds.size - 1) temp2[i] = validArchiveIds[i]
        archives = temp
        validArchiveIds = temp2
    }

    val compression: Int
        /**
         * Gets compression.
         *
         * @return the compression
         */
        get() = archive.compression
}
