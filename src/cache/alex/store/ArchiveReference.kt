package cache.alex.store

import java.util.*


class ArchiveReference {
    
    
    @JvmField
    var nameHash: Int = 0

    
    
    @JvmField
    var whirpool: ByteArray? = null

    
    var cRC: Int = 0
        private set

    
    
    @JvmField
    var revision: Int = 0
    
    
    lateinit var files: Array<FileReference?>
    
    
    lateinit var validFileIds: IntArray
    
    
    var isNeedsFilesSort: Boolean = false
    private var updatedRevision = false

    
    fun updateRevision() {
        if (!this.updatedRevision) {
            ++this.revision
            this.updatedRevision = true
        }
    }

    
    fun setCrc(crc: Int) {
        this.cRC = crc
    }

    
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

    
    fun sortFiles() {
        Arrays.sort(this.validFileIds)
        this.isNeedsFilesSort = false
    }

    
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
