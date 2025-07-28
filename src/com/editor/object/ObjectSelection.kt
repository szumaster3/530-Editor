package com.editor.`object`

import com.cache.defs.ObjectDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getObjectDefinitionsSize
import com.misc.TextPrompt
import java.awt.Component
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import java.util.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.collections.ArrayList
import launcher.Main.log

class ObjectSelection : JFrame {
    private var addButton: JButton? = null
    private var duplicateButton: JButton? = null
    private var editButton: JButton? = null
    private var objectsListModel: DefaultListModel<ObjectDefinition>? = null
    private var objectsList: JList<ObjectDefinition>? = null
    private var jMenu1: JMenu? = null
    private var jMenuBar1: JMenuBar? = null
    private var exitButton: JMenuItem? = null
    private var deleteButton: JButton? = null
    private var searchField: JTextField? = null

    private val allObjects: MutableList<ObjectDefinition> = ArrayList()

    constructor(cache: String?) {
        CACHE = Cache(cache)
        title = "Object Selection"
        isResizable = false
        defaultCloseOperation = 2
        setLocationRelativeTo(null)
        initComponents()
    }

    constructor() {
        initComponents()
    }

    private fun initComponents() {
        editButton = JButton("Edit")
        addButton = JButton("Add New")
        duplicateButton = JButton("Duplicate")
        deleteButton = JButton("Delete")
        jMenuBar1 = JMenuBar()
        jMenu1 = JMenu("File")
        exitButton = JMenuItem("Close")

        this.objectsListModel = DefaultListModel()
        this.objectsList = JList(this.objectsListModel)
        objectsList!!.selectionMode = ListSelectionModel.SINGLE_SELECTION
        objectsList!!.setCellRenderer(ObjectListCellRenderer())
        val jScrollPane1: JScrollPane = JScrollPane(objectsList)

        searchField = JTextField()
        searchField!!.columns = 20
        searchField!!
            .document
            .addDocumentListener(
                object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent) {
                        filterObjectList(searchField!!.text)
                    }

                    override fun removeUpdate(e: DocumentEvent) {
                        filterObjectList(searchField!!.text)
                    }

                    override fun changedUpdate(e: DocumentEvent) {
                        filterObjectList(searchField!!.text)
                    }
                }
            )

        TextPrompt("Search by ID or name...", searchField!!)

        editButton!!.addActionListener(
            ActionListener { e: ActionEvent? ->
                try {
                    editObject()
                } catch (ex: IOException) {
                    throw RuntimeException(ex)
                }
            }
        )
        addButton!!.addActionListener(
            ActionListener { e: ActionEvent? ->
                try {
                    addNewObject()
                } catch (ex: IOException) {
                    throw RuntimeException(ex)
                }
            }
        )
        duplicateButton!!.addActionListener(
            ActionListener { e: ActionEvent? ->
                try {
                    duplicateObject()
                } catch (ex: IOException) {
                    throw RuntimeException(ex)
                }
            }
        )
        deleteButton!!.addActionListener(ActionListener { e: ActionEvent? -> deleteObject() })

        exitButton!!.addActionListener(
            ActionListener { evt: ActionEvent -> this.exitButtonActionPerformed(evt) }
        )
        jMenu1!!.add(exitButton)
        jMenuBar1!!.add(jMenu1)
        jMenuBar = jMenuBar1

        val layout: GroupLayout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            layout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    searchField,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(
                                    jScrollPane1,
                                    GroupLayout.PREFERRED_SIZE,
                                    200,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addGroup(
                                    layout
                                        .createSequentialGroup()
                                        .addComponent(editButton)
                                        .addGap(0, 0, Int.MAX_VALUE)
                                        .addComponent(addButton)
                                )
                                .addGroup(
                                    layout
                                        .createSequentialGroup()
                                        .addComponent(duplicateButton)
                                        .addGap(0, 0, Int.MAX_VALUE)
                                        .addComponent(deleteButton)
                                )
                        )
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )

        layout.setVerticalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            searchField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            jScrollPane1,
                            GroupLayout.PREFERRED_SIZE,
                            279,
                            GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            layout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(editButton)
                                .addComponent(addButton)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(
                            layout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(duplicateButton)
                                .addComponent(deleteButton)
                        )
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )

        pack()
        addAllObjects()
    }

    private fun exitButtonActionPerformed(evt: ActionEvent) {
        dispose()
    }

    fun addAllObjects() {
        val totalObjects = getObjectDefinitionsSize(CACHE!!)
        for (id in 0 until totalObjects) {
            val obj = ObjectDefinition.getObjectDefinition(CACHE, id)
            if (obj != null) {
                allObjects.add(obj)
                addObjectDefs(obj)
            }
        }
        log("ObjectSelection", "All Objects Loaded")
    }

    private fun filterObjectList(searchTerm: String?) {
        val filtered: MutableList<ObjectDefinition>
        if (searchTerm == null || searchTerm.trim { it <= ' ' }.isEmpty()) {
            filtered = allObjects
        } else {
            filtered = ArrayList()
            for (obj in allObjects) {
                if (
                    (obj.getName() != null &&
                            obj.getName()
                                .lowercase(Locale.getDefault())
                                .contains(searchTerm.lowercase(Locale.getDefault()))) ||
                    obj.id.toString() == searchTerm
                ) {
                    filtered.add(obj)
                }
            }
        }

        objectsListModel!!.clear()
        for (obj in filtered) {
            objectsListModel!!.addElement(obj)
        }
    }

    fun addObjectDefs(defs: ObjectDefinition) {
        EventQueue.invokeLater { objectsListModel!!.addElement(defs) }
    }

    fun updateObjectDefs(obj: ObjectDefinition) {
        EventQueue.invokeLater {
            val index = objectsListModel!!.indexOf(obj)
            if (index == -1) {
                objectsListModel!!.addElement(obj)
            } else {
                objectsListModel!!.setElementAt(obj, index)
            }
        }
    }

    fun removeObjectDefs(obj: ObjectDefinition) {
        EventQueue.invokeLater {
            objectsListModel!!.removeElement(obj)
            allObjects.remove(obj)
        }
    }

    @Throws(IOException::class)
    private fun editObject() {
        val selectedIndex = objectsList!!.selectedIndex
        if (selectedIndex != -1) {
            val obj = objectsList!!.selectedValue
            if (obj != null) {
                ObjectEditor(this, obj)
            }
        }
    }

    @Throws(IOException::class)
    private fun addNewObject() {
        val obj = ObjectDefinition(CACHE, newObjectID, false)
        if (obj != null && obj.id != -1) {
            println("Adding new object with ID: " + obj.id)
            ObjectEditor(this, obj).isVisible = true
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to create a new object!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    @Throws(IOException::class)
    private fun duplicateObject() {
        val obj = objectsList!!.selectedValue
        if (obj != null) {
            val clonedObj = obj.clone() as ObjectDefinition
            if (clonedObj != null) {
                clonedObj.id = newObjectID
                if (clonedObj.id != -1) {
                    ObjectEditor(this, clonedObj).isVisible = true
                }
            }
        }
    }

    private fun deleteObject() {
        val obj = objectsList!!.selectedValue
        if (obj != null) {
            val result: Int =
                JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to delete object [" + obj.id + "]?"
                )
            if (result == JOptionPane.YES_OPTION) {
                println("Deleting object: " + obj.id)
                CACHE!!.indexes[16].removeFile(obj.archiveId, obj.fileId)
                removeObjectDefs(obj)
                log("ObjectSelection", "Object " + obj.id + " removed.")
            }
        }
    }

    private val newObjectID: Int
        get() = getObjectDefinitionsSize(CACHE!!)

    private inner class ObjectListCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val c: Component =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is ObjectDefinition) {
                val defs = value
                setText(defs.id.toString() + " - " + defs.getName())
            }
            return c
        }
    }

    companion object {
        var CACHE: Cache? = null

        @JvmStatic
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            CACHE = Cache("cache/", false)
            EventQueue.invokeLater { ObjectSelection().isVisible = true }
        }
    }
}
