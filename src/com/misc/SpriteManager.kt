package com.misc

import com.displee.cache.CacheLibrary
import core.cache.CacheIndex
import openrs.cache.sprite.Sprite
import openrs.util.ImageUtils
import java.awt.*
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
        leftPanel.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        val archiveScroll = JScrollPane(archiveList)
        archiveScroll.border = BorderFactory.createTitledBorder("Archive List")
        archiveScroll.viewportBorder = null
        archiveScroll.maximumSize = Dimension(280, 300)

        val frameScroll = JScrollPane(frameList)
        frameScroll.border = BorderFactory.createTitledBorder("Frame List")
        frameScroll.viewportBorder = null
        frameScroll.maximumSize = Dimension(280, 300)

        leftPanel.add(archiveScroll)
        leftPanel.add(frameScroll)

        val rightPanel = JPanel(BorderLayout())
        rightPanel.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

        val zoomInfoLabel = JLabel("Zoom: Ctrl + Scroll mouse wheel", SwingConstants.CENTER)
        zoomInfoLabel.border = BorderFactory.createTitledBorder("Info")
        rightPanel.add(zoomInfoLabel, BorderLayout.NORTH)

        val imageScroll = JScrollPane(spriteLabel)
        imageScroll.border = null
        imageScroll.viewportBorder = null
        rightPanel.add(imageScroll, BorderLayout.CENTER)

        val buttonPanel = JPanel(GridLayout(2, 4, 5, 5))

        val replaceButton = JButton("Replace Frame")
        val dumpFrameButton = JButton("Dump Frame")
        val dumpAllFromArchiveButton = JButton("Dump All Frames")
        val exportAllButton = JButton("Export All Archives")
        val addFrameButton = JButton("Add Frame")
        val addArchiveButton = JButton("Add Archive")
        val deleteFrameButton = JButton("Delete Frame")
        val removeArchiveButton = JButton("Delete Archive")

        val buttons = listOf(
            replaceButton, dumpFrameButton, dumpAllFromArchiveButton, exportAllButton,
            addFrameButton, addArchiveButton, deleteFrameButton, removeArchiveButton
        )

        buttons.forEach { button ->
            button.preferredSize = Dimension(130, 30)
            buttonPanel.add(button)
        }

        rightPanel.add(buttonPanel, BorderLayout.SOUTH)

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel)
        splitPane.resizeWeight = 0.1
        splitPane.isOneTouchExpandable = true
        splitPane.border = null

        add(splitPane, BorderLayout.CENTER)

        (contentPane as JPanel).border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

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
            addFrameToArchive(archiveId, pngFile)
        }

        addArchiveButton.addActionListener {
            addNewArchiveWithFrame()
        }

        deleteFrameButton.addActionListener {
            val archiveId = archiveList.selectedValue ?: return@addActionListener
            val frameIndex = frameList.selectedValue ?: return@addActionListener
            deleteFrameFromArchive(archiveId, frameIndex)
        }

        removeArchiveButton.addActionListener {
            val archiveId = archiveList.selectedValue
            if (archiveId == null) {
                JOptionPane.showMessageDialog(this, "No archive selected.", "Error", JOptionPane.ERROR_MESSAGE)
                return@addActionListener
            }
            val confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove archive $archiveId?", "Confirm", JOptionPane.YES_NO_OPTION)
            if (confirm == JOptionPane.YES_OPTION) {
                removeArchive(archiveId)
            }
        }

        spriteLabel.addMouseWheelListener { e ->
            if (e.isControlDown) {
                val rotation = e.wheelRotation
                if (rotation < 0) {
                    zoom *= 1.1
                    if (zoom > 5.0) zoom = 5.0
                } else {
                    zoom /= 1.1
                    if (zoom < 0.1) zoom = 0.1
                }
                val frameIndex = frameList.selectedValue ?: return@addMouseWheelListener
                showFrame(frameIndex)
                e.consume()
            }
        }

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
            loadFrames(archiveId)
            frameList.selectedIndex = frameIndex
            showFrame(frameIndex)
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

    private fun addFrameToArchive(archiveId: Int, pngFile: File) {
        try {
            val image = ImageIO.read(pngFile) ?: return
            val oldSprite = currentSprite ?: return

            val newSprite = Sprite(oldSprite.width, oldSprite.height, oldSprite.size() + 1)

            for (i in 0 until oldSprite.size()) {
                newSprite.setFrame(i, oldSprite.getFrame(i))
            }

            newSprite.setFrame(newSprite.size() - 1, image)

            currentSprite = newSprite
            currentArchiveId = archiveId

            val newData = newSprite.encode()
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

    private fun addNewArchiveWithFrame() {
        try {
            val pngFile = selectImageFile() ?: return
            val image = ImageIO.read(pngFile) ?: return

            val newSprite = Sprite(image.width, image.height, 1)
            newSprite.setFrame(0, image)

            val maxArchiveId = archiveListModel.elements().toList().maxOrNull() ?: -1
            val newArchiveId = maxArchiveId + 1

            val newData = newSprite.encode()
            cacheLibrary.put(CacheIndex.SPRITES.id, newArchiveId, newData.array(), null)

            archiveListModel.addElement(newArchiveId)
            archiveList.selectedIndex = archiveListModel.size() - 1

            JOptionPane.showMessageDialog(this, "New archive added with one frame (ID: $newArchiveId).")

            loadFrames(newArchiveId)
            frameList.selectedIndex = 0
            zoom = 1.0
            showFrame(0)
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(this, "Failed to add new archive: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun deleteFrameFromArchive(archiveId: Int, frameIndex: Int) {
        try {
            val oldSprite = currentSprite ?: return
            val frameCount = oldSprite.size()
            if (frameCount <= 1) {
                JOptionPane.showMessageDialog(this, "Cannot delete the last frame.", "Error", JOptionPane.ERROR_MESSAGE)
                return
            }

            val newSprite = Sprite(oldSprite.width, oldSprite.height, frameCount - 1)

            var newIdx = 0
            for (i in 0 until frameCount) {
                if (i == frameIndex) continue
                newSprite.setFrame(newIdx, oldSprite.getFrame(i))
                newIdx++
            }

            currentSprite = newSprite
            currentArchiveId = archiveId

            val newData = newSprite.encode()
            cacheLibrary.put(CacheIndex.SPRITES.id, archiveId, newData.array(), null)

            loadFrames(archiveId)
            frameList.selectedIndex = if (frameIndex >= newSprite.size()) newSprite.size() - 1 else frameIndex
            zoom = 1.0
            showFrame(frameList.selectedValue ?: 0)

            JOptionPane.showMessageDialog(this, "Frame deleted successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(this, "Failed to delete frame: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun removeArchive(archiveId: Int) {
        try {
            cacheLibrary.remove(CacheIndex.SPRITES.id, archiveId)

            archiveListModel.removeElement(archiveId)
            currentSprite = null
            currentArchiveId = null
            frameListModel.clear()
            spriteLabel.icon = null

            JOptionPane.showMessageDialog(this, "Archive $archiveId removed successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(this, "Failed to remove archive: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
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
}
