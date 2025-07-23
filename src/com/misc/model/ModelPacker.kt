package com.misc.model

import com.cache.store.Cache
import com.cache.util.Utils
import launcher.Main
import java.awt.Desktop
import java.awt.Dimension
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import javax.swing.*
import javax.swing.GroupLayout.Alignment

class ModelPacker(
    cache: String? = null,
) : JFrame() {
    private var cache: Cache? = null
    private val modelDirField: JTextField = JTextField()
    private val sameId: JCheckBox = JCheckBox("Keep ID").apply {
        toolTipText = "Keeps same ID as named"
    }
    private val submitButton: JButton = JButton("Submit").apply {
        addActionListener { submitActionPerformed() }
    }
    private val modelDirItemButton: JButton = JButton("Select Directory").apply {
        addActionListener { modelDirActionPerformed() }
    }

    private val openLogButton: JButton = JButton("Open in .txt").apply {
        toolTipText = "Save and open the log file in a .txt format"
        isEnabled = false
        addActionListener { openLogInTextFile() }
    }

    private val exitItem: JMenuItem = JMenuItem("Exit").apply {
        addActionListener { exitActionPerformed() }
    }

    private val menuBar: JMenuBar = JMenuBar().apply {
        val fileMenu = JMenu("File").apply {
            add(exitItem)
        }
        add(fileMenu)
    }

    private val logTextArea: JTextArea = JTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        font = font.deriveFont(11f)
    }

    private val scrollPane: JScrollPane = JScrollPane(logTextArea).apply {
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        preferredSize = Dimension(300, 200)
    }

    init {
        cache?.let {
            try {
                this.cache = Cache(it)
            } catch (e: Exception) {
                Main.log("ModelPack", "Cannot find cache directory.")
            }
        }
        initComponents()
        setupFrame()
    }

    private fun initComponents() {
        val label1 = JLabel("Note: The packer is slow for large datasets.")
        val label2 = JLabel("Models Directory")
        layout = GroupLayout(contentPane).apply {
            setHorizontalGroup(
                createParallelGroup(Alignment.LEADING).addGroup(
                    createSequentialGroup().addContainerGap().addComponent(label1).addContainerGap()
                ).addGroup(
                    createSequentialGroup().addContainerGap().addComponent(label2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modelDirField, GroupLayout.DEFAULT_SIZE, 120, Int.MAX_VALUE).addContainerGap()
                ).addGroup(
                    createSequentialGroup().addContainerGap().addComponent(sameId)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(modelDirItemButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(submitButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(openLogButton)
                        .addContainerGap()
                ).addGroup(createSequentialGroup().addContainerGap().addComponent(scrollPane).addContainerGap())
            )
            setVerticalGroup(
                createSequentialGroup().addContainerGap().addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                        createParallelGroup(Alignment.BASELINE).addComponent(label2).addComponent(
                            modelDirField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                    ).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                        createParallelGroup(Alignment.BASELINE).addComponent(sameId).addComponent(
                            modelDirItemButton
                        ).addComponent(submitButton).addComponent(openLogButton)
                    ).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(scrollPane)
                    .addContainerGap(10, Int.MAX_VALUE)
            )
        }
    }

    private fun setupFrame() {
        defaultCloseOperation = 1
        title = "Model Packer"
        isResizable = false
        jMenuBar = menuBar
        setLocationRelativeTo(null)
        pack()
        isVisible = true
    }

    private fun submitActionPerformed() {
        openLogButton.isEnabled = false

        val directory = modelDirField.text
        val keepID = sameId.isSelected
        val directoryFile = File(directory)

        val files = directoryFile.walk()
            .filter { it.isFile && (it.name.endsWith(".dat") || it.name.endsWith(".gz")) }
            .toList()

        var modelsPacked = false

        files.forEach { file ->
            val fileName = file.name
            val baseFileName = fileName.substringBeforeLast(".")
            val modelId = if (keepID) {
                baseFileName.toIntOrNull() ?: -1
            } else {
                -1
            }

            if (modelId > 0) {
                Main.log("ModelPack", "Model ID for [$baseFileName] is: [$modelId]")
            }

            try {
                unpackModel(file)?.let { modelBytes ->
                    cache?.let { cache ->
                        val packedModel = if (keepID) {
                            Utils.packCustomModel(cache, modelBytes, modelId)
                        } else {
                            val archiveId = cache.indexes[7].lastArchiveId + 1
                            Utils.packCustomModel(cache, modelBytes, archiveId)
                        }

                        if (packedModel != -1) {
                            log("Model ID: [$baseFileName] -> [${if (modelId > 0) modelId else packedModel}]")
                            modelsPacked = true
                        } else {
                            log("Failed to pack model: $fileName")
                        }
                    } ?: log("Cache is not initialized.")
                } ?: log("Invalid file format for $fileName.")
            } catch (e: IOException) {
                log("There was an error packing the model: ${e.message}")
            }
        }

        if (modelsPacked) {
            openLogButton.isEnabled = true
        }
    }

    private fun log(message: String) {
        logTextArea.append("$message\n")
        logTextArea.caretPosition = logTextArea.document.length
    }

    private fun openLogInTextFile() {
        val logDir = "data/logs/"
        val logFile = File(logDir, "model_logs.txt")

        try {
            File(logDir).mkdirs()

            logFile.writeText(logTextArea.text)

            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                desktop.open(logFile)
            }

        } catch (e: IOException) {
            log("Error saving or opening the log file: ${e.message}")
        }
    }

    private fun unpackModel(file: File): ByteArray? {
        return when {
            file.name.endsWith(".gz") -> unpackGzFile(file)
            file.name.endsWith(".dat") -> Utils.getBytesFromFile(file)
            else -> null
        }
    }

    private fun unpackGzFile(file: File): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            GZIPInputStream(FileInputStream(file)).use { gzipInputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (gzipInputStream.read(buffer).also { bytesRead = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            log("Error unpacking .gz file: ${e.message}")
            return null
        }
        return byteArrayOutputStream.toByteArray()
    }

    private fun modelDirActionPerformed() {
        JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            if (showOpenDialog(this@ModelPacker) == JFileChooser.APPROVE_OPTION) {
                modelDirField.text = selectedFile.absolutePath + "/"
            }
        }
    }

    private fun exitActionPerformed() {
        dispose()
    }
}
