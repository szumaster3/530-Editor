package com.misc.extract

import com.cache.util.Utils
import com.misc.extract.Core.start
import launcher.Main
import java.awt.event.*
import javax.swing.*

class IndexExport : JFrame() {
    private var fileChooser: JFileChooser? = null
    private var btnExtract: JButton? = null
    private var checkBox: JCheckBox? = null
    private var checkBox1: JCheckBox? = null
    private var checkBox2: JCheckBox? = null
    private var checkBox3: JCheckBox? = null
    private var checkBox4: JCheckBox? = null
    private var checkBox5: JCheckBox? = null
    private var checkBox6: JCheckBox? = null
    private var checkBox7: JCheckBox? = null
    private var checkBox8: JCheckBox? = null
    private var checkBox9: JCheckBox? = null
    private var checkBox10: JCheckBox? = null
    private var checkBox11: JCheckBox? = null
    private var checkBox12: JCheckBox? = null
    private var checkBox13: JCheckBox? = null
    private var checkBox14: JCheckBox? = null
    private var checkBox15: JCheckBox? = null
    private var checkBox16: JCheckBox? = null
    private var checkBox17: JCheckBox? = null
    private var checkBox18: JCheckBox? = null
    private var checkBox19: JCheckBox? = null
    private var checkBox20: JCheckBox? = null
    private var checkBox21: JCheckBox? = null
    private var checkBox22: JCheckBox? = null
    private var checkBox23: JCheckBox? = null
    private var checkBox24: JCheckBox? = null
    private var checkBox25: JCheckBox? = null
    private var checkBox26: JCheckBox? = null
    private var checkBox27: JCheckBox? = null
    private var checkBox28: JCheckBox? = null
    private var checkBox255: JCheckBox? = null
    private var lblNewLabel: JLabel? = null
    private var progressBar: JProgressBar? = null

    private inner class CheckBoxListener : ItemListener {
        override fun itemStateChanged(e: ItemEvent) {
            when (e.source) {
                checkBox -> Utils.toExtract[0] = checkBox!!.isSelected.also { log() }
                checkBox1 -> Utils.toExtract[1] = checkBox1!!.isSelected.also { log() }
                checkBox2 -> Utils.toExtract[2] = checkBox2!!.isSelected.also { log() }
                checkBox3 -> Utils.toExtract[3] = checkBox3!!.isSelected.also { log() }
                checkBox4 -> Utils.toExtract[4] = checkBox4!!.isSelected.also { log() }
                checkBox5 -> Utils.toExtract[5] = checkBox5!!.isSelected.also { log() }
                checkBox6 -> Utils.toExtract[6] = checkBox6!!.isSelected.also { log() }
                checkBox7 -> Utils.toExtract[7] = checkBox7!!.isSelected.also { log() }
                checkBox8 -> Utils.toExtract[8] = checkBox8!!.isSelected.also { log() }
                checkBox9 -> Utils.toExtract[9] = checkBox9!!.isSelected.also { log() }
                checkBox10 -> Utils.toExtract[10] = checkBox10!!.isSelected.also { log() }
                checkBox11 -> Utils.toExtract[11] = checkBox11!!.isSelected.also { log() }
                checkBox12 -> Utils.toExtract[12] = checkBox12!!.isSelected.also { log() }
                checkBox13 -> Utils.toExtract[13] = checkBox13!!.isSelected.also { log() }
                checkBox14 -> Utils.toExtract[14] = checkBox14!!.isSelected.also { log() }
                checkBox15 -> Utils.toExtract[15] = checkBox15!!.isSelected.also { log() }
                checkBox16 -> Utils.toExtract[16] = checkBox16!!.isSelected.also { log() }
                checkBox17 -> Utils.toExtract[17] = checkBox17!!.isSelected.also { log() }
                checkBox18 -> Utils.toExtract[18] = checkBox18!!.isSelected.also { log() }
                checkBox19 -> Utils.toExtract[19] = checkBox19!!.isSelected.also { log() }
                checkBox20 -> Utils.toExtract[20] = checkBox20!!.isSelected.also { log() }
                checkBox21 -> Utils.toExtract[21] = checkBox21!!.isSelected.also { log() }
                checkBox22 -> Utils.toExtract[22] = checkBox22!!.isSelected.also { log() }
                checkBox23 -> Utils.toExtract[23] = checkBox23!!.isSelected.also { log() }
                checkBox24 -> Utils.toExtract[24] = checkBox24!!.isSelected.also { log() }
                checkBox25 -> Utils.toExtract[25] = checkBox25!!.isSelected.also { log() }
                checkBox26 -> Utils.toExtract[26] = checkBox26!!.isSelected.also { log() }
                checkBox27 -> Utils.toExtract[27] = checkBox27!!.isSelected.also { log() }
                checkBox28 -> Utils.toExtract[28] = checkBox28!!.isSelected.also { log() }
                checkBox255 -> Utils.toExtract[255] = checkBox255!!.isSelected.also { log() }
            }
        }

        private fun log() {
            Main.log(this.javaClass.name, Utils.toExtract.contentToString())
        }
    }

    init {
        setSize(350, 400)
        initComponents()
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        Core.progressBar = progressBar
    }

    private fun selectInput(e: MouseEvent) {
        fileChooser = JFileChooser()
        fileChooser!!.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val response = fileChooser!!.showOpenDialog(this)
        if (response == JFileChooser.APPROVE_OPTION) {
            val selectedDir = fileChooser!!.selectedFile
            if (selectedDir.isDirectory) {
                Utils.inputFolder = selectedDir.absolutePath
                Main.log(this.javaClass.name, "Input directory set as: " + Utils.inputFolder)
            }
            enableExtractIfReady()
        }
    }

    private fun selectOutput(e: MouseEvent) {
        fileChooser = JFileChooser()
        fileChooser!!.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val response = fileChooser!!.showOpenDialog(this)
        if (response == JFileChooser.APPROVE_OPTION) {
            val selectedDir = fileChooser!!.selectedFile
            if (selectedDir.isDirectory) {
                Utils.outputFolder = selectedDir.absolutePath
                Main.log(this.javaClass.name, "Output directory set as: " + Utils.outputFolder)
            }
            enableExtractIfReady()
        }
    }

    private fun enableExtractIfReady() {
        btnExtract!!.isEnabled = Utils.inputFolder.isNotEmpty() && Utils.outputFolder.isNotEmpty() && Utils.format.isNotEmpty()
    }

    private fun extract(e: MouseEvent) {
        lblNewLabel!!.text = "Extracting..."
        btnExtract!!.isEnabled = false

        Thread {
            try {
                start()
                SwingUtilities.invokeLater {
                    lblNewLabel!!.text = "Done"
                    btnExtract!!.isEnabled = true
                    progressBar!!.value = 0
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                SwingUtilities.invokeLater {
                    lblNewLabel!!.text = "Error occurred"
                    btnExtract!!.isEnabled = true
                }
            }
        }.start()
    }

    private fun initComponents() {
        btnExtract = JButton()
        checkBox = JCheckBox("0")
        checkBox1 = JCheckBox("1")
        checkBox2 = JCheckBox("2")
        checkBox3 = JCheckBox("3")
        checkBox4 = JCheckBox("4")
        checkBox5 = JCheckBox("5")
        checkBox6 = JCheckBox("6")
        checkBox7 = JCheckBox("7")
        checkBox8 = JCheckBox("8")
        checkBox9 = JCheckBox("9")
        checkBox10 = JCheckBox("10")
        checkBox11 = JCheckBox("11")
        checkBox12 = JCheckBox("12")
        checkBox13 = JCheckBox("13")
        checkBox14 = JCheckBox("14")
        checkBox15 = JCheckBox("15")
        checkBox16 = JCheckBox("16")
        checkBox17 = JCheckBox("17")
        checkBox18 = JCheckBox("18")
        checkBox19 = JCheckBox("19")
        checkBox20 = JCheckBox("20")
        checkBox21 = JCheckBox("21")
        checkBox22 = JCheckBox("22")
        checkBox23 = JCheckBox("23")
        checkBox24 = JCheckBox("24")
        checkBox25 = JCheckBox("25")
        checkBox26 = JCheckBox("26")
        checkBox27 = JCheckBox("27")
        checkBox28 = JCheckBox("28")
        checkBox255 = JCheckBox("255")

        title = "Cache Exporter Poesy700"
        isResizable = false
        val contentPane = contentPane
        contentPane.layout = null

        val btnNewButton = JButton("Select input folder")
        btnNewButton.setBounds(20, 11, 127, 23)
        contentPane.add(btnNewButton)
        btnNewButton.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                selectInput(e)
            }
        })

        val button = JButton("Select output folder")
        button.setBounds(184, 11, 127, 23)
        contentPane.add(button)
        button.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                selectOutput(e)
            }
        })

        val format = arrayOf("", "Old", "New")
        val comboBox: JComboBox<String> = JComboBox(format)
        comboBox.toolTipText = "Choose format"
        comboBox.setBounds(184, 59, 127, 20)
        contentPane.add(comboBox)
        comboBox.addActionListener {
            val s = comboBox.selectedItem as String
            when (s) {
                "Old" -> {
                    Utils.format = "old"
                    Main.log(this.javaClass.name, "cache format set to: ${Utils.format}")
                    enableExtractIfReady()
                    enableCheckboxesOld()
                }

                "New" -> {
                    Utils.format = "new"
                    Main.log(this.javaClass.name, "cache format set to: ${Utils.format}")
                    enableExtractIfReady()
                    enableCheckboxesNew()
                }

                else -> {
                    Main.log(this.javaClass.name, "Default")
                    Utils.format = ""
                    btnExtract!!.isEnabled = false
                    enableAllCheckboxes(true)
                }
            }
        }

        val lblCacheFormat = JLabel("Cache Format")
        lblCacheFormat.setBounds(22, 63, 74, 14)
        contentPane.add(lblCacheFormat)

        btnExtract!!.text = "Extract"
        btnExtract!!.isEnabled = false
        btnExtract!!.setBounds(125, 330, 89, 23)
        btnExtract!!.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                extract(e)
            }
        })
        contentPane.add(btnExtract)

        val checkboxes = listOf(
            checkBox, checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8,
            checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16,
            checkBox17, checkBox18, checkBox19, checkBox20, checkBox21, checkBox22, checkBox23, checkBox24,
            checkBox25, checkBox26, checkBox27, checkBox28, checkBox255
        )
        val positions = listOf(
            Pair(20, 126), Pair(20, 156), Pair(20, 186), Pair(20, 216),
            Pair(65, 126), Pair(65, 156), Pair(65, 186), Pair(65, 216),
            Pair(110, 126), Pair(110, 156), Pair(110, 186), Pair(110, 216),
            Pair(155, 126), Pair(155, 156), Pair(155, 186), Pair(155, 216),
            Pair(200, 126), Pair(200, 156), Pair(200, 186), Pair(200, 216),
            Pair(245, 126), Pair(245, 156), Pair(245, 186), Pair(245, 216),
            Pair(290, 126), Pair(290, 156), Pair(290, 186), Pair(290, 216),
            Pair(20, 246), Pair(20, 276)
        )
        for ((index, cb) in checkboxes.withIndex()) {
            if (cb != null) {
                cb.isEnabled = false
                val (x, y) = positions[index]
                cb.setBounds(x, y + 3, 43, 23)
                cb.addItemListener(CheckBoxListener())
                contentPane.add(cb)
            }
        }

        val lblIndexes = JLabel("Indexes")
        lblIndexes.setBounds(22, 105, 46, 14)
        contentPane.add(lblIndexes)

        lblNewLabel = JLabel()
        lblNewLabel!!.setBounds(148, 290, 100, 14)
        contentPane.add(lblNewLabel)

        progressBar = JProgressBar()
        progressBar!!.setBounds(20, 310, 300, 20)
        progressBar!!.minimum = 0
        progressBar!!.maximum = 100
        progressBar!!.isStringPainted = true
        contentPane.add(progressBar)
    }

    private fun enableCheckboxesOld() {
        enableAllCheckboxes(false)
        checkBox?.isEnabled = true
        checkBox1?.isEnabled = true
        checkBox2?.isEnabled = true
        checkBox3?.isEnabled = true
        checkBox4?.isEnabled = true
    }

    private fun enableCheckboxesNew() {
        enableAllCheckboxes(true)
    }

    private fun enableAllCheckboxes(enable: Boolean) {
        listOf(
            checkBox, checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8,
            checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16,
            checkBox17, checkBox18, checkBox19, checkBox20, checkBox21, checkBox22, checkBox23, checkBox24,
            checkBox25, checkBox26, checkBox27, checkBox28, checkBox255
        ).forEach { it?.isEnabled = enable }
    }
}
