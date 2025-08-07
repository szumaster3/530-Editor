package cache.alex.util

import cache.alex.store.Cache
import cache.alex.io.OutputStream
import java.awt.*
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.*
import java.math.BigInteger
import java.util.*
import javax.swing.JProgressBar
import kotlin.math.pow


object Utils {
    @JvmField
    var inputFolder: String = ""

    @JvmField
    var outputFolder: String = ""

    @JvmField
    var format: String = ""

    var nameFile: String = "$inputFolder/names.dat"

    @JvmField
    var toExtract: BooleanArray = BooleanArray(256)

    
    @JvmStatic
    fun cryptRSA(
        data: ByteArray,
        exponent: BigInteger?,
        modulus: BigInteger?,
    ): ByteArray = (BigInteger(data)).modPow(exponent, modulus).toByteArray()

    
    @JvmStatic
    fun getArchivePacketData(
        indexId: Int,
        archiveId: Int,
        archive: ByteArray,
    ): ByteArray {
        val stream = OutputStream(archive.size + 4)
        stream.writeByte(indexId)
        stream.writeShort(archiveId)
        stream.writeByte(0)
        stream.writeInt(archive.size)
        var offset = 8

        for (var6 in archive.indices) {
            if (offset == 512) {
                stream.writeByte(-1)
                offset = 1
            }

            stream.writeByte(archive[var6].toInt())
            ++offset
        }

        val var61 = ByteArray(stream.offset)
        stream.offset = 0
        stream.getBytes(var61, 0, var61.size)
        return var61
    }

    
    @JvmStatic
    fun getNameHash(name: String): Int = name.lowercase(Locale.getDefault()).hashCode()

    
    fun getInterfaceDefinitionsSize(cache: Cache): Int = cache.indexes[3].lastArchiveId + 1

    
    fun getInterfaceDefinitionsComponentsSize(
        cache: Cache,
        interfaceId: Int,
    ): Int = cache.indexes[3].getLastFileId(interfaceId) + 1

    
    fun getAnimationDefinitionsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[20].lastArchiveId
        return lastArchiveId * 128 + cache.indexes[20].getValidFilesCount(lastArchiveId)
    }

    
    @JvmStatic
    fun getItemDefinitionsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[19].lastArchiveId
        return lastArchiveId * 256 + cache.indexes[19].getValidFilesCount(lastArchiveId)
    }

    
    @JvmStatic
    fun getNPCDefinitionsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[18].lastArchiveId
        return lastArchiveId * 128 + cache.indexes[18].getValidFilesCount(lastArchiveId)
    }

    
    @JvmStatic
    fun getObjectDefinitionsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[16].lastArchiveId
        return lastArchiveId * 256 + cache.indexes[16].getValidFilesCount(lastArchiveId)
    }

    
    fun getGraphicDefinitionsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[21].lastArchiveId
        return lastArchiveId * 256 + cache.indexes[21].getValidFilesCount(lastArchiveId)
    }

    @JvmStatic
    fun getClientScriptsSize(cache: Cache): Int {
        val lastArchiveId = cache.indexes[12].lastArchiveId
        return lastArchiveId
    }

    @JvmStatic
    var hsl2rgb: IntArray? = null

    
    @JvmStatic
    @Throws(IOException::class)
    fun getBytesFromFile(file: File): ByteArray {
        FileInputStream(file).use { inputStream ->

            val length = file.length()


            if (length > Int.MAX_VALUE) {
                throw IOException("File is too large to load into memory")
            }

            val bytes = ByteArray(length.toInt())
            var offset = 0


            var numRead: Int
            while (offset < bytes.size) {
                numRead = inputStream.read(bytes, offset, bytes.size - offset)
                if (numRead == -1) {
                    break
                }
                offset += numRead
            }


            if (offset < bytes.size) {
                throw IOException("Could not completely read file ${file.name}")
            }

            return bytes
        }
    }

    fun setColors() {
        hsl2rgb = IntArray(65536)
        val out1 = hsl2rgb
        val d = 0.7
        var i = 0

        for (i1 in 0..511) {
            val f = ((i1 shr 3).toFloat() / 64.0f + 0.0078125f) * 360.0f
            val f1 = 0.0625f + (7 and i1).toFloat() / 8.0f

            for (i2 in 0..127) {
                val f2 = i2.toFloat() / 128.0f
                var f3 = 0.0f
                var f4 = 0.0f
                var f5 = 0.0f
                val f6 = f / 60.0f
                val i3 = f6.toInt()
                val i4 = i3 % 6
                val f7 = f6 - i3.toFloat()
                val f8 = f2 * (-f1 + 1.0f)
                val f9 = f2 * (-(f7 * f1) + 1.0f)
                val f10 = (1.0f - f1 * (-f7 + 1.0f)) * f2
                if (i4 == 0) {
                    f3 = f2
                    f5 = f8
                    f4 = f10
                } else if (i4 == 1) {
                    f5 = f8
                    f3 = f9
                    f4 = f2
                } else if (i4 == 2) {
                    f3 = f8
                    f4 = f2
                    f5 = f10
                } else if (i4 == 3) {
                    f4 = f9
                    f3 = f8
                    f5 = f2
                } else if (i4 == 4) {
                    f5 = f2
                    f3 = f10
                    f4 = f8
                } else {
                    f4 = f8
                    f5 = f9
                    f3 = f2
                }

                out1?.set(
                    i++,
                    (
                            (f3.toDouble().pow(d).toFloat() * 256.0f).toInt() shl 16 or (
                                    (
                                            f4
                                                .toDouble()
                                                .pow(d)
                                                .toFloat() * 256.0f
                                            ).toInt() shl 8
                                    ) or (f5.toDouble().pow(d).toFloat() * 256.0f).toInt()
                            ),
                )
            }
        }
    }

    fun copyToClipboard(color: String?) {
        val selection = StringSelection(color)
        val clip: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clip.setContents(selection, null)
    }

    fun getContrastColor(color: Color): Color {
        val y = ((299 * color.red + 587 * color.green + 114 * color.blue) / 1000).toDouble()
        return if (y >= 128) Color.black else Color.white
    }

    fun RGB_to_RS2HSB(
        red: Int,
        green: Int,
        blue: Int,
    ): Int {
        val HSB = Color.RGBtoHSB(red, green, blue, null)
        val hue = HSB[0]
        val saturation = HSB[1]
        val brightness = HSB[2]
        val encode_hue = (hue * 63.0f).toInt()
        val encode_saturation = (saturation * 7.0f).toInt()
        val encode_brightness = (brightness * 127.0f).toInt()
        return (encode_hue shl 10) + (encode_saturation shl 7) + encode_brightness
    }

    fun RS2HSB_to_RGB(RS2HSB: Int): Int {
        val decode_hue = RS2HSB shr 10 and 0x3F
        val decode_saturation = RS2HSB shr 7 and 0x7
        val decode_brightness = RS2HSB and 0x7F
        return Color.HSBtoRGB(decode_hue / 63.0f, decode_saturation / 7.0f, decode_brightness / 127.0f)
    }

    
    @JvmStatic
    fun packCustomModel(
        cache: Cache,
        data: ByteArray?,
    ): Int {
        val archiveId = cache.indexes[7].lastArchiveId + 1
        return if (cache.indexes[7].putFile(archiveId, 0, data)) {
            archiveId
        } else {
            -1
        }
    }

    
    @JvmStatic
    fun packCustomModel(
        cache: Cache,
        data: ByteArray?,
        modelId: Int,
    ): Int =
        if (cache.indexes[7].putFile(modelId, 0, data)) {
            modelId
        } else {
            -1
        }

    @JvmStatic
    fun getTextureDiffuseSize(cache: Cache): Int = cache.indexes[9].lastArchiveId

    @JvmStatic
    fun getSpriteDefinitionSize(cache: Cache): Int = cache.indexes[8].lastArchiveId

    @JvmStatic
    fun getParticleConfigSize(cache: Cache): Int = cache.indexes[27].getLastFileId(0) + 1

    @JvmStatic
    fun getMagnetConfigSize(cache: Cache): Int = cache.indexes[27].getLastFileId(1) + 1

    @JvmStatic
    fun getConfigArchive(
        id: Int,
        bits: Int,
    ): Int = (id) shr bits

    @JvmStatic
    fun getConfigFile(
        id: Int,
        bits: Int,
    ): Int = (id) and (1 shl bits) - 1

    class ProgressBar : JProgressBar() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2d = g as Graphics2D
            g2d.color = Color.WHITE
            val fontMetrics = g2d.fontMetrics
            val progressText = "${getPercentComplete() * 100}%"
            val x = (width - fontMetrics.stringWidth(progressText)) / 2
            val y = (height + fontMetrics.ascent - fontMetrics.descent) / 2
            preferredSize = Dimension(width, fontMetrics.height + 2)
            g2d.drawString(progressText, x, y)
        }
    }

}
