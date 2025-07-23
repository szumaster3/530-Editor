package com.misc

import java.awt.*
import java.awt.datatransfer.StringSelection
import javax.swing.*

class ColorPicker : JFrame() {
    private val colorPreview: JPanel
    private var selectedColor: Color = Color.WHITE
    private val statusLabel: JLabel = JLabel()
    private var rs2hsb: Int = rgbToRS2HSB(selectedColor.red, selectedColor.green, selectedColor.blue)

    init {
        title = "Color Picker"
        setSize(200, 250)
        defaultCloseOperation = 1
        layout = FlowLayout(FlowLayout.CENTER)
        isResizable = false
        setLocationRelativeTo(null)

        colorPreview =
            object : JPanel() {
                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    g.color = selectedColor
                    g.fillRect(0, 0, width, height)

                    val luminance =
                        0.2126 * selectedColor.red + 0.7152 * selectedColor.green + 0.0722 * selectedColor.blue
                    val textColor = if (luminance < 128) Color.WHITE else Color.BLACK

                    val g2d = g as Graphics2D
                    g2d.color = textColor
                    g2d.font = g2d.font.deriveFont(12f).deriveFont(Font.BOLD)
                    val text = "RS2: $rs2hsb"

                    val fontMetrics = g2d.fontMetrics
                    val x = (width - fontMetrics.stringWidth(text)) / 2
                    val y = (height + fontMetrics.height) / 2
                    g2d.drawString(text, x, y)
                }
            }
        colorPreview.preferredSize = Dimension(175, 85)
        add(colorPreview)

        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 10)
        add(buttonPanel)

        val pickColorButton = JButton("\uD83C\uDFA8")
        pickColorButton.isFocusPainted = false
        pickColorButton.preferredSize = Dimension(45, 45)
        pickColorButton.addActionListener {
            val colorChooser = JColorChooser(selectedColor)
            val dialog = JColorChooser.createDialog(this, "Select a Color", true, colorChooser, null, null)

            colorChooser.selectionModel.addChangeListener {
                selectedColor = colorChooser.color
                rs2hsb = rgbToRS2HSB(selectedColor.red, selectedColor.green, selectedColor.blue)
                colorPreview.repaint()
            }

            dialog.isVisible = true
        }
        buttonPanel.add(pickColorButton)

        val copyButton = JButton("\uD83D\uDCCB")
        copyButton.isFocusPainted = false
        copyButton.preferredSize = Dimension(45, 45)
        copyButton.addActionListener {
            copyToClipboard(rs2hsb.toString())
            displayCopiedMessage()
        }
        buttonPanel.add(copyButton)

        val convertButton = JButton("\uD83C\uDF6D")
        convertButton.isFocusPainted = false
        convertButton.preferredSize = Dimension(45, 45)
        convertButton.addActionListener {
            val rgb = rs2hsbToRGB(rs2hsb)
            statusLabel.text = "RGB: $rgb"
            copyToClipboard(rgb.toString())
        }
        buttonPanel.add(convertButton)

        statusLabel.preferredSize = Dimension(175, 30)
        statusLabel.horizontalAlignment = SwingConstants.CENTER
        add(statusLabel)
    }

    private fun copyToClipboard(text: String) {
        val selection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)
    }

    private fun displayCopiedMessage() {
        statusLabel.text = "Copied to Clipboard."
        Timer(2000) {
            statusLabel.text = ""
        }.start()
    }

    /**
     * Converts RGB values to the RS2 HSB format.
     *
     * This function takes RGB values (red, green, blue), converts them to the HSB (Hue, Saturation, Brightness)
     * representation, and then encodes them into an integer format compatible with the RS2 game engine.
     *
     * Author: [BNormal](https://github.com/BNormal)
     *
     * @param red The red component of the color (0-255).
     * @param green The green component of the color (0-255).
     * @param blue The blue component of the color (0-255).
     * @return An integer representing the color in RS2 HSB format, combining hue, saturation, and brightness.
     */
    private fun rgbToRS2HSB(
        red: Int,
        green: Int,
        blue: Int,
    ): Int {
        val HSB = Color.RGBtoHSB(red, green, blue, null)
        val encode_hue = (HSB[0] * 63).toInt()
        val encode_saturation = (HSB[1] * 7).toInt()
        val encode_brightness = (HSB[2] * 127).toInt()
        return (encode_hue shl 10) + (encode_saturation shl 7) + encode_brightness
    }

    private fun rs2hsbToRGB(RS2HSB: Int): Int {
        val decode_hue = RS2HSB shr 10 and 0x3F
        val decode_saturation = RS2HSB shr 7 and 0x7
        val decode_brightness = RS2HSB and 0x7F
        return Color.HSBtoRGB(decode_hue / 63.0f, decode_saturation / 7.0f, decode_brightness / 127.0f)
    }
}
