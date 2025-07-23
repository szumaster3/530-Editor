package com.misc.util

import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class HexAsciiEditor(initialData: ByteArray, val onSave: (ByteArray) -> Unit) : JPanel(BorderLayout()) {

    private var updating = false
    private var data = initialData.copyOf()

    private val offsetArea = JTextArea().apply {
        font = Font("Monospaced", Font.PLAIN, 12)
        isEditable = false
        background = Color(240, 240, 240)
        foreground = Color(100, 100, 100)
        lineWrap = false
        border = null
    }

    private val hexArea = JTextArea().apply {
        font = Font("Monospaced", Font.PLAIN, 12)
        lineWrap = false
    }

    private val asciiArea = JTextArea().apply {
        font = Font("Monospaced", Font.PLAIN, 12)
        lineWrap = false
    }

    init {
        val offsetScroll = JScrollPane(offsetArea)
        val hexScroll = JScrollPane(hexArea)
        val asciiScroll = JScrollPane(asciiArea)

        val hexAsciiSplit = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, hexScroll, asciiScroll).apply {
            resizeWeight = 0.5
            dividerSize = 5
        }

        val offsetHexSplit = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, offsetScroll, hexAsciiSplit).apply {
            resizeWeight = 0.05
            dividerSize = 5
        }

        add(offsetHexSplit, BorderLayout.CENTER)

        // Synchronizacja scrolli
        listOf(offsetScroll, hexScroll, asciiScroll).forEach { scrollPane ->
            scrollPane.verticalScrollBar.addAdjustmentListener { e ->
                if (!updating) {
                    updating = true
                    val value = e.value
                    listOf(offsetScroll, hexScroll, asciiScroll).forEach {
                        if (it.verticalScrollBar != e.adjustable) {
                            it.verticalScrollBar.value = value
                        }
                    }
                    updating = false
                }
            }
        }

        // Listeners
        hexArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = syncFromHex()
            override fun removeUpdate(e: DocumentEvent) = syncFromHex()
            override fun changedUpdate(e: DocumentEvent) = syncFromHex()
        })

        asciiArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = syncFromAscii()
            override fun removeUpdate(e: DocumentEvent) = syncFromAscii()
            override fun changedUpdate(e: DocumentEvent) = syncFromAscii()
        })

        // Buttons
        val saveButton = JButton("Save").apply {
            addActionListener {
                val result = JOptionPane.showConfirmDialog(
                    this@HexAsciiEditor,
                    "Are you sure you want to save changes?",
                    "Confirm Save",
                    JOptionPane.YES_NO_OPTION
                )
                if (result == JOptionPane.YES_OPTION) {
                    onSave(data)
                    JOptionPane.showMessageDialog(this@HexAsciiEditor, "Saved successfully!", "Info", JOptionPane.INFORMATION_MESSAGE)
                }
            }
        }

        val resetButton = JButton("Reset").apply {
            addActionListener {
                data = initialData.copyOf()
                updateTextAreas()
            }
        }

        val buttonPanel = JPanel().apply {
            add(saveButton)
            add(resetButton)
        }

        add(buttonPanel, BorderLayout.SOUTH)
        updateTextAreas()
    }

    private fun updateTextAreas() {
        updating = true
        offsetArea.text = buildOffsetText(data)
        hexArea.text = buildHexText(data)
        asciiArea.text = buildAsciiText(data)
        updating = false
    }

    private fun buildOffsetText(bytes: ByteArray): String {
        val lines = (bytes.size + 15) / 16
        return buildString {
            for (i in 0 until lines) {
                append(String.format("%04X:\n", i * 16))
            }
        }
    }

    private fun buildHexText(bytes: ByteArray): String {
        return buildString {
            for (i in bytes.indices) {
                append(String.format("%02X ", bytes[i]))
                if ((i + 1) % 16 == 0) append("\n")
            }
        }
    }

    private fun buildAsciiText(bytes: ByteArray): String {
        return buildString {
            for (i in bytes.indices) {
                val b = bytes[i].toInt() and 0xFF
                append(if (b in 32..126) b.toChar() else '.')
                if ((i + 1) % 16 == 0) append("\n")
            }
        }
    }

    private fun syncFromHex() {
        if (updating) return
        updating = true
        try {
            val hexText = hexArea.text.replace("[^0-9A-Fa-f]".toRegex(), "")
            if (hexText.length % 2 != 0) throw IllegalArgumentException("Odd number of hex digits.")
            val len = hexText.length / 2
            val newData = ByteArray(len)
            for (i in 0 until len) {
                newData[i] = hexText.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }
            data = newData
            asciiArea.text = buildAsciiText(data)
            offsetArea.text = buildOffsetText(data)
        } catch (_: Exception) {
        }
        updating = false
    }

    private fun syncFromAscii() {
        if (updating) return
        updating = true
        try {
            val asciiText = asciiArea.text.replace("\n", "")
            val newData = ByteArray(asciiText.length)
            for (i in asciiText.indices) {
                val ch = asciiText[i]
                newData[i] = if (ch.code in 32..126) ch.code.toByte() else '.'.code.toByte()
            }
            data = newData
            hexArea.text = buildHexText(data)
            offsetArea.text = buildOffsetText(data)
        } catch (_: Exception) {
        }
        updating = false
    }
}