package com.misc

import com.displee.cache.CacheLibrary
import com.displee.compress.CompressionType
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.JFileChooser
import javax.swing.JOptionPane

object IndexManager {

    fun createIndex(cachePath: String, indexId: Int) {
        val lib = CacheLibrary.create(cachePath)
        lib.createIndex(
            CompressionType.GZIP,
            6,
            530,
            named = true,
            whirlpool = true,
            lengths = true,
            checksums = true,
            writeReferenceTable = true,
            id = indexId
        )
        JOptionPane.showMessageDialog(null, "Index $indexId was successfully created.")
    }

    fun deleteIndex(cacheLibrary: CacheLibrary, indexId: Int) {
        try {
            cacheLibrary.index(indexId).remove(indexId)
            JOptionPane.showMessageDialog(null, "Index $indexId deleted.")
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to delete index: ${ex.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    fun exportIndex(cacheLibrary: CacheLibrary, indexId: Int) {
        try {
            val index = cacheLibrary.index(indexId) ?: throw Exception("Index $indexId does not exist")
            val exportFile = JFileChooser()
            val result = exportFile.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = exportFile.selectedFile
                exportIndexToZip(index, file)
                JOptionPane.showMessageDialog(null, "Index $indexId exported to ${file.absolutePath}")
            }
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to export index: ${ex.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    fun updateIndexCRC(cacheLibrary: CacheLibrary, indexId: Int, crc: Int) {
        try {
            cacheLibrary.updateIndexCrc(indexId, crc)
            JOptionPane.showMessageDialog(null, "CRC for index $indexId updated.")
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to update CRC: ${ex.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun exportIndexToZip(index: com.displee.cache.index.Index, file: File) {
        ZipOutputStream(FileOutputStream(file)).use { zos ->
            index.archives().forEach { archive ->
                archive.files().forEach { fileEntry ->
                    val data = fileEntry.data
                    if (data != null) {
                        val entryName = "archive_${archive.id}/file_${fileEntry.id}.dat"
                        zos.putNextEntry(ZipEntry(entryName))
                        zos.write(data)
                        zos.closeEntry()
                    }
                }
            }
        }
    }

    private fun CacheLibrary.updateIndexCrc(indexId: Int, newCrc: Int) {
        val index = this.index(indexId) ?: throw Exception("Index $indexId does not exist")
        try {
            val indicesField = this::class.java.getDeclaredField("indices")
            indicesField.isAccessible = true
            val indices = indicesField.get(this) as List<com.displee.cache.index.Index>
            val idxToUpdate = indices.firstOrNull { it.id == indexId } ?: throw Exception("Index not found")
            val crcField = idxToUpdate::class.java.getDeclaredField("crc")
            crcField.isAccessible = true
            crcField.setInt(idxToUpdate, newCrc)
        } catch (e: Exception) {
            throw Exception("Failed to update CRC by reflection: ${e.message}")
        }
    }

    fun createMainFile(cachePath: String, newProtocol: Boolean = true) {}
}
