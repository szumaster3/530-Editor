package launcher

import com.displee.cache.CacheLibrary
import com.misc.FileManager
import com.misc.IndexManager
import com.misc.extract.IndexExport
import com.editor.item.ItemListExport
import com.editor.item.ItemSelection
import com.editor.npc.NPCListExport
import com.editor.npc.NPCSelection
import com.editor.`object`.ObjectListExport
import com.editor.`object`.ObjectSelection
import com.misc.transfer.IndexTransfer
import com.misc.transfer.InterfaceTransfer
import com.misc.transfer.RegionTransfer
import com.misc.ColorPicker
import com.misc.RegionDumper
import com.misc.model.ModelExporter
import com.misc.model.ModelPacker
import com.misc.model.view.frame.ModelFrame
import java.awt.*
import java.awt.event.ActionEvent
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.*
import javax.swing.border.EmptyBorder

class ToolSelection : JFrame() {
    private var cache = ""
    private val loadCacheButton = JButton("Load cache")
    private val loadLastCacheButton = JButton("Last Location")
    private val selectionBox: JComboBox<String> = JComboBox()
    private val cacheFile = File("cache_location.txt")
    private val toolSelected = "ToolSelection"
    private val startMessage = "Tool started."
    private val failMessage = "Failed to start. Before turning on the editor you need to load the cache."
    private val backupMessage = JLabel().apply { font = font.deriveFont(12) }

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

        val selectToolButton = JLabel("Select tool")

        val button0 = JButton("Rebuild cache").apply {
            addActionListener { rebuildCache() }
            preferredSize = Dimension(100, 30)
        }

        val button1 = JButton("Backup cache").apply {
            addActionListener { backup(this) }
            preferredSize = Dimension(100, 30)
        }

        val dumpSpritesButton =
            JButton("Dump Sprites").apply {
                preferredSize = Dimension(100, 30)
                alignmentX = Component.LEFT_ALIGNMENT
                addActionListener {
                    if (cache.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            this,
                            failMessage,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        )
                        return@addActionListener
                    }

                    val chooser =
                        JFileChooser().apply {
                            dialogTitle = "Select folder to save sprites"
                            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                            isAcceptAllFileFilterUsed = false
                        }
                    val result = chooser.showSaveDialog(this)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        val selectedDir = chooser.selectedFile
                        Thread {
                            try {
                                com.misc.SpriteDumper.dumpSprites(cache, selectedDir)
                                SwingUtilities.invokeLater {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Sprites dump completed"
                                    )
                                }
                            } catch (ex: IOException) {
                                ex.printStackTrace()
                                SwingUtilities.invokeLater {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Error dumping sprites: ${ex.message}",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                    )
                                }
                            }
                        }
                            .start()
                    }
                }
            }

        val cacheTab = JPanel()
        cacheTab.layout = BoxLayout(cacheTab, BoxLayout.Y_AXIS)

        val createIndexButton = JButton("Create Index").apply {
            addActionListener {
                val id = JOptionPane.showInputDialog(this, "Enter index ID to create:")?.toIntOrNull()
                if (id != null) {
                    try {
                        IndexManager.createIndex(cache, id)
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(this, "Failed to create index $id: ${e.message}")
                    }
                }
            }
        }

        val deleteIndexButton = JButton("Delete Index").apply {
            addActionListener {
                val id = JOptionPane.showInputDialog(this, "Enter index ID to delete:")?.toIntOrNull()
                if (id != null) {
                    try {
                        val lib = CacheLibrary.create(cache)
                        IndexManager.deleteIndex(lib, id)
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(this, "Failed to delete index $id: ${e.message}")
                    }
                }
            }
        }

        val exportIndexButton = JButton("Export Index").apply {
            addActionListener {
                val id = JOptionPane.showInputDialog(this, "Enter index ID to export:")?.toIntOrNull()
                if (id != null) {
                    try {
                        val lib = CacheLibrary.create(cache)
                        IndexManager.exportIndex(lib, id)
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(this, "Failed to export index $id: ${e.message}")
                    }
                }
            }
        }

        val editCRCButton = JButton("Edit CRC").apply {
            addActionListener {
                val id = JOptionPane.showInputDialog(this, "Enter index ID to update CRC:")?.toIntOrNull()
                val crc = JOptionPane.showInputDialog(this, "Enter new CRC:")?.toIntOrNull()
                if (id != null && crc != null) {
                    try {
                        val lib = CacheLibrary.create(cache)
                        IndexManager.updateIndexCRC(lib, id, crc)
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(this, "Failed to update CRC: ${e.message}")
                    }
                }
            }
        }

        listOf(createIndexButton, deleteIndexButton, exportIndexButton, editCRCButton).forEach {
            it.alignmentX = Component.CENTER_ALIGNMENT
            it.maximumSize = Dimension(200, 30)
            cacheTab.add(Box.createVerticalStrut(10))
            cacheTab.add(it)
        }

        val panelMiddle = JPanel(FlowLayout(FlowLayout.CENTER))
        panelMiddle.add(button0)
        panelMiddle.add(Box.createHorizontalStrut(2))
        panelMiddle.add(button1)

        val panelBottom = JPanel(FlowLayout(FlowLayout.CENTER))
        panelBottom.add(dumpSpritesButton)

        panelTab.add(selectToolButton)
        panelTab.add(panelMiddle)
        panelTab.add(panelBottom)
        panelTab.add(backupMessage)

        val toolsTab = JPanel(FlowLayout())
        val submitButton = JButton("Submit")
        val buttonSize = Dimension(100, 30)
        val submitButtonSize = Dimension(90, 22)

        setupButton(loadCacheButton, buttonSize, this::loadCacheButtonHandler)
        setupButton(loadLastCacheButton, buttonSize, this::loadLastCacheButtonHandler)

        val alignmentPanel1 = JPanel(FlowLayout(FlowLayout.CENTER))
        val alignmentPanel2 = JPanel(FlowLayout())
        val alignmentPanel3 = JPanel(FlowLayout())

        alignmentPanel1.add(loadCacheButton)
        alignmentPanel1.add(Box.createHorizontalStrut(2))
        alignmentPanel1.add(loadLastCacheButton)
        alignmentPanel2.add(selectToolButton)

        selectionBox.model = DefaultComboBoxModel(
            arrayOf(
                "Item Editor",
                "NPC Editor",
                "Object Editor",
                "Region Transfer",
                "Interface Transfer",
                "Index Transfer",
                "Export model",
                "Export definition lists",
                "Export Indexes",
                "Pack model",
                "Pick a Color",
                "File Manager",
                "Model Viewer",
                "Map Dumper"
            ),
        )

        setupButton(submitButton, submitButtonSize, this::submitButtonActionPerformed)
        alignmentPanel3.add(selectionBox)
        alignmentPanel3.add(submitButton)

        toolsTab.add(alignmentPanel1)
        toolsTab.add(alignmentPanel2)
        toolsTab.add(alignmentPanel3)

        tabbedPane.addTab("Tools", toolsTab)
        tabbedPane.addTab("Panel", panelTab)
        tabbedPane.addTab("Cache", cacheTab)
        tabbedPane.setEnabledAt(2, true)
        createIndexButton.isEnabled = true

        this.contentPane.add(tabbedPane)

        this.preferredSize = Dimension(250, 200)
        this.pack()
    }

    private fun setupButton(
        button: JButton,
        size: Dimension,
        action: (ActionEvent) -> Unit,
    ) {
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
        when (toolID) {
            0 -> try {
                ItemSelection(cache).isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }

            1 -> try {
                NPCSelection(cache).isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }

            2 -> try {
                ObjectSelection(cache).isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }

            3 -> try {
                RegionTransfer(cache).isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }
            4 -> {
                InterfaceTransfer(cache).isVisible = true; Main.log(toolSelected, startMessage)
            }

            5 -> try {
                IndexTransfer().isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }

            6 -> try {
                ModelExporter(cache).isVisible = true; Main.log(toolSelected, startMessage)
            } catch (e: IOException) {
                Main.log(toolSelected, failMessage)
            }

            7 ->
                try {
                    val lib = CacheLibrary.create(cache)
                    exportDefinitionList(lib)
                    Main.log(toolSelected, startMessage)
                } catch (e: IOException) {
                    Main.log(toolSelected, failMessage)
                }
            8 ->
                try {
                    IndexExport().isVisible = true
                    Main.log(toolSelected, startMessage)
                } catch (e: IOException) {
                    Main.log(toolSelected, failMessage)
                }
            9 ->
                try {
                    ModelPacker(cache).isVisible = true
                    Main.log(toolSelected, startMessage)
                } catch (e: IOException) {
                    Main.log(toolSelected, failMessage)
                }
            10 ->
                try {
                    SwingUtilities.invokeLater { ColorPicker().isVisible = true }
                    Main.log(toolSelected, startMessage)
                } catch (e: IOException) {
                    Main.log(toolSelected, failMessage)
                }
            11 ->
                try {
                    val lib = CacheLibrary.create(cache)
                    FileManager(lib).isVisible = true
                    Main.log(toolSelected, startMessage)
                } catch (e: Exception) {
                    Main.log(toolSelected, failMessage)
                }
            12 ->
                try {
                    SwingUtilities.invokeLater { ModelFrame(cache).isVisible = true }
                    Main.log(toolSelected, startMessage)
                } catch (e: Exception) {
                    Main.log(toolSelected, failMessage)
                }
            13 -> try {
                RegionDumper(cache)
                Main.log(toolSelected, "Map Dumper started.")
            } catch (e: Exception) {
                Main.log(toolSelected, failMessage)
            }

            else -> Main.log(toolSelected, "No Tool Selected!")
        }
    }

    private fun loadCacheButtonHandler(evt: ActionEvent) {
        val fc = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }
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

    private fun backup(button: JButton) {
        if (cache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cache loaded.", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }
        val backupDir = File("data/backups/").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        val zipFile = File(
            backupDir,
            "cache_backup_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.zip"
        )

        try {
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                val dir = File(cache)
                zipDirectory(dir, zos, dir.name)
            }
            backupMessage.text = "                          Backup done"
            backupMessage.foreground = Color.darkGray
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

    private fun exportDefinitionList(cache: CacheLibrary) {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(5, 5, 5, 5)
        }

        val npcButton = JButton("NPC Export").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(70, 20)
            addActionListener {
                try {
                    NPCListExport(cache.path)
                    Main.log("NPC Export", "Export complete.")
                } catch (e: IOException) {
                    Main.log("NPC Export", "NPC export failed.")
                }
            }
        }

        val itemButton = JButton("Item Export").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(70, 20)
            addActionListener {
                try {
                    ItemListExport(cache.path)
                    Main.log("Item Export", "Export complete.")
                } catch (e: IOException) {
                    Main.log("Item Export", "Item export failed.")
                }
            }
        }

        val objectButton = JButton("Object Export").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(70, 20)
            addActionListener {
                try {
                    ObjectListExport(cache.path)
                    Main.log("Object Export", "Export complete.")
                } catch (e: IOException) {
                    Main.log("Object Export", "Object export failed.")
                }
            }
        }

        panel.add(npcButton)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(itemButton)
        panel.add(Box.createRigidArea(Dimension(0, 10)))
        panel.add(objectButton)

        val frame = JFrame("Export Lists").apply {
            defaultCloseOperation = DISPOSE_ON_CLOSE
            contentPane = panel
            preferredSize = Dimension(140, 170)
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
    }
}
