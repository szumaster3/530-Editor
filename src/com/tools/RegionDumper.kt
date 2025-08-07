package com.tools

import com.displee.cache.CacheLibrary
import cache.alex.util.XTEAManager
import java.awt.*
import java.io.File
import javax.swing.*

class RegionDumper(val cache: String) : JFrame() {
    private val cacheLibrary = CacheLibrary.create(cache)

    private val regionIdField = JTextField(10)
    private val xteaPathField = JTextField(25).apply { isEditable = false }
    private val selectXteaButton = JButton("Select XTEA JSON")
    private val outputDirField = JTextField(25).apply { text = "./data/export/maps/" }
    private val dumpButton = JButton("Dump Region")

    init {
        title = "Region Dumper"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        size = Dimension(450, 330)
        isResizable = false
        setLocationRelativeTo(null)
        layout = GridBagLayout()

        val gbc = GridBagConstraints().apply {
            insets = Insets(10, 10, 10, 10)
            fill = GridBagConstraints.BOTH
            weightx = 1.0
        }

        val regionPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Region")
        }
        regionPanel.add(JLabel("Region ID:"), GridBagConstraints().apply {
            gridx = 0; gridy = 0; weightx = 0.0; anchor = GridBagConstraints.WEST; insets = Insets(5,5,5,5)
        })
        regionPanel.add(regionIdField, GridBagConstraints().apply {
            gridx = 1; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL; insets = Insets(5,5,5,5)
        })

        val xteaPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "XTEA Key")
        }
        xteaPanel.add(JLabel("XTEA JSON File:"), GridBagConstraints().apply {
            gridx = 0; gridy = 0; weightx = 0.0; anchor = GridBagConstraints.WEST; insets = Insets(5,5,5,5)
        })
        xteaPanel.add(xteaPathField, GridBagConstraints().apply {
            gridx = 1; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL; insets = Insets(5,5,5,5)
        })
        xteaPanel.add(selectXteaButton, GridBagConstraints().apply {
            gridx = 2; gridy = 0; weightx = 0.0; insets = Insets(5,5,5,5)
        })

        val outputPanel = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output")
        }
        outputPanel.add(JLabel("Output Directory:"), GridBagConstraints().apply {
            gridx = 0; gridy = 0; weightx = 0.0; anchor = GridBagConstraints.WEST; insets = Insets(5,5,5,5)
        })
        outputPanel.add(outputDirField, GridBagConstraints().apply {
            gridx = 1; gridy = 0; weightx = 1.0; fill = GridBagConstraints.HORIZONTAL; insets = Insets(5,5,5,5)
        })

        add(regionPanel, gbc.apply { gridx = 0; gridy = 0; gridwidth = 3; weighty = 0.0 })
        add(xteaPanel, gbc.apply { gridx = 0; gridy = 1; gridwidth = 3; weighty = 0.0 })
        add(outputPanel, gbc.apply { gridx = 0; gridy = 2; gridwidth = 3; weighty = 0.0 })

        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER, 15, 5)
            add(dumpButton)
        }
        add(buttonPanel, gbc.apply { gridx = 0; gridy = 3; gridwidth = 3; weighty = 0.0; fill = GridBagConstraints.NONE })

        selectXteaButton.addActionListener {
            val path = selectFile()
            if (path.isNotEmpty()) {
                xteaPathField.text = path
                loadXteas(path)
            }
        }

        dumpButton.addActionListener { dumpRegion() }

        isVisible = true
    }

    private fun selectFile(): String {
        val chooser = JFileChooser()
        return if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else ""
    }

    private fun loadXteas(path: String) {
        val file = File(path)
        if (!file.exists()) {
            showError("XTEA JSON file not found.")
            return
        }
        val success = XTEAManager.load(file)
        if (success) {
            JOptionPane.showMessageDialog(this, "XTEA keys loaded successfully!", "Info", JOptionPane.INFORMATION_MESSAGE)
        } else {
            showError("Failed to load XTEA keys from file.")
        }
    }

    private fun dumpRegion() {
        val regionId = regionIdField.text.toIntOrNull()
        val outputDir = outputDirField.text

        if (regionId == null) {
            showError("Invalid region ID.")
            return
        }
        if (outputDir.isEmpty()) {
            showError("Please specify an output directory.")
            return
        }
        if (!XTEAManager.isLoaded()) {
            showError("Please load XTEA keys first.")
            return
        }

        try {
            val x = (regionId shr 8) and 0xFF
            val y = regionId and 0xFF
            val xtea = XTEAManager.lookup(regionId)

            val tiles = cacheLibrary.data(5, "m${x}_${y}")
                ?: throw Exception("Map data not found for region ${x}_$y")
            val objects = cacheLibrary.data(5, "l${x}_${y}", xtea)
                ?: throw Exception("Landscape data not found or missing XTEA key for region ${x}_$y")

            val outDirFile = File(outputDir)
            if (!outDirFile.exists()) outDirFile.mkdirs()

            File(outDirFile, "region_${regionId}_tiles.dat").writeBytes(tiles)
            File(outDirFile, "region_${regionId}_objects.dat").writeBytes(objects)

            showMessage("Success", "Region ${x}_$y dumped successfully!")
        } catch (ex: Exception) {
            ex.printStackTrace()
            showError("Error dumping region: ${ex.message}")
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

    private fun showMessage(title: String, message: String) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE)
    }
}
