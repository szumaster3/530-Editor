package com.misc

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent

class TextPrompt(private val prompt: String, private val textField: JTextComponent) : JLabel(), DocumentListener {
    init {
        foreground = Color.GRAY
        text = prompt
        font = textField.font
        isVisible = textField.text.isEmpty()
        textField.document.addDocumentListener(this)
        textField.layout = BorderLayout()
        textField.add(this, BorderLayout.LINE_START)
    }

    override fun insertUpdate(e: DocumentEvent?) = checkForPrompt()
    override fun removeUpdate(e: DocumentEvent?) = checkForPrompt()
    override fun changedUpdate(e: DocumentEvent?) = checkForPrompt()

    private fun checkForPrompt() {
        isVisible = textField.text.isEmpty()
    }
}