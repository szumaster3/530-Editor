package com.misc.transfer

import com.displee.cache.CacheLibrary
import com.displee.cache.index.archive.Archive
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

/**
 * The type Interface editor.
 */
class InterfaceTransfer(
    cacheLibrary: String,
) : JFrame() {
    private val cacheLibrary = CacheLibrary.create(cacheLibrary)
    private var archiveBox: JComboBox<String>? = null
    private var idField: JTextField? = null
    private var addArchiveButton: JButton? = null
    private var removeArchiveButton: JButton? = null
    private var transferArchiveButton: JButton? = null
    private var selectSourceCacheButton: JButton? = null
    private var sourceCachePath = ""

    init {
        initComponents()
    }

    private fun initComponents() {
        this.title = "Interface Editor"
        this.defaultCloseOperation = 1
        this.layout = BorderLayout()
        this.setSize(450, 150)

        val topPanel = JPanel()
        topPanel.layout = FlowLayout(FlowLayout.LEFT)
        val idLabel = JLabel("ID:")
        idField = JTextField(10)
        topPanel.add(idLabel)
        topPanel.add(idField)

        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 10)
        addArchiveButton = JButton("Add Archive")
        addArchiveButton!!.addActionListener { addArchive() }

        removeArchiveButton = JButton("Remove Archive")
        removeArchiveButton!!.addActionListener { removeArchive() }

        transferArchiveButton = JButton("Transfer Archive")
        transferArchiveButton!!.addActionListener { transferArchive() }

        buttonPanel.add(addArchiveButton)
        buttonPanel.add(removeArchiveButton)
        buttonPanel.add(transferArchiveButton)

        val bottomPanel = JPanel()
        bottomPanel.layout = FlowLayout(FlowLayout.LEFT)
        val selectArchiveLabel = JLabel("Select Interface:")
        archiveBox = JComboBox()
        archiveBox!!.addItem("Select Interface...")
        selectSourceCacheButton = JButton("Select Source cache")
        selectSourceCacheButton!!.addActionListener { selectSourceCache() }

        bottomPanel.add(selectArchiveLabel)
        bottomPanel.add(archiveBox)
        bottomPanel.add(selectSourceCacheButton)

        this.add(topPanel, BorderLayout.NORTH)
        this.add(buttonPanel, BorderLayout.CENTER)
        this.add(bottomPanel, BorderLayout.SOUTH)

        this.setLocationRelativeTo(null)
        this.isVisible = true

        loadInterfaces()
    }

    private fun loadInterfaces() {
        archiveBox!!.removeAllItems()
        archiveBox!!.addItem("Select Interface...")

        try {
            val interfaces = cacheLibrary.index(3).archives()
            println("Total interfaces found: ${interfaces.size}")

            if (interfaces != null && interfaces.isNotEmpty()) {
                for (i in interfaces.indices) {
                    archiveBox!!.addItem("Interface $i")
                }
            } else {
                JOptionPane.showMessageDialog(this, "No interfaces found in the selected cache.")
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Error loading interfaces: " + e.message)
        }
    }

    private fun addArchive() {
        try {
            val archiveId = idField!!.text.toInt()
            val newArchive = Archive(archiveId)
            cacheLibrary.index(3).add(newArchive)
            cacheLibrary.index(3).update()
            JOptionPane.showMessageDialog(this, "Archive added with ID: $archiveId")
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this, "Invalid Archive ID.")
        }
    }

    private fun removeArchive() {
        try {
            val archiveId = idField!!.text.toInt()
            val archiveToRemove = cacheLibrary.index(3).archive(archiveId)
            if (archiveToRemove != null) {
                cacheLibrary.index(3).remove(archiveId)
                cacheLibrary.index(3).update()
                JOptionPane.showMessageDialog(this, "Archive removed with ID: $archiveId")
            } else {
                JOptionPane.showMessageDialog(this, "Archive not found with ID: $archiveId")
            }
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this, "Invalid Archive ID.")
        }
    }

    private fun transferArchive() {
        if (sourceCachePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a source cache first.")
            return
        }

        try {
            val archiveId = idField!!.text.toInt()
            val sourceCache: CacheLibrary = CacheLibrary.create(sourceCachePath)

            val archiveToCopy = sourceCache.index(3).archive(archiveId)

            if (archiveToCopy == null) {
                JOptionPane.showMessageDialog(this, "Archive not found in source cache.")
                return
            }
            val newIdString = JOptionPane.showInputDialog(this, "Enter new ID for the archive:")
            val newId = if (newIdString != null && !newIdString.isEmpty()) newIdString.toInt() else archiveId

            val replace = true
            cacheLibrary.index(3).add(archiveToCopy, newId, null, replace)
            cacheLibrary.index(3).update()

            JOptionPane.showMessageDialog(this, "Archive transferred successfully with new ID: $newId")
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(this, "Invalid Archive ID or New ID.")
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(this, "Error during transfer: " + e.message)
        }
    }

    private fun selectSourceCache() {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Select Source cache Folder"
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = fileChooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedDirectory = fileChooser.selectedFile
            sourceCachePath = selectedDirectory.absolutePath
            JOptionPane.showMessageDialog(this, "Source cache selected: $sourceCachePath")
        }
    }
}
