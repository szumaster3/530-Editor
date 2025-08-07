package com.tools

import com.displee.cache.CacheLibrary
import com.displee.cache.index.archive.Archive
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class InterfaceTransfer(cachePath: String) : JFrame() {

    private val cacheLibrary = CacheLibrary.create(cachePath)
    private var archiveListModel = DefaultListModel<String>()
    private var archiveList = JList(archiveListModel)
    private var idField = JTextField(10)
    private var sourceCachePath = ""

    init {
        title = "Interface Manager"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(600, 400)
        setLocationRelativeTo(null)
        layout = BorderLayout(10, 10)

        val leftPanel = JPanel(BorderLayout())
        val listTitle = JLabel("Available Interfaces:")
        archiveList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        archiveList.visibleRowCount = 15
        archiveList.addListSelectionListener {
            val selected = archiveList.selectedIndex
            if (selected != -1) {
                idField.text = (selected + 1).toString()
            }
        }

        leftPanel.add(listTitle, BorderLayout.NORTH)
        leftPanel.add(JScrollPane(archiveList), BorderLayout.CENTER)

        val rightPanel = JPanel()
        rightPanel.layout = BoxLayout(rightPanel, BoxLayout.Y_AXIS)

        val idPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        idPanel.add(JLabel("ID:"))
        idPanel.add(idField)

        val buttonPanel = JPanel(GridLayout(6, 1, 5, 5))
        buttonPanel.border = BorderFactory.createTitledBorder("Actions")

        val removeBtn = JButton("Remove Archive").apply { addActionListener { removeArchive() } }
        val transferBtn = JButton("Transfer Archive").apply { addActionListener { transferArchive() } }
        val importBtn = JButton("Import Archive (.dat)").apply { addActionListener { importArchive() } }
        val exportBtn = JButton("Export Archive (.dat)").apply { addActionListener { exportArchive() } }
        val selectSourceBtn = JButton("Select Source Cache").apply { addActionListener { selectSourceCache() } }

        arrayOf(removeBtn, transferBtn, importBtn, exportBtn, selectSourceBtn).forEach { buttonPanel.add(it) }

        rightPanel.add(idPanel)
        rightPanel.add(Box.createRigidArea(Dimension(0, 10)))
        rightPanel.add(buttonPanel)

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel)
        splitPane.resizeWeight = 0.5

        add(splitPane, BorderLayout.CENTER)

        isVisible = true
        loadInterfaces()
    }

    private fun loadInterfaces() {
        archiveListModel.clear()
        try {
            val interfaces = cacheLibrary.index(3).archives()
            var countNonNull = 0
            var countNull = 0

            for (i in interfaces.indices) {
                if (interfaces[i] != null) {
                    archiveListModel.addElement("Interface ${i + 1}")
                    countNonNull++
                } else {
                    archiveListModel.addElement("Interface ${i + 1} (empty)")
                    countNull++
                }
            }

            println("Total archive slots: ${interfaces.size}")
            println("Non-null archives: $countNonNull")
            println("Null (empty) archives: $countNull")
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Error loading interfaces: ${e.message}")
        }
    }

    private fun removeArchive() {
        try {
            val archiveId = idField.text.toInt() - 1
            cacheLibrary.index(3).remove(archiveId)
            cacheLibrary.index(3).update()
            JOptionPane.showMessageDialog(this, "Removed archive ID: ${archiveId + 1}")
            loadInterfaces()
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this, "Invalid ID.")
        }
    }

    private fun transferArchive() {
        if (sourceCachePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select source cache first.")
            return
        }

        try {
            val archiveId = idField.text.toInt() - 1
            val source = CacheLibrary.create(sourceCachePath)
            val archive = source.index(3).archive(archiveId)

            if (archive == null) {
                JOptionPane.showMessageDialog(this, "Archive not found in source cache.")
                return
            }

            val newIdStr = JOptionPane.showInputDialog(this, "New archive ID:", (archiveId + 1).toString())
            val newId = newIdStr?.toIntOrNull()?.minus(1) ?: archiveId

            cacheLibrary.index(3).add(archive, newId, null, true)
            cacheLibrary.index(3).update()
            JOptionPane.showMessageDialog(this, "Transferred archive to ID: ${newId + 1}")
            loadInterfaces()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Transfer error: ${e.message}")
        }
    }

    private fun importArchive() {
        val chooser = JFileChooser().apply {
            dialogTitle = "Import Archive (.dat)"
            fileFilter = FileNameExtensionFilter("DAT Files", "dat")
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            try {
                val index = cacheLibrary.index(3)
                val inputIdStr = JOptionPane.showInputDialog(
                    this,
                    "Enter the archive ID to import into.\nIf left blank, a new archive will be created."
                )

                val archiveId = inputIdStr?.toIntOrNull()?.minus(1) ?: index.nextId()
                val data = file.readBytes()

                val existingArchive = index.archive(archiveId)

                if (existingArchive != null) {
                    existingArchive.clear()
                    existingArchive.add(data)
                    JOptionPane.showMessageDialog(this, "Overwritten archive ID ${archiveId + 1}")
                } else {
                    val newArchive = Archive(archiveId)
                    newArchive.add(data)
                    index.add(newArchive)
                    JOptionPane.showMessageDialog(this, "Added new archive ID ${archiveId + 1}")
                }

                index.update()
                loadInterfaces()

            } catch (e: Exception) {
                JOptionPane.showMessageDialog(this, "Import error: ${e.message}")
            }
        }
    }

    private fun exportArchive() {
        try {
            val archiveId = idField.text.toInt() - 1
            val archive = cacheLibrary.index(3).archive(archiveId)

            if (archive == null) {
                JOptionPane.showMessageDialog(this, "Archive not found.")
                return
            }

            val chooser = JFileChooser().apply {
                dialogTitle = "Export Archive (.dat)"
                selectedFile = File("interface_${archiveId + 1}.dat")
            }

            val result = chooser.showSaveDialog(this)
            if (result == JFileChooser.APPROVE_OPTION) {
                chooser.selectedFile.writeBytes(archive.write())
                JOptionPane.showMessageDialog(this, "Exported to: ${chooser.selectedFile.absolutePath}")
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Export failed: ${e.message}")
        }
    }

    private fun selectSourceCache() {
        val chooser = JFileChooser().apply {
            dialogTitle = "Select Source Cache Folder"
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sourceCachePath = chooser.selectedFile.absolutePath
            JOptionPane.showMessageDialog(this, "Source cache set: $sourceCachePath")
        }
    }
}
