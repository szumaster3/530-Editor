package com.misc

import com.displee.cache.CacheLibrary
import com.displee.cache.index.Index
import com.displee.cache.index.archive.Archive
import core.cache.CacheIndex
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.io.File
import java.nio.file.Files
import javax.swing.*
import com.misc.util.HexAsciiEditor
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import javax.swing.tree.*

class FileManager(private val library: CacheLibrary) : JFrame() {

    private var selectedIndex: Int? = null
    private var selectedArchive: Int? = null
    private var selectedFile: Int? = null

    private var currentIndex: Index? = null
    private var currentArchive: Archive? = null
    private var currentFile: ByteArray? = null

    private val rootNode = DefaultMutableTreeNode("Root")
    private val treeModel = DefaultTreeModel(rootNode)
    private val tree = JTree(treeModel)

    private val infoLabel = JLabel("<html>Name: <br>Archives: <br>Revision: <br>CRC:<br>Compression:</html>")
    private val actionResultPanel = JPanel(BorderLayout())

    init {
        title = "File Manager"
        layout = BorderLayout()

        val mainPanel = JPanel(BorderLayout())
        val treePanel = createTreePanel()

        val actionPanel = JPanel(GridLayout(3, 3, 5, 5))

        val centerPanel = JPanel(BorderLayout())
        centerPanel.add(treePanel, BorderLayout.CENTER)
        centerPanel.add(informationPanel(), BorderLayout.SOUTH)

        mainPanel.add(centerPanel, BorderLayout.CENTER)
        mainPanel.add(actionPanel, BorderLayout.SOUTH)

        actionPanel.apply {
            border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
            add(createAddArchiveButton())
            add(createRemoveArchiveButton())
            add(createAddFileButton())
            add(createRemoveFileButton())
            add(createEditFileButton())
            add(createExportIndexButton())
            add(createRenameArchiveButton())
            add(createExportWholeIndexButton())
        }

        add(mainPanel)
        setSize(700, 600)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun createTreePanel(): JScrollPane {
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        tree.cellRenderer = object : DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean,
                leaf: Boolean, row: Int, hasFocus: Boolean,
            ): java.awt.Component {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
                val node = value as? DefaultMutableTreeNode
                val text = node?.userObject?.toString() ?: ""
                icon = when {
                    text.startsWith("Index") -> UIManager.getIcon("FileView.directoryIcon")
                    text.startsWith("Archive") -> UIManager.getIcon("FileView.computerIcon")
                    text.startsWith("File") -> UIManager.getIcon("FileView.fileIcon")
                    else -> null
                }
                return this
            }
        }

        populateTree(rootNode)
        tree.model = DefaultTreeModel(rootNode)
        tree.expandRow(0)

        tree.addTreeSelectionListener { event ->
            val selectedNode = event.path.lastPathComponent
            if (selectedNode !is DefaultMutableTreeNode) {
                clearSelection()
                return@addTreeSelectionListener
            }
            val nodeData = selectedNode.userObject.toString()

            when {
                nodeData.startsWith("Index") -> {
                    selectedIndex = nodeData.substringAfter("Index ").toIntOrNull()
                    selectedArchive = null
                    selectedFile = null
                    currentIndex = selectedIndex?.let { library.index(it) }
                    currentArchive = null
                    currentFile = null
                }

                nodeData.startsWith("Archive") -> {
                    selectedArchive = nodeData.substringAfter("Archive ").toIntOrNull()
                    selectedFile = null
                    currentIndex?.let { currentArchive = selectedArchive?.let { id -> it.archive(id) } }
                    currentFile = null
                }

                nodeData.startsWith("File") -> {
                    selectedFile = nodeData.substringAfter("File ").toIntOrNull()
                    currentFile = selectedFile?.let { id -> currentArchive?.file(id)?.data }
                }

                else -> {
                    clearSelection()
                }
            }

            detailsPanel()
            updateActionInfoLabel("Selected: $nodeData")
        }

        val scroll = JScrollPane(tree)
        scroll.preferredSize = Dimension(300, 400)
        return scroll
    }

    private fun clearSelection() {
        selectedIndex = null
        selectedArchive = null
        selectedFile = null
        currentIndex = null
        currentArchive = null
        currentFile = null
    }

    private fun updateActionInfoLabel(text: String) {
        SwingUtilities.invokeLater {
            actionResultPanel.removeAll()
            val label = createActionResultLabel()
            label.text = text
            actionResultPanel.add(label, BorderLayout.PAGE_END)
            actionResultPanel.revalidate()
            actionResultPanel.repaint()
        }
    }

    private fun createActionResultLabel(): JLabel {
        val label = JLabel("No action performed.")
        label.alignmentX = JLabel.CENTER_ALIGNMENT
        label.alignmentY = JLabel.CENTER_ALIGNMENT

        label.border = BorderFactory.createLineBorder(java.awt.Color.BLACK, 1)
        label.background = java.awt.Color.BLACK
        label.foreground = java.awt.Color.WHITE
        label.isOpaque = true
        return label
    }

    private fun informationPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(5, 7, 5, 0)
        panel.add(infoLabel, BorderLayout.CENTER)
        panel.add(actionResultPanel, BorderLayout.SOUTH)
        return panel
    }

    private fun populateTree(rootNode: DefaultMutableTreeNode) {
        val expandedPaths = getExpandedPaths()
        val selectedPath = tree.selectionPath

        rootNode.removeAllChildren()
        val indices = library.indices()

        indices.forEach { index ->
            val indexNode = DefaultMutableTreeNode("Index ${index.id}")
            rootNode.add(indexNode)

            val archives = index.archives()
            archives.forEach { archive ->
                val archiveNode = DefaultMutableTreeNode("Archive ${archive.id}")
                indexNode.add(archiveNode)

                archive.files.forEach { (fileId, _) ->
                    val fileNode = DefaultMutableTreeNode("File $fileId")
                    archiveNode.add(fileNode)
                }
            }
        }
        (tree.model as DefaultTreeModel).reload()

        restoreExpandedPaths(expandedPaths)
        if (selectedPath != null) {
            tree.selectionPath = selectedPath
        }
    }

    private fun detailsPanel() {
        val cacheIndexName = CacheIndex.values().find { it.id == selectedIndex }
        val indexName = cacheIndexName?.name?.lowercase()?.split('_')?.joinToString(" ") {
            it.replaceFirstChar { char -> char.uppercase() }
        } ?: "None"
        val archivesCount = currentIndex?.archives()?.size ?: "None"
        val revision = currentIndex?.revision ?: "None"
        val crc = currentIndex?.crc ?: "None"
        val compression = currentIndex?.compressionType ?: "None"

        infoLabel.text =
            "<html>Name: $indexName <br>Archives: $archivesCount <br>Revision: $revision <br>CRC: $crc<br>Compression: $compression</html>"
    }

    private fun createAddArchiveButton(): JButton = JButton("Add archive").apply {
        addActionListener { addArchive() }
        size = Dimension(100, 25)
    }

    private fun createRemoveArchiveButton(): JButton = JButton("Remove archive").apply {
        addActionListener { removeArchive() }
        size = Dimension(100, 25)
    }

    private fun createAddFileButton(): JButton = JButton("Add file").apply {
        addActionListener { addFile() }
        size = Dimension(100, 25)
    }

    private fun createRemoveFileButton(): JButton = JButton("Remove file").apply {
        addActionListener { removeFile() }
        size = Dimension(100, 25)
    }

    private fun createEditFileButton(): JButton = JButton("Edit").apply {
        addActionListener { editFile() }
        size = Dimension(100, 25)
    }

    private fun createExportIndexButton(): JButton = JButton("Export index").apply {
        addActionListener { exportIndex() }
        size = Dimension(100, 25)
    }

    private fun createRenameArchiveButton(): JButton = JButton("Rename archive").apply {
        addActionListener { renameArchive() }
        size = Dimension(120, 25)
    }

    private fun createExportWholeIndexButton(): JButton = JButton("Export whole index to folder").apply {
        addActionListener { exportWholeIndex() }
        size = Dimension(180, 25)
    }

    private fun findNode(
        parent: DefaultMutableTreeNode,
        nodeName: String,
    ): DefaultMutableTreeNode? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i) as DefaultMutableTreeNode
            if (child.userObject == nodeName) {
                return child
            }
        }
        return null
    }

    private fun getExpandedPaths(): List<TreePath> {
        val expandedPaths = mutableListOf<TreePath>()
        val numRows = tree.rowCount
        for (i in 0 until numRows) {
            val path = tree.getPathForRow(i)
            if (tree.isExpanded(path)) {
                expandedPaths.add(path)
            }
        }
        return expandedPaths
    }

    private fun restoreExpandedPaths(expandedPaths: List<TreePath>) {
        expandedPaths.forEach { path -> tree.expandPath(path) }
    }

    private fun createHexEditorPanel(
        fileData: ByteArray,
        onSave: (ByteArray) -> Unit,
    ): JPanel {
        return HexAsciiEditor(fileData, onSave)
    }

    private fun editFile() {
        selectedIndex?.let { indexId ->
            selectedArchive?.let { archiveId ->
                selectedFile?.let { fileId ->
                    val archive = library.index(indexId).archive(archiveId)
                    val fileData = archive?.file(fileId)?.data

                    if (fileData != null) {
                        val hexEditorPanel = createHexEditorPanel(fileData) { newData ->
                            runCatching {
                                archive.add(fileId, newData)
                                library.index(indexId).update()
                            }.onSuccess {
                                updateActionInfoLabel("Saved changes to File $fileId in Archive $archiveId.")
                                currentFile = newData
                                SwingUtilities.invokeLater { refreshTree() }
                            }.onFailure {
                                JOptionPane.showMessageDialog(
                                    null, "Failed to save: ${it.message}", "Error", JOptionPane.ERROR_MESSAGE
                                )
                            }
                        }

                        val hexEditorFrame = JFrame("Hex Editor - File $fileId")
                        hexEditorFrame.layout = BorderLayout()
                        hexEditorFrame.add(hexEditorPanel, BorderLayout.CENTER)
                        hexEditorFrame.setSize(700, 600)
                        hexEditorFrame.setLocationRelativeTo(this)
                        hexEditorFrame.isVisible = true
                    } else {
                        JOptionPane.showMessageDialog(
                            this, "File data is null, cannot edit.", "Error", JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    fun gzipCompress(data: ByteArray): ByteArray {
        val baos = ByteArrayOutputStream()
        GZIPOutputStream(baos).use { it.write(data) }
        return baos.toByteArray()
    }

    private fun byteArrayToHex(byteArray: ByteArray, bytesPerLine: Int = 16): String = buildString {
        for (i in byteArray.indices) {
            append(String.format("%02X ", byteArray[i]))
            if ((i + 1) % bytesPerLine == 0) append("\n")
        }
    }

    private fun hexToByteArray(hex: String): ByteArray {
        val cleaned = hex.replace("\\s+".toRegex(), "")
        if (cleaned.length % 2 != 0) throw IllegalArgumentException("Hex string must have even length")

        val result = ByteArray(cleaned.length / 2)
        for (i in cleaned.indices step 2) {
            val byte = cleaned.substring(i, i + 2).toInt(16)
            result[i / 2] = byte.toByte()
        }
        return result
    }

    private var lastSelectionPath: TreePath? = null

    private fun saveSelectionPath() {
        lastSelectionPath = tree.selectionPath
    }

    private fun restoreSelectionPath() {
        if (lastSelectionPath != null) {
            tree.selectionPath = lastSelectionPath
        }
    }

    private fun addArchive() {
        if (selectedIndex == null) {
            updateActionInfoLabel("Select an index to add archive to.")
            return
        }

        val addEmpty = JOptionPane.showConfirmDialog(
            this, "Do you want to add an empty archive?", "Add Empty Archive", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION

        val index = library.index(selectedIndex!!)

        if (addEmpty) {
            val newArchive = index.add()
            if (newArchive != null) {
                val emptyData = gzipCompress(byteArrayOf(0))
                newArchive.add(0, emptyData)
                index.update()
            }
            if (newArchive != null) {
                updateActionInfoLabel("Added empty archive ${newArchive.id} to index $selectedIndex.")
                index.update()
                SwingUtilities.invokeLater { refreshTree() }
            } else {
                updateActionInfoLabel("Failed to add new archive to index $selectedIndex.")
            }
        } else {
            updateActionInfoLabel("Add archive cancelled.")
        }
    }

    private fun removeArchive() {
        if (selectedIndex == null || selectedArchive == null) {
            updateActionInfoLabel("Select an archive to remove.")
            return
        }
        val index = library.index(selectedIndex!!)
        val archive = index.archive(selectedArchive!!)
        archive?.let {
            index.remove(selectedArchive!!)
            val updated = index.update()
            updateActionInfoLabel(if (updated) "Removed archive $selectedArchive." else "Failed to remove archive.")
            SwingUtilities.invokeLater { refreshTree() }
        }
    }

    private fun addFile() {
        if (selectedIndex == null || selectedArchive == null) {
            updateActionInfoLabel("Select an index and an archive to add file to.")
            return
        }

        val addEmpty = JOptionPane.showConfirmDialog(
            this, "Do you want to add an empty file?", "Add Empty File", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION

        val archive = library.index(selectedIndex!!).archive(selectedArchive!!)

        if (archive == null) {
            updateActionInfoLabel("Archive $selectedArchive not found in index $selectedIndex.")
            return
        }

        if (addEmpty) {
            val emptyData = gzipCompress(byteArrayOf(0))
            archive.add(emptyData)
            library.index(selectedIndex!!).update()
            updateActionInfoLabel("Added empty file to archive $selectedArchive.")
            SwingUtilities.invokeLater { refreshTree() }
        } else {
            val fc = JFileChooser()
            val ret = fc.showOpenDialog(this)
            if (ret == JFileChooser.APPROVE_OPTION) {
                val file = fc.selectedFile
                try {
                    val data = Files.readAllBytes(file.toPath())
                    archive.add(data)
                    library.index(selectedIndex!!).update()
                    updateActionInfoLabel("Added file '${file.name}' to archive $selectedArchive.")
                    SwingUtilities.invokeLater { refreshTree() }
                } catch (ex: Exception) {
                    updateActionInfoLabel("Failed to add file: ${ex.message}")
                }
            } else {
                updateActionInfoLabel("Add file cancelled.")
            }
        }
    }

    private fun removeFile() {
        if (selectedIndex == null || selectedArchive == null || selectedFile == null) {
            updateActionInfoLabel("Select a file to remove.")
            return
        }
        library.remove(selectedIndex!!, selectedArchive!!, selectedFile!!)
        library.index(selectedIndex!!).update()
        updateActionInfoLabel("Removed file $selectedFile from archive $selectedArchive.")
        SwingUtilities.invokeLater { refreshTree() }
    }

    private fun exportIndex() {
        if (selectedIndex == null) {
            updateActionInfoLabel("Select an index to export.")
            return
        }
        val index = library.index(selectedIndex!!)
        val fc = JFileChooser()
        fc.selectedFile = java.io.File("index_${selectedIndex}.dat")
        val ret = fc.showSaveDialog(this)
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                val file = fc.selectedFile
                val data = index.write()
                file.writeBytes(data)
                updateActionInfoLabel("Exported index $selectedIndex to ${file.absolutePath}")
            } catch (ex: Exception) {
                updateActionInfoLabel("Failed to export index: ${ex.message}")
            }
        }
    }

    private fun renameArchive() {
        if (selectedIndex == null || selectedArchive == null) {
            updateActionInfoLabel("Select an archive to rename.")
            return
        }
        val newIdStr = JOptionPane.showInputDialog(this, "Enter new archive ID:")
        val newId = newIdStr?.toIntOrNull()
        if (newId == null) {
            updateActionInfoLabel("Invalid archive ID.")
            return
        }

        val index = library.index(selectedIndex!!)
        val archive = index.archive(selectedArchive!!)
        if (archive == null) {
            updateActionInfoLabel("Archive not found.")
            return
        }

        val newArchive = index.add(newId)
        archive.files.forEach { (fileId, file) ->
            val data = file.data
            if (data != null) {
                newArchive.add(fileId, data)
            } else {
                println("Warning: File $fileId data is null, skipping")
            }
        }
        index.remove(selectedArchive!!)
        val updated = index.update()
        if (updated) {
            selectedArchive = newId
            updateActionInfoLabel("Renamed archive to $newId")
            SwingUtilities.invokeLater { refreshTree() }
        } else {
            updateActionInfoLabel("Failed to rename archive")
        }
    }

    private fun refreshTree() {
        val expandedPaths = getExpandedPaths()
        saveSelectionPath()
        populateTree(rootNode)
        restoreExpandedPaths(expandedPaths)
        restoreSelectionPath()
    }

    private fun exportWholeIndex() {
        if (selectedIndex == null) {
            updateActionInfoLabel("Select an index to export.")
            return
        }
        val indexId = selectedIndex!!
        val index = library.index(indexId)

        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.dialogTitle = "Select output folder for index $indexId export"
        val ret = chooser.showSaveDialog(this)
        if (ret != JFileChooser.APPROVE_OPTION) {
            updateActionInfoLabel("Export cancelled.")
            return
        }

        val baseOutputDir = chooser.selectedFile
        val indexFolder = File(baseOutputDir, "index_$indexId")
        if (!indexFolder.exists()) {
            indexFolder.mkdirs()
        }

        try {
            index.archives().forEach { archive ->
                val archiveFolder = File(indexFolder, archive.id.toString())
                if (!archiveFolder.exists()) {
                    archiveFolder.mkdirs()
                }

                archive.files.forEach { (fileId, file) ->
                    val data = file.data
                    if (data != null) {
                        val fileOut = File(archiveFolder, fileId.toString())
                        fileOut.writeBytes(data)
                    } else {
                        println("Warning: File $fileId data is null, skipping")
                    }
                }
            }
            updateActionInfoLabel("Exported whole index $indexId to folder ${indexFolder.absolutePath}")
        } catch (ex: Exception) {
            updateActionInfoLabel("Failed to export whole index: ${ex.message}")
        }
    }
}
