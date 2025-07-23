package com.editor.npc

import com.cache.defs.NPCDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getNPCDefinitionsSize
import com.misc.TextPrompt
import launcher.Main.log
import java.awt.EventQueue
import java.io.IOException
import javax.swing.*
import javax.swing.LayoutStyle.ComponentPlacement
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class NPCSelection : JFrame {
    private var addButton: JButton? = null
    private var duplicateButton: JButton? = null
    private var editButton: JButton? = null
    private var deleteButton: JButton? = null
    private var npcListModel: DefaultListModel<NPCDefinition>? = null
    private var npcList: JList<NPCDefinition?>? = null
    private var jMenu1: JMenu? = null
    private var jMenuBar1: JMenuBar? = null
    private var exitButton: JMenuItem? = null
    private var searchField: JTextField? = null

    private val allNPCs = mutableListOf<NPCDefinition>()

    constructor(cache: String?) {
        Cache = Cache(cache)
        this.title = "NPC Selection"
        this.isResizable = false
        this.defaultCloseOperation = 2
        this.setLocationRelativeTo(null)
        this.initComponents()
    }

    constructor() {
        this.initComponents()
    }

    private fun initComponents() {
        this.editButton = JButton()
        this.addButton = JButton()
        this.duplicateButton = JButton()
        this.deleteButton = JButton()
        this.jMenuBar1 = JMenuBar()
        this.jMenu1 = JMenu()
        this.exitButton = JMenuItem()
        this.npcListModel = DefaultListModel<NPCDefinition>()
        this.npcList = JList<NPCDefinition?>(this.npcListModel)
        npcList?.selectionMode = ListSelectionModel.SINGLE_SELECTION
        npcList?.layoutOrientation = JList.VERTICAL
        npcList?.visibleRowCount = -1
        val jScrollPane1 = JScrollPane(this.npcList)

        searchField = JTextField()
        searchField?.columns = 20
        searchField?.document?.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = filterNPCList(searchField?.text ?: "")
            override fun removeUpdate(e: DocumentEvent?) = filterNPCList(searchField?.text ?: "")
            override fun changedUpdate(e: DocumentEvent?) = filterNPCList(searchField?.text ?: "")
        })
        TextPrompt("Search by ID or name...", searchField!!)
        editButton?.apply {
            text = "Edit"
            addActionListener {
                val defs = npcList?.selectedValue
                if (defs != null) {
                    NPCEditor(this@NPCSelection, defs)
                }
            }
        }

        addButton?.apply {
            text = "Add New"
            addActionListener {
                val npc = NPCDefinition(Cache, newNPCID, false)
                if (npc.id != -1) {
                    NPCEditor(this@NPCSelection, npc).isVisible = true
                }
            }
        }

        duplicateButton?.apply {
            text = "Duplicate"
            addActionListener {
                val npc = npcList?.selectedValue
                if (npc != null) {
                    val clonedNpc = npc.clone() as NPCDefinition
                    clonedNpc.id = this@NPCSelection.newNPCID
                    if (clonedNpc.id != -1) {
                        NPCEditor(this@NPCSelection, clonedNpc).isVisible = true
                    }
                }
            }
        }

        deleteButton?.apply {
            text = "Delete"
            addActionListener {
                val defs = npcList?.selectedValue
                if (defs != null) {
                    val frame = JFrame()
                    val result = JOptionPane.showConfirmDialog(frame, "Do you really want to delete NPC: [${defs.id}]?")
                    if (result == JOptionPane.YES_OPTION) {
                        Cache?.indexes?.get(18)?.removeFile(defs.archiveId, defs.fileId)
                        this@NPCSelection.removeNPCDefs(defs)
                        log("NPCSelection", "NPC ID: ${defs.id} removed.")
                    }
                }
            }
        }

        jMenu1?.apply {
            text = "File"
            exitButton?.apply {
                text = "Close"
                addActionListener { this@NPCSelection.exitButtonActionPerformed() }
            }
            add(exitButton)
        }

        jMenuBar1?.add(jMenu1)
        this.jMenuBar = jMenuBar1

        val layout = GroupLayout(this.contentPane)
        this.contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    searchField,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addGroup(
                                    layout.createSequentialGroup()
                                        .addComponent(editButton)
                                        .addGap(0, 0, Int.MAX_VALUE)
                                        .addComponent(addButton)
                                )
                                .addGroup(
                                    layout.createSequentialGroup()
                                        .addComponent(duplicateButton)
                                        .addGap(0, 0, Int.MAX_VALUE)
                                        .addComponent(deleteButton)
                                )
                        )
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            searchField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(editButton)
                                .addComponent(addButton)
                        )
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(duplicateButton)
                                .addComponent(deleteButton)
                        )
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )

        this.pack()
        this.addAllNPCs()
    }

    private fun exitButtonActionPerformed() {
        this.dispose()
    }

    val newNPCID: Int
        get() = getNPCDefinitionsSize(Cache!!)

    fun addAllNPCs() {
        val npcCount = getNPCDefinitionsSize(Cache!!)
        println("NPC Count: $npcCount")
        for (id in 0 until npcCount) {
            val npc = NPCDefinition.getNPCDefinition(Cache, id)
            if (npc != null) {
                allNPCs.add(npc)
                this.addNPCDefs(npc)
            } else {
                println("Error: NPC not loaded for ID $id")
            }
        }
        log("NPCSelection", "All NPCs Loaded")
    }

    private fun filterNPCList(searchTerm: String) {
        val filtered = if (searchTerm.isBlank()) {
            allNPCs
        } else {
            allNPCs.filter { npc ->
                npc.name?.contains(searchTerm, ignoreCase = true) == true ||
                        npc.id.toString() == searchTerm
            }
        }

        npcListModel?.clear()
        for (npc in filtered) {
            npcListModel?.addElement(npc)
        }
    }

    fun addNPCDefs(npc: NPCDefinition?) {
        EventQueue.invokeLater { npcListModel?.addElement(npc) }
    }

    fun updateNPCDefs(npc: NPCDefinition?) {
        EventQueue.invokeLater {
            val index = npcListModel?.indexOf(npc)
            if (index == -1) {
                npcListModel?.addElement(npc)
            } else {
                if (index != null) {
                    npcListModel?.setElementAt(npc, index)
                }
            }
        }
    }

    fun removeNPCDefs(npc: NPCDefinition?) {
        EventQueue.invokeLater {
            npcListModel?.removeElement(npc)
            allNPCs.remove(npc)
        }
    }

    companion object {
        @JvmField
        var Cache: Cache? = null

        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            Cache = Cache("cache/", false)
            EventQueue.invokeLater { NPCSelection().isVisible = true }
        }
    }
}