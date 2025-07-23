package com.misc.model

import com.cache.store.Cache
import launcher.Main.log
import java.awt.event.ActionEvent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.*

internal class ModelExporter(
    cache: String,
) : JFrame() {
    private val modelID = JTextField(10)
    private val submit = JButton("Submit")
    private val exit = JMenuItem("Exit")
    private val menuBar = JMenuBar()
    private val fileMenu = JMenu("File")
    private val label = JLabel("Model ID To Dump:")

    init {
        title = "Model Exporter"
        isResizable = false
        defaultCloseOperation = 1
        setLocationRelativeTo(null)

        val layout = GroupLayout(contentPane)
        layout.autoCreateGaps = true
        layout.autoCreateContainerGaps = true

        layout.setHorizontalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label)
                .addComponent(modelID, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
                .addComponent(submit),
        )

        layout.setVerticalGroup(
            layout
                .createSequentialGroup()
                .addComponent(label)
                .addComponent(modelID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(submit),
        )

        contentPane.layout = layout

        submit.addActionListener { e: ActionEvent? -> submitAction(cache) }
        exit.addActionListener { e: ActionEvent? -> dispose() }

        fileMenu.add(exit)
        menuBar.add(fileMenu)
        jMenuBar = menuBar

        pack()
    }

    public fun submitAction(cache: String) {
        val dir = File("data/export/")
        dir.mkdirs()

        try {
            val store = Cache(cache)
            val index = store.indexes[7]
            val modelId = modelID.text.toInt()
            val data = index.getFile(modelId)
            val filePath = "data/export/models/$modelId.dat"
            writeFile(data!!, filePath)
            log("ModelExporter", "Dumped Model ID: $modelId to: $filePath")
        } catch (e: IOException) {
            log("ModelExporter", "Cannot Dump Model")
        } catch (e: NumberFormatException) {
            log("ModelExporter", "Invalid Model ID")
        }
    }

    companion object {
        @Throws(IOException::class)
        fun writeFile(
            data: ByteArray,
            fileName: String,
        ) {
            FileOutputStream(fileName).use { fos ->
                fos.write(data)
                fos.close()
            }
        }
    }
}
