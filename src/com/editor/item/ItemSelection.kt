package com.editor.item

import com.cache.defs.ItemDefinition
import com.cache.defs.ItemDefinition.Companion.getItemDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getItemDefinitionsSize
import com.misc.TextPrompt
import java.awt.EventQueue
import java.io.IOException
import javax.swing.*
import javax.swing.LayoutStyle.ComponentPlacement

class ItemSelection : JFrame {
    private var itemDefinitionListModel: DefaultListModel<ItemDefinition>? = null
    private var itemList: JList<ItemDefinition>? = null
    private val fullItemList = mutableListOf<ItemDefinition>()

    constructor(cache: String?) {
        CACHE = Cache(cache)
        title = "Item Selection"
        isResizable = false
        defaultCloseOperation = 2
        setLocationRelativeTo(null)
        initComponents()
    }

    constructor() {
        initComponents()
    }

    private fun initComponents() {
        val editButton = JButton("Edit")
        val addButton = JButton("Add New")
        val duplicateButton = JButton("Duplicate")
        val deleteButton = JButton("Delete")
        val infoButton = JButton("Info")
        val deleteFileButton = JButton("Delete file")
        val menuBar = JMenuBar()
        val fileMenu = JMenu("File")
        val exitButton = JMenuItem("Close")
        val searchField = JTextField(20).apply {
            toolTipText = "Search by name"
        }
        TextPrompt("Search by ID or name...", searchField)

        itemDefinitionListModel = DefaultListModel()
        itemList = JList(itemDefinitionListModel).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            layoutOrientation = JList.VERTICAL
            visibleRowCount = -1
        }
        val scrollPane = JScrollPane(itemList)

        editButton.addActionListener {
            itemList?.selectedValue?.let { ItemEditor(this, it) }
        }

        addButton.addActionListener {
            val definition = ItemDefinition(CACHE!!, newItemDefinition, false)
            if (definition.id != -1) ItemEditor(this, definition)
        }

        duplicateButton.addActionListener {
            itemList?.selectedValue?.clone()?.let {
                (it as ItemDefinition).apply {
                    id = newItemDefinition
                    if (id != -1) ItemEditor(this@ItemSelection, this)
                }
            }
        }

        deleteButton.addActionListener {
            val selected = itemList?.selectedValue ?: return@addActionListener
            if (JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to delete item [${selected.id}]?"
                ) == JOptionPane.YES_OPTION
            ) {
                removeItemDefinition(selected)
            }
        }

        infoButton.addActionListener {
            val selected = itemList?.selectedValue
            if (selected != null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Item ID: ${selected.id}\nArchive ID: ${selected.archiveId}\nFile ID: ${selected.fileId}",
                    "Item Info",
                    JOptionPane.INFORMATION_MESSAGE
                )
                println("Item ID: ${selected.id}\nArchive ID: ${selected.archiveId}\nFile ID: ${selected.fileId}")
            } else {
                JOptionPane.showMessageDialog(this, "No item selected.", "Warning", JOptionPane.WARNING_MESSAGE)
            }
        }

        deleteFileButton.addActionListener {
            val options = arrayOf("Remove Last File", "Remove Specific File")
            val choice = JOptionPane.showOptionDialog(
                this,
                "Choose delete option:",
                "Delete File",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )

            if (choice == 0) {
                val archiveId = JOptionPane.showInputDialog(this, "Enter Archive ID:")?.toIntOrNull()
                if (archiveId == null) {
                    JOptionPane.showMessageDialog(this, "Invalid archive ID.", "Error", JOptionPane.ERROR_MESSAGE)
                    return@addActionListener
                }

                val confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove the LAST file from archive $archiveId?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirm == JOptionPane.YES_OPTION) {
                    val success = forceRemoveFileReference(archiveId)
                    if (success) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Last file removed from archive $archiveId.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                        reloadItemList()
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Failed to remove last file.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }

            } else if (choice == 1) {
                val archiveId = JOptionPane.showInputDialog(this, "Enter Archive ID:")?.toIntOrNull()
                val fileId = JOptionPane.showInputDialog(this, "Enter File ID:")?.toIntOrNull()

                if (archiveId == null || fileId == null) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid archive or file ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                    return@addActionListener
                }

                val confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove file $fileId from archive $archiveId?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirm == JOptionPane.YES_OPTION) {
                    val success = removeArchiveFile(archiveId, fileId)
                    if (success) {
                        JOptionPane.showMessageDialog(
                            this,
                            "File $fileId removed from archive $archiveId.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                        reloadItemList()
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Failed to remove file.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            }
        }

        exitButton.addActionListener { dispose() }
        fileMenu.add(exitButton)
        menuBar.add(fileMenu)
        jMenuBar = menuBar

        searchField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            private fun filter() {
                val text = searchField.text.lowercase()
                val filtered = fullItemList.filter { it.name?.lowercase()?.contains(text) == true }
                SwingUtilities.invokeLater {
                    itemDefinitionListModel?.apply {
                        clear()
                        filtered.forEach { addElement(it) }
                    }
                    itemList?.repaint()
                }
            }

            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = filter()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = filter()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = filter()
        })

        contentPane.layout = GroupLayout(contentPane).apply {
            autoCreateGaps = true
            autoCreateContainerGaps = true

            setHorizontalGroup(
                createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(editButton)
                            .addGap(0, 0, Int.MAX_VALUE)
                            .addComponent(addButton)
                    )
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(duplicateButton)
                            .addGap(0, 0, Int.MAX_VALUE)
                            .addComponent(deleteButton)
                    )
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(infoButton)
                            .addGap(0, 0, Int.MAX_VALUE)
                            .addComponent(deleteFileButton)
                    )
            )

            setVerticalGroup(
                createSequentialGroup()
                    .addComponent(
                        searchField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE
                    )
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(
                        createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(editButton)
                            .addComponent(addButton)
                    )
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(duplicateButton)
                            .addComponent(deleteButton)
                    )
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(infoButton)
                            .addComponent(deleteFileButton)
                    )
            )
        }

        deleteFileButton.isEnabled = false
        infoButton.isEnabled = false
        pack()

        Thread {
            val size = getItemDefinitionsSize(CACHE!!)
            for (id in 0 until size) {
                getItemDefinition(CACHE!!, id)?.let {
                    fullItemList.add(it)
                    SwingUtilities.invokeLater { itemDefinitionListModel?.addElement(it) }
                }
            }
        }.start()
    }

    private val newItemDefinition: Int
        get() = getItemDefinitionsSize(CACHE!!)

    private fun reloadItemList() {
        fullItemList.clear()
        itemDefinitionListModel?.clear()
        val size = getItemDefinitionsSize(CACHE!!)
        for (id in 0 until size) {
            getItemDefinition(CACHE!!, id)?.let {
                fullItemList.add(it)
                itemDefinitionListModel?.addElement(it)
            }
        }
        itemList?.repaint()
    }

    fun updateItemDefinition(def: ItemDefinition?) {
        if (def == null) return
        EventQueue.invokeLater {
            val index = itemDefinitionListModel?.indexOf(def) ?: -1
            if (index == -1) {
                itemDefinitionListModel?.addElement(def)
                fullItemList.add(def)
            } else {
                itemDefinitionListModel?.setElementAt(def, index)
                fullItemList[index] = def
            }
        }
    }

    private fun removeItemDefinition(def: ItemDefinition) {
        try {
            val index = CACHE!!.indexes[19]
            if (!index.fileExists(def.archiveId, def.fileId)) {
                JOptionPane.showMessageDialog(
                    this,
                    "File does not exist in cache.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
                )
                return
            }
            val removed = index.removeFile(def.archiveId, def.fileId)
            if (!removed) {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to remove file from cache.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            if (index.getValidFilesCount(def.archiveId) == 0) {
                index.removeArchive(def.archiveId)
            }
            val rewritten = index.rewriteTable()
            if (!rewritten) {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to rewrite index table.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            EventQueue.invokeLater {
                fullItemList.removeIf { it.id == def.id }
                itemDefinitionListModel?.removeElement(def)
                reloadItemList()
                JOptionPane.showMessageDialog(
                    this,
                    "Item removed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to remove item definition: ${ex.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
            ex.printStackTrace()
        }
    }

    fun removeArchiveFile(archiveId: Int, fileId: Int): Boolean {
        try {
            val index = CACHE?.indexes?.get(19)
                ?: throw IllegalStateException("Cache or index 19 is not initialized.")

            if (!index.fileExists(archiveId, fileId)) {
                JOptionPane.showMessageDialog(
                    null,
                    "File does not exist in cache.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
                )
                return false
            }

            index.removeFile(archiveId, fileId)

            if (index.fileExists(archiveId, fileId)) {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to remove file from cache.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return false
            }

            val validFilesCount = index.getValidFilesCount(archiveId)
            if (validFilesCount == 0) {
                index.removeArchive(archiveId)
            }

            if (!index.rewriteTable()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to rewrite index table.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return false
            }

            EventQueue.invokeLater {
                reloadItemList()
                JOptionPane.showMessageDialog(
                    null,
                    "File (archiveId=$archiveId, fileId=$fileId) removed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(
                null,
                "Failed to remove file: ${ex.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }
    }

    fun forceRemoveFileReference(archiveId: Int): Boolean {
        val index = CACHE!!.indexes[19]
        val archiveRef = index.table.archives[archiveId]

        if (archiveRef == null) {
            println("File reference does not exist.")
            return false
        }

        val validFileIds = archiveRef.validFileIds
        if (validFileIds.isEmpty()) {
            println("No valid files to remove in archive $archiveId.")
            return false
        }

        val newValidFileIds = validFileIds.toMutableList()
        val removedFileId = newValidFileIds.removeAt(newValidFileIds.size - 1)
        println("Removing last file reference with fileId = $removedFileId from archive $archiveId")

        archiveRef.validFileIds = newValidFileIds.toIntArray()

        val rewritten = index.rewriteTable()

        return rewritten
    }

    companion object {
        var CACHE: Cache? = null

        @JvmStatic
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            CACHE = Cache("cache/", false)
            EventQueue.invokeLater { ItemSelection().isVisible = true }
        }
    }
}
