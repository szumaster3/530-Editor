package com.cache.store

import java.util.*

/**
 * The type Archive reference.
 */
class ArchiveReference {
    /**
     * Gets name hash.
     *
     * @return the name hash
     */
    /**
     * Sets name hash.
     *
     * @param nameHash the name hash
     */
    @JvmField
    var nameHash: Int = 0

    /**
     * Get whirpool byte [ ].
     *
     * @return the byte [ ]
     */
    /**
     * Sets whirpool.
     *
     * @param whirpool the whirpool
     */
    @JvmField
    var whirpool: ByteArray? = null

    /**
     * Gets crc.
     *
     * @return the crc
     */
    var cRC: Int = 0
        private set

    /**
     * Gets revision.
     *
     * @return the revision
     */
    /**
     * Sets revision.
     *
     * @param revision the revision
     */
    @JvmField
    var revision: Int = 0
    /**
     * Get files file reference [ ].
     *
     * @return the file reference [ ]
     */
    /**
     * Sets files.
     *
     * @param files the files
     */
    lateinit var files: Array<FileReference?>
    /**
     * Get valid file ids int [ ].
     *
     * @return the int [ ]
     */
    /**
     * Sets valid file ids.
     *
     * @param validFileIds the valid file ids
     */
    lateinit var validFileIds: IntArray
    /**
     * Is needs files sort boolean.
     *
     * @return the boolean
     */
    /**
     * Sets needs files sort.
     *
     * @param needsFilesSort the needs files sort
     */
    var isNeedsFilesSort: Boolean = false
    private var updatedRevision = false

    /**
     * Update revision.
     */
    fun updateRevision() {
        if (!this.updatedRevision) {
            ++this.revision
            this.updatedRevision = true
        }
    }

    /**
     * Sets crc.
     *
     * @param crc the crc
     */
    fun setCrc(crc: Int) {
        this.cRC = crc
    }

    /**
     * Remove file reference.
     *
     * @param fileId the file id
     */
    fun removeFileReference(fileId: Int) {
        val newValidFileIds = IntArray(validFileIds.size - 1)
        var count = 0
        val `arr$` = this.validFileIds
        val `len$` = `arr$`.size

        for (`i$` in 0 until `len$`) {
            val id = `arr$`[`i$`]
            if (id != fileId) {
                newValidFileIds[count++] = id
            }
        }

        this.validFileIds = newValidFileIds
        files[fileId] = null
    }

    /**
     * Add empty file reference.
     *
     * @param fileId the file id
     */
    fun addEmptyFileReference(fileId: Int) {
        this.isNeedsFilesSort = true
        val newValidFileIds = validFileIds.copyOf(validFileIds.size + 1)
        newValidFileIds[newValidFileIds.size - 1] = fileId
        this.validFileIds = newValidFileIds
        if (files.size <= fileId) {
            val newFiles = files.copyOf(fileId + 1)
            newFiles[fileId] = FileReference()
            this.files = newFiles
        } else {
            files[fileId] = FileReference()
        }
    }

    /**
     * Sort files.
     */
    fun sortFiles() {
        Arrays.sort(this.validFileIds)
        this.isNeedsFilesSort = false
    }

    /**
     * Reset.
     */
    fun reset() {
        this.whirpool = null
        this.updatedRevision = true
        this.revision = 0
        this.nameHash = 0
        this.cRC = 0
        this.files = arrayOfNulls(0)
        this.validFileIds = IntArray(0)
        this.isNeedsFilesSort = false
    }

    /**
     * Copy header.
     *
     * @param fromReference the from reference
     */
    fun copyHeader(fromReference: ArchiveReference) {
        this.setCrc(fromReference.cRC)
        this.nameHash = fromReference.nameHash
        this.whirpool = fromReference.whirpool
        val validFiles = fromReference.validFileIds
        this.validFileIds = validFiles.copyOf(validFiles.size)
        val files = fromReference.files
        this.files = files.copyOf(files.size)
    }
}
