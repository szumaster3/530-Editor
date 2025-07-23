package com.misc.transfer

import com.displee.cache.CacheLibrary
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.*

class RegionTransfer(
    private val cacheTo: String,
) : JFrame() {

    private val regionID = JTextField(10)
    private val symmetricKeyField = JTextField(20)
    private val sourceCache = JTextField(20).apply { isEditable = false }
    private val regionOutput = JTextField(20).apply { isEditable = false }
    private val regionInput = JTextField(10)
    private val editButton = JButton("Transfer Region")
    private val selectSourceCache = JButton("Select Source Cache")
    private val backupButton = JButton("Backup Cache")
    private val calculateRegionButton = JButton("Calculate Region")
    private val calculateRegionIdButton = JButton("Calculate Region ID")

    init {
        title = "Region Editor"
        defaultCloseOperation = 2
        setSize(450, 400)
        setLocationRelativeTo(null)
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = Insets(6, 8, 6, 8)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        val regionPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Region Info")
        }

        val rGbc = GridBagConstraints().apply {
            insets = Insets(4, 6, 4, 6)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        regionPanel.add(JLabel("Region ID:"), rGbc.apply { gridx = 0; gridy = 0; weightx = 0.0 })
        regionPanel.add(regionID, rGbc.apply { gridx = 1; gridy = 0 })

        regionPanel.add(JLabel("XTEA:"), rGbc.apply { gridx = 0; gridy = 1; weightx = 0.0 })
        regionPanel.add(symmetricKeyField, rGbc.apply { gridx = 1; gridy = 1 })

        regionPanel.add(JLabel("Enter Region (lX_Y):"), rGbc.apply { gridx = 0; gridy = 2; weightx = 0.0 })
        regionPanel.add(regionInput, rGbc.apply { gridx = 1; gridy = 2 })

        regionPanel.add(JLabel("Region Output:"), rGbc.apply { gridx = 0; gridy = 3; weightx = 0.0 })
        regionPanel.add(regionOutput, rGbc.apply { gridx = 1; gridy = 3 })

        regionPanel.add(calculateRegionButton, rGbc.apply { gridx = 1; gridy = 4 })
        regionPanel.add(calculateRegionIdButton, rGbc.apply { gridx = 1; gridy = 5 })

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        add(regionPanel, gbc)

        val cachePanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Cache Path")
        }

        cachePanel.add(JLabel("Cache From Path:"), rGbc.apply { gridx = 0; gridy = 0; weightx = 0.0 })
        cachePanel.add(sourceCache, rGbc.apply { gridx = 1; gridy = 0 })
        cachePanel.add(selectSourceCache, rGbc.apply { gridx = 1; gridy = 1 })

        gbc.gridy = 1
        add(cachePanel, gbc)

        val actionPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Actions")
        }

        editButton.preferredSize = selectSourceCache.preferredSize
        backupButton.preferredSize = selectSourceCache.preferredSize

        actionPanel.add(editButton, rGbc.apply { gridx = 0; gridy = 0; weightx = 1.0 })
        actionPanel.add(backupButton, rGbc.apply { gridx = 1; gridy = 0; weightx = 1.0 })

        gbc.gridy = 2
        add(actionPanel, gbc)

        selectSourceCache.addActionListener { selectSourceCachePath() }
        editButton.addActionListener { transferRegion() }
        backupButton.addActionListener { backupCache() }
        calculateRegionButton.addActionListener { calculateRegion() }
        calculateRegionIdButton.addActionListener { calculateRegionId() }

        isVisible = true
    }

    private fun transferRegion() {
        val regionId = regionID.text.trim().toIntOrNull()
        val xtea =
            symmetricKeyField.text
                .trim()
                .split(",")
                .mapNotNull { it.toIntOrNull() }
                .toIntArray()

        if (regionId == null || sourceCache.text.isEmpty()) {
            showError("Invalid Region ID or cache Path!")
            return
        }

        val cacheFrom = CacheLibrary.create(sourceCache.text)
        val cacheDest = CacheLibrary.create(cacheTo)
        val x = (regionId shr 8) and 0xFF
        val y = regionId and 0xFF
        val regionFileName = "l${x}_$y"

        val regionData = cacheFrom.data(5, regionFileName, xtea)
        val indexTo = cacheDest.index(5)

        if (regionData != null) {
            cacheDest.put(5, regionFileName, regionData, xtea)
            indexTo.update()
            JOptionPane.showMessageDialog(
                this,
                "Region $regionId transferred successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            showError("Region $regionId not found in source cache.")
        }
    }

    private fun backupCache() {
        val cachePath = if (sourceCache.text.isNotEmpty()) sourceCache.text else cacheTo
        val cacheDirectory = File(cachePath)
        if (!cacheDirectory.exists() || !cacheDirectory.isDirectory) {
            showError("Invalid source cache path.")
            return
        }
        val zipFilePath = "$cachePath/cache_backup.zip"
        try {
            FileOutputStream(zipFilePath).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    zipDirectory(cacheDirectory, cacheDirectory, zos)
                }
            }
            JOptionPane.showMessageDialog(
                this,
                "Backup created: $zipFilePath",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            )
        } catch (e: IOException) {
            showError("An error occurred while backing up the cache.")
        }
    }

    private fun zipDirectory(
        folder: File,
        baseFolder: File,
        zos: ZipOutputStream,
    ) {
        folder.listFiles()?.forEach { file ->
            if (file.name.endsWith(".zip")) return@forEach
            val entryName = file.relativeTo(baseFolder).path.replace("\\", "/")
            if (file.isDirectory) {
                zipDirectory(file, baseFolder, zos)
            } else {
                zos.putNextEntry(ZipEntry(entryName))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    private fun calculateRegion() {
        val id = regionID.text.trim().toIntOrNull()
        if (id != null) {
            val x = (id shr 8) and 0xFF
            val y = id and 0xFF
            regionOutput.text = "l${x}_$y"
        } else {
            showError("Invalid Region ID!")
        }
    }

    private fun calculateRegionId() {
        val text = regionInput.text.trim()
        if (text.startsWith("l") && "_" in text) {
            val parts = text.substring(1).split("_")
            if (parts.size == 2) {
                val x = parts[0].toInt()
                val y = parts[1].toInt()
                regionID.text = ((x shl 8) or y).toString()
            } else {
                showError("Invalid Region Format (lX_Y)!")
            }
        } else {
            showError("Invalid Region Format (lX_Y)!")
        }
    }

    private fun selectSourceCachePath() {
        val fileChooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sourceCache.text = fileChooser.selectedFile.absolutePath
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}
