package com.misc.transfer

import com.displee.cache.CacheLibrary
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.*

class IndexTransfer : JFrame() {
    private val sourceCacheField = JTextField(20).apply { isEditable = false }
    private val targetCacheField = JTextField(20).apply { isEditable = false }
    private val indexField = JTextField(15)
    private val transferButton = JButton("Transfer Indices")

    init {
        title = "Transfer Indices"
        layout = GridBagLayout()
        size = Dimension(490, 190)
        defaultCloseOperation = 1
        setLocationRelativeTo(null)
        isResizable = false
        val gbc =
            GridBagConstraints().apply {
                insets = Insets(5, 5, 5, 5)
                fill = GridBagConstraints.HORIZONTAL
            }
        val selectSourceButton =
            JButton("Select Source cache").apply {
                addActionListener { sourceCacheField.text = selectDirectory() }
            }

        val selectTargetButton =
            JButton("Select Target cache").apply {
                addActionListener { targetCacheField.text = selectDirectory() }
            }

        transferButton.addActionListener { transferIndices() }

        add(
            JLabel("Source cache"),
            gbc.apply {
                gridx = 0
                gridy = 0
            },
        )
        add(sourceCacheField, gbc.apply { gridx = 1 })
        add(selectSourceButton, gbc.apply { gridx = 2 })

        add(
            JLabel("Target cache"),
            gbc.apply {
                gridx = 0
                gridy = 1
            },
        )
        add(targetCacheField, gbc.apply { gridx = 1 })
        add(selectTargetButton, gbc.apply { gridx = 2 })

        add(
            JLabel("Index IDs (comma-separated)"),
            gbc.apply {
                gridx = 0
                gridy = 2
            },
        )
        add(
            indexField,
            gbc.apply {
                gridx = 1
                gridwidth = 2
            },
        )

        add(
            transferButton,
            gbc.apply {
                gridx = 1
                gridy = 3
            },
        )

        isVisible = true
    }

    private fun selectDirectory(): String {
        val fileChooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }
        return if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            ""
        }
    }

    private fun transferIndices() {
        val sourcePath = sourceCacheField.text
        val targetPath = targetCacheField.text
        val indexIds = indexField.text.split(",").mapNotNull { it.trim().toIntOrNull() }

        if (sourcePath.isEmpty() || targetPath.isEmpty() || indexIds.isEmpty()) {
            showError("Invalid input. Ensure paths are selected and indices are valid numbers.")
            return
        }

        val cacheFrom = CacheLibrary.create(sourcePath, true)
        val cacheTo = CacheLibrary.create(targetPath, true)

        for (indexId in indexIds) {
            val indexTo = cacheTo.index(indexId)

            cacheFrom.index(indexId).cache()
            indexTo.clear()
            indexTo.add(*cacheFrom.index(indexId).archives())
            indexTo.update()
        }

        val result =
            JOptionPane.showConfirmDialog(
                this,
                "Rebuild the cache?",
                "Rebuild cache",
                JOptionPane.YES_NO_OPTION,
            )

        if (result == JOptionPane.YES_OPTION) {
            cacheTo.rebuild(File("data/rebuild"))
            showMessage("Success", "Rebuild done.")
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

    private fun showMessage(
        title: String,
        message: String,
    ) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE)
    }
}
