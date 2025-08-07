package com.misc

import com.displee.cache.CacheLibrary
import core.cache.CacheIndex
import openrs.cache.sprite.Sprite
import openrs.util.ImageUtils
import java.awt.*
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.TitledBorder

class SpriteManager(private val cache: String) : JFrame("Sprite Manager") {

    private lateinit var cacheLibrary: CacheLibrary

    private val archiveListModel = DefaultListModel<Int>()
    private val archiveList = JList(archiveListModel)

    private val frameListModel = DefaultListModel<Int>()
    private val frameList = JList(frameListModel)

    private val spriteLabel = JLabel().apply {
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.CENTER
        preferredSize = Dimension(300, 300)
        isOpaque = true
        background = Color(240, 240, 240)
        border = TitledBorder("Preview")
    }

    private var currentSprite: Sprite? = null
    private var currentArchiveId: Int? = null

    private var zoom = 1.0

    fun init() {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        setSize(1000, 650)
        setLocationRelativeTo(null)

        val leftPanel = JPanel()
        leftPanel.layout = BoxLayout(leftPanel, BoxLayout.Y_AXIS)

        val archiveScroll = JScrollPane(archiveList)
        archiveScroll.border = TitledBorder("Archive List")
        archiveScroll.maximumSize = Dimension(250, 300)

        val frameScroll = JScrollPane(frameList)
        frameScroll.border = TitledBorder("Frame List")
        frameScroll.maximumSize = Dimension(250, 300)

        leftPanel.add(archiveScroll)
        leftPanel.add(frameScroll)
        add(leftPanel, BorderLayout.WEST)

        val rightPanel = JPanel(BorderLayout())
        val zoomInfoLabel = JLabel("Zoom: Ctrl + Scroll mouse wheel", SwingConstants.CENTER)
        zoomInfoLabel.border = TitledBorder("Info")
        rightPanel.add(zoomInfoLabel, BorderLayout.NORTH)
        val imageScroll = JScrollPane(spriteLabel)
        rightPanel.add(imageScroll, BorderLayout.CENTER)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        val replaceButton = JButton("Replace Frame")
        val dumpFrameButton = JButton("Dump Frame")
        val dumpAllFromArchiveButton = JButton("Dump All Frames")
        val exportAllButton = JButton("Export All")
        val addFrameButton = JButton("Add Frame")

        buttonPanel.add(replaceButton)
        buttonPanel.add(dumpFrameButton)
        buttonPanel.add(dumpAllFromArchiveButton)
        buttonPanel.add(exportAllButton)
        buttonPanel.add(addFrameButton)

        rightPanel.add(buttonPanel, BorderLayout.SOUTH)

        add(rightPanel, BorderLayout.CENTER)

        archiveList.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                val archiveId = archiveList.selectedValue ?: return@addListSelectionListener
                loadFrames(archiveId)
            }
        }

        frameList.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                val frameIndex = frameList.selectedValue ?: return@addListSelectionListener
                zoom = 1.0
                showFrame(frameIndex)
            }
        }

        replaceButton.addActionListener {
            val archiveId = archiveList.selectedValue ?: return@addActionListener
            val frameIndex = frameList.selectedValue ?: return@addActionListener
            val pngFile = selectImageFile() ?: return@addActionListener
            replaceFrame(archiveId, frameIndex, pngFile)
        }

        dumpFrameButton.addActionListener {
            val archiveId = archiveList.selectedValue ?: return@addActionListener
            val frameIndex = frameList.selectedValue ?: return@addActionListener
            val outputDir = selectOutputDir() ?: return@addActionListener
            dumpSingleFrame(archiveId, frameIndex, outputDir)
        }

        dumpAllFromArchiveButton.addActionListener {
            val archiveId = archiveList.selectedValue ?: return@addActionListener
            val outputDir = selectOutputDir() ?: return@addActionListener
            dumpAllFrames(archiveId, outputDir)
        }

        exportAllButton.addActionListener {
            val outputDir = selectOutputDir() ?: return@addActionListener
            exportAllArchives(outputDir)
        }

        addFrameButton.addActionListener {
            val archiveId = archiveList.selectedValue ?: return@addActionListener
            val pngFile = selectImageFile() ?: return@addActionListener
            addFrame(archiveId, pngFile)
        }

        spriteLabel.addMouseWheelListener(object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                if (e.isControlDown) {
                    val rotation = e.wheelRotation
                    if (rotation < 0) {
                        zoom *= 1.1
                        if (zoom > 5.0) zoom = 5.0
                    } else {
                        zoom /= 1.1
                        if (zoom < 0.1) zoom = 0.1
                    }
                    val frameIndex = frameList.selectedValue ?: return
                    showFrame(frameIndex)
                    e.consume()
                }
            }
        })

        loadCache(File(cache))
        isVisible = true
    }

    private fun loadCache(cacheDir: File) {
        if (!cacheDir.exists() || !cacheDir.isDirectory) {
            JOptionPane.showMessageDialog(this, "Cache directory not found: ${cacheDir.absolutePath}", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        archiveListModel.clear()
        cacheLibrary = CacheLibrary.create(cacheDir.absolutePath)
        val index = cacheLibrary.index(CacheIndex.SPRITES.id) ?: return
        val archiveIds = index.archiveIds()
        archiveIds.forEach { archiveListModel.addElement(it) }

        JOptionPane.showMessageDialog(this, "Loaded ${archiveIds.size} sprite archives.")
    }

    private fun loadFrames(archiveId: Int) {
        frameListModel.clear()
        try {
            val data = cacheLibrary.data(CacheIndex.SPRITES.id, archiveId) ?: return
            val sprite = Sprite.decode(ByteBuffer.wrap(data))
            currentSprite = sprite
            currentArchiveId = archiveId
            for (i in 0 until sprite.size()) {
                frameListModel.addElement(i)
            }
            frameList.selectedIndex = 0
        } catch (e: Exception) {
            e.printStackTrace()
            currentSprite = null
        }
    }

    private fun showFrame(frameIndex: Int) {
        val sprite = currentSprite ?: return
        try {
            val frame = sprite.getFrame(frameIndex)
            val transparent = ImageUtils.makeColorTransparent(frame, Color.WHITE)
            val scaled = scaleImage(transparent, zoom)
            spriteLabel.icon = ImageIcon(scaled)
            spriteLabel.revalidate()
            spriteLabel.repaint()
        } catch (e: Exception) {
            e.printStackTrace()
            spriteLabel.icon = null
        }
    }

    private fun scaleImage(img: Image, scale: Double): Image {
        val width = (img.getWidth(null) * scale).toInt()
        val height = (img.getHeight(null) * scale).toInt()
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH)
    }

    private fun replaceFrame(archiveId: Int, frameIndex: Int, pngFile: File) {
        try {
            val image = ImageIO.read(pngFile) ?: return
            currentSprite?.setFrame(frameIndex, image)
            val newData = currentSprite?.encode() ?: return
            cacheLibrary.put(CacheIndex.SPRITES.id, archiveId, newData.array(), null)
            JOptionPane.showMessageDialog(this, "Frame replaced successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(this, "Failed to replace frame: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun dumpSingleFrame(archiveId: Int, frameIndex: Int, outputDir: File) {
        try {
            val sprite = currentSprite ?: return
            val frame = sprite.getFrame(frameIndex)
            val transparent = ImageUtils.makeColorTransparent(frame, Color.WHITE)
            val outFile = File(outputDir, "${archiveId}_frame$frameIndex.png")
            ImageIO.write(transparent, "png", outFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dumpAllFrames(archiveId: Int, outputDir: File) {
        try {
            val sprite = currentSprite ?: return
            for (i in 0 until sprite.size()) {
                val frame = sprite.getFrame(i)
                val transparent = ImageUtils.makeColorTransparent(frame, Color.WHITE)
                val outFile = File(outputDir, "${archiveId}_frame$i.png")
                ImageIO.write(transparent, "png", outFile)
            }
            JOptionPane.showMessageDialog(this, "All frames dumped for archive $archiveId.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exportAllArchives(outputDir: File) {
        for (archiveId in archiveListModel.elements()) {
            try {
                val data = cacheLibrary.data(CacheIndex.SPRITES.id, archiveId) ?: continue
                val sprite = Sprite.decode(ByteBuffer.wrap(data))
                for (i in 0 until sprite.size()) {
                    val frame = sprite.getFrame(i)
                    val transparent = ImageUtils.makeColorTransparent(frame, Color.WHITE)
                    val outFile = File(outputDir, "${archiveId}_frame$i.png")
                    ImageIO.write(transparent, "png", outFile)
                }
                println("Exported archive $archiveId")
            } catch (e: Exception) {
                println("Failed to export archive $archiveId: ${e.message}")
            }
        }
        JOptionPane.showMessageDialog(this, "Exported all archives.")
    }

    private fun selectImageFile(): File? {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Select PNG image"
        chooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png")
        val result = chooser.showOpenDialog(this)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
    }

    private fun selectOutputDir(): File? {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Select output directory"
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = chooser.showSaveDialog(this)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
    }

    private fun addFrame(archiveId: Int, pngFile: File) {
        try {
            val image = ImageIO.read(pngFile) ?: return
            val oldSprite = currentSprite ?: return

            if (image.width != oldSprite.width || image.height != oldSprite.height) {
                JOptionPane.showMessageDialog(this, "New frame must be ${oldSprite.width}x${oldSprite.height} pixels.", "Error", JOptionPane.ERROR_MESSAGE)
                return
            }

            val newSprite = Sprite(oldSprite.width, oldSprite.height, oldSprite.size() + 1)

            for (i in 0 until oldSprite.size()) {
                newSprite.setFrame(i, oldSprite.getFrame(i))
            }

            newSprite.setFrame(newSprite.size() - 1, image)

            currentSprite = newSprite
            currentArchiveId = archiveId

            val newData = newSprite.encode() ?: return
            cacheLibrary.put(CacheIndex.SPRITES.id, archiveId, newData.array(), null)

            loadFrames(archiveId)
            frameList.selectedIndex = newSprite.size() - 1
            zoom = 1.0
            showFrame(newSprite.size() - 1)

            JOptionPane.showMessageDialog(this, "New frame added successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(this, "Failed to add new frame: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }
}
