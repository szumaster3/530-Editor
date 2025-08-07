package com

import com.displee.cache.CacheLibrary
import com.tools.ColorPicker
import com.tools.RegionDumper
import com.tools.SpriteManager
import com.utils.IndexExport
import com.tools.IndexTransfer
import com.tools.InterfaceTransfer
import com.tools.RegionTransfer
import com.utils.ModelExporter
import com.utils.ModelPacker
import com.utils.model.frame.ModelFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.*

class ToolSelection : JFrame() {
    private var cache = ""
    private val loadCacheButton = JButton("Load cache")
    private val loadLastCacheButton = JButton("Last Location")
    private val selectionBox: JComboBox<String> = JComboBox()
    private val cacheFile = File("cache_location.txt")
    private val backupMessage = JLabel().apply { font = font.deriveFont(12f) }

    init {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.title = "Tool Selection"
        this.isResizable = false
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)
        initComponents()
        Main.log("Main", "ToolSelection Started")
    }

    private fun initComponents() {
        val tabbedPane = JTabbedPane()

        val panelTab = JPanel(FlowLayout(FlowLayout.LEFT))

        val selectToolLabel = JLabel("Select tool")

        val rebuildCacheButton = JButton("Rebuild cache").apply {
            preferredSize = Dimension(101, 30)
            addActionListener { rebuildCache() }
        }
        val backupCacheButton = JButton("Backup cache").apply {
            preferredSize = Dimension(101, 30)
            addActionListener { backup() }
        }

        val panelMiddle = JPanel(FlowLayout(FlowLayout.CENTER))
        panelMiddle.add(rebuildCacheButton)
        panelMiddle.add(Box.createHorizontalStrut(2))
        panelMiddle.add(backupCacheButton)

        panelTab.add(selectToolLabel)
        panelTab.add(panelMiddle)
        panelTab.add(backupMessage)

        val toolsTab = JPanel(FlowLayout())

        setupButton(loadCacheButton, Dimension(100, 30), this::loadCacheButtonHandler)
        setupButton(loadLastCacheButton, Dimension(100, 30), this::loadLastCacheButtonHandler)

        selectionBox.model = DefaultComboBoxModel(
            arrayOf(
                "Region Transfer",
                "Interface Transfer",
                "Index Transfer",
                "Export model",
                "Export Indexes",
                "Pack model",
                "Pick a Color",
                "Model Viewer",
                "Sprite Viewer",
                "Map Dumper"
            )
        )

        val submitButton = JButton("Submit").apply {
            preferredSize = Dimension(90, 22)
            addActionListener(this@ToolSelection::submitButtonActionPerformed)
        }

        val alignmentPanel1 = JPanel(FlowLayout(FlowLayout.CENTER))
        alignmentPanel1.add(loadCacheButton)
        alignmentPanel1.add(Box.createHorizontalStrut(2))
        alignmentPanel1.add(loadLastCacheButton)

        val alignmentPanel2 = JPanel(FlowLayout())
        alignmentPanel2.add(selectToolLabel)

        val alignmentPanel3 = JPanel(FlowLayout())
        alignmentPanel3.add(selectionBox)
        alignmentPanel3.add(submitButton)

        toolsTab.add(alignmentPanel1)
        toolsTab.add(alignmentPanel2)
        toolsTab.add(alignmentPanel3)

        tabbedPane.addTab("Tools", toolsTab)
        tabbedPane.addTab("Panel", panelTab)

        this.contentPane.add(tabbedPane)

        this.preferredSize = Dimension(250, 200)
        this.pack()
    }

    private fun setupButton(button: JButton, size: Dimension, action: (ActionEvent) -> Unit) {
        button.apply {
            preferredSize = size
            minimumSize = size
            maximumSize = size
            addActionListener(action)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }
    }

    private fun submitButtonActionPerformed(evt: ActionEvent) {
        val toolID = selectionBox.selectedIndex
        try {
            when (toolID) {
                0 -> RegionTransfer(cache).isVisible = true
                1 -> InterfaceTransfer(cache).isVisible = true
                2 -> IndexTransfer().isVisible = true
                3 -> ModelExporter(cache).isVisible = true
                4 -> IndexExport().isVisible = true
                5 -> ModelPacker(cache).isVisible = true
                6 -> SwingUtilities.invokeLater { ColorPicker().isVisible = true }
                7 -> SwingUtilities.invokeLater { ModelFrame(cache).isVisible = true }
                8 -> SwingUtilities.invokeLater { SpriteManager(cache).init() }
                9 -> RegionDumper(cache)
                else -> {
                    Main.log("ToolSelection", "No Tool Selected!")
                    return
                }
            }
            Main.log("ToolSelection", "Tool started.")
        } catch (e: Exception) {
            Main.log("ToolSelection", "Failed to start. Before turning on the editor you need to load the cache.")
        }
    }

    private fun loadCacheButtonHandler(evt: ActionEvent) {
        val fc = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }
        if (evt.source === loadCacheButton) {
            val returnVal = fc.showOpenDialog(this)
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                val file = fc.selectedFile
                this.cache = "${file.path}/"
                saveLastCachePath(this.cache)
            }
        }
    }

    private fun loadLastCacheButtonHandler(evt: ActionEvent) {
        if (cacheFile.exists()) {
            val lastCachePath = loadLastCachePath()
            if (lastCachePath.isNotEmpty()) {
                this.cache = lastCachePath
                Main.log("ToolSelection", "Last location: $cache")
            } else {
                JOptionPane.showMessageDialog(this, "No previous cache found.")
            }
        } else {
            JOptionPane.showMessageDialog(this, "No previous cache found.")
        }
    }

    private fun saveLastCachePath(path: String) {
        try {
            BufferedWriter(FileWriter(cacheFile)).use { writer -> writer.write(path) }
            Main.log("ToolSelection", "cache path saved.")
        } catch (e: IOException) {
            Main.log("ToolSelection", "Failed to write cache path.")
        }
    }

    private fun loadLastCachePath(): String = try {
        BufferedReader(FileReader(cacheFile)).use { reader -> reader.readText() }
    } catch (e: IOException) {
        Main.log("ToolSelection", "Failed to load cache path.")
        ""
    }

    private fun rebuildCache() {
        val response = JOptionPane.showConfirmDialog(
            null,
            "Are you sure?",
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
        )
        if (response == JOptionPane.YES_OPTION) {
            try {
                val cache = CacheLibrary.create(cache)
                cache.rebuild(File("data/cache_rebuild/"))
                JOptionPane.showMessageDialog(
                    null,
                    "Cache rebuild completed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    null,
                    "An error occurred during the cache rebuild.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun backup() {
        if (cache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cache loaded.", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }
        val backupDir = File("data/backups/").apply { if (!exists()) mkdirs() }
        val now = LocalDateTime.now()
        val dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val fileName = "cache_backup_$dateTime.zip"

        val zipFile = File(backupDir, fileName)
        try {
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                val dir = File(cache)
                zipDirectory(dir, zos, dir.name)
            }
            backupMessage.text = "Backup done"
            backupMessage.foreground = Color.darkGray

            println("Backup saved to: ${zipFile.absolutePath}")
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(
                this,
                "Error while creating backup: ${e.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun zipDirectory(directory: File, zos: ZipOutputStream, parentDirectory: String) {
        if (!directory.exists()) return

        if (directory.isDirectory) {
            val dirEntry = ZipEntry("$parentDirectory/")
            zos.putNextEntry(dirEntry)
            zos.closeEntry()
            directory.listFiles()?.forEach { file ->
                zipDirectory(file, zos, "$parentDirectory/${file.name}")
            }
        } else {
            FileInputStream(directory).use { fis ->
                val entry = ZipEntry(parentDirectory)
                zos.putNextEntry(entry)
                fis.copyTo(zos)
                zos.closeEntry()
            }
        }
    }
}
