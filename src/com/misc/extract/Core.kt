package com.misc.extract

import com.cache.store.CacheFile
import com.cache.store.FileOperations
import com.cache.util.Utils
import launcher.Main
import java.io.File
import java.io.RandomAccessFile
import javax.swing.*

object Core {
    var fFound: BooleanArray = BooleanArray(0)
    var fInts: IntArray = IntArray(0)
    var fNames: Array<String?> = arrayOfNulls(0)
    var InputDir: String = "./obj/"
    var OutputDir: String = "./out/"
    var Cache: Array<CacheFile?>? = null
    var CacheType: Array<String?>? = null
    var CacheExt: Array<String?>? = null
    var CacheData: RandomAccessFile? = null
    var cacheVersion: String = ""
    var wantSpritesExtracted: Boolean = false
    var nameFile: String? = Utils.nameFile

    var progressBar: JProgressBar? = null

    @JvmStatic
    fun start() {
        processConfiguration()
        if (nameFile != null) processNames(FileOperations.ReadFileText("./data/names.dat"))
        try {
            ExtractCacheFiles()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun ExtractCacheFiles() {
        Main.log(this.javaClass.name, "Extracting Cache Files...")

        val totalFiles = Cache?.indices
            ?.filter { Utils.toExtract[it] }
            ?.sumOf { Cache?.get(it)?.numberOfFiles?.toLong() ?: 0L } ?: 0L

        if (totalFiles == 0L) {
            Main.log(this.javaClass.name, "No files to extract.")
            return
        }

        progressBar?.maximum = totalFiles.toInt()
        progressBar?.value = 0

        var extractedFiles = 0L

        for (i in Cache?.indices!!) {
            if (Utils.toExtract[i]) {
                if (Cache?.get(i) == null) {
                    Main.log(this.javaClass.name, "Cache is null skipped: index $i")
                    break
                }
                Main.log(
                    this.javaClass.name,
                    " - Extracting Expected " + Cache!![i]!!.numberOfFiles + " in Cache " + i + " \"" + CacheType!![i] + "\"...",
                )
                FileOperations.TotalWrite = 0
                val numberOfFiles = Cache!![i]!!.numberOfFiles

                for (j in 0 until numberOfFiles) {
                    val localByteStream = Cache!![i]!!.getFile(j)
                    if (localByteStream != null) {
                        val arrayOfByte: ByteArray
                        if (cacheVersion == "new") {
                            arrayOfByte = Unpack.unpackSecondMethod(localByteStream)
                            FileOperations.WriteFile(
                                OutputDir + CacheType!![i] + "/" + j + "." + CacheExt!![i]!!,
                                arrayOfByte,
                            )
                        } else {
                            arrayOfByte = Unpack.unpack(localByteStream, OutputDir + CacheType!![i] + "/" + j)
                            FileOperations.WriteFile(
                                OutputDir + CacheType!![i] + "/" + j + "." + CacheExt!![i],
                                arrayOfByte,
                            )
                        }
                    } else {
                        Main.log(this.javaClass.name, "Stream is null")
                    }

                    extractedFiles++
                    updateProgress(extractedFiles.toInt())
                }
                Main.log(
                    this.javaClass.name,
                    "  - " + FileOperations.TotalWrite + " Files Extracted to /" + CacheType!![i] + "/*." + CacheExt!![i] + ".",
                )
            }
        }

        Main.log(this.javaClass.name, "Successfully extracted.")
    }

    private fun updateProgress(value: Int) {
        progressBar?.let {
            SwingUtilities.invokeLater {
                it.value = value
                it.string = "$value / ${it.maximum} files extracted (${(value * 100) / it.maximum}%)"
                it.isStringPainted = true
            }
        }
    }

    @JvmStatic
    fun checkNames(paramInt: Int): String? {
        for (i in fInts.indices) {
            if (fInts[i] == paramInt) {
                fFound[i] = true
                return fNames[i]
            }
        }
        Main.log(this.javaClass.name, "Could Not Find Name for: $paramInt")
        return "$paramInt.DAT"
    }

    fun processNames(paramString: String) {
        Main.log(this.javaClass.name, "Parsing Names...")
        fNames = paramString.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        fFound = BooleanArray(fNames.size)
        val arrayOfInt = IntArray(fNames.size)
        for (i in fNames.indices) {
            if (!fNames[i]!!.startsWith("#")) {
                val arrayOfString = fNames[i]!!.split(",").toTypedArray()
                if (arrayOfString.size >= 2 && arrayOfString[1].isNotBlank()) {
                    try {
                        arrayOfInt[i] = arrayOfString[0].toInt()
                    } catch (e: NumberFormatException) {
                        Main.log(this.javaClass.name, "Invalid integer at line $i: '${arrayOfString[0]}'")
                        arrayOfInt[i] = -1
                    }
                    fNames[i] = arrayOfString[1].trim()
                } else {
                    Main.log(this.javaClass.name, "Invalid name entry (missing comma or second part) at line $i: '${fNames[i]}'")
                    arrayOfInt[i] = -1
                    fNames[i] = "Unknown"
                }
            } else {
                arrayOfInt[i] = -1
                fNames[i] = "Unknown"
            }
        }
        fInts = arrayOfInt
        Main.log(this.javaClass.name, " - " + fNames.size + " Names read.")
    }

    fun processConfiguration() {
        try {
            Main.log(this.javaClass.name, "Parsing configuration...")
            nameFile = Utils.nameFile
            cacheVersion = Utils.format
            InputDir = Utils.inputFolder
            OutputDir = Utils.outputFolder

            val idxFiles = File(InputDir).listFiles { _, name -> name.startsWith("main_file_cache.idx") }
            val maxIndex = idxFiles?.mapNotNull {
                val match = Regex("main_file_cache\\.idx(\\d+)").find(it.name)
                match?.groupValues?.get(1)?.toIntOrNull()
            }?.maxOrNull() ?: -1

            if (maxIndex == -1) {
                Main.log(this.javaClass.name, "No cache index files found.")
                return
            }

            Utils.toExtract = BooleanArray(maxIndex + 1) { false }

            idxFiles.forEach {
                val match = Regex("main_file_cache\\.idx(\\d+)").find(it.name)
                val idx = match?.groupValues?.get(1)?.toIntOrNull()
                if (idx != null) {
                    Utils.toExtract[idx] = true
                }
            }

            if (cacheVersion == "old") {
                Cache = arrayOfNulls(5)
                CacheType = arrayOfNulls(5)
                CacheExt = arrayOfNulls(5)
                CacheData = RandomAccessFile("$InputDir/main_file_cache.dat", "rw")
            } else {
                Cache = arrayOfNulls(Utils.toExtract.size)
                CacheType = arrayOfNulls(Utils.toExtract.size)
                CacheExt = arrayOfNulls(Utils.toExtract.size)
                CacheData = RandomAccessFile("$InputDir/main_file_cache.dat2", "rw")
            }

            for (i in Utils.toExtract.indices) {
                if (Utils.toExtract[i]) {
                    val idxFile = File("$InputDir/main_file_cache.idx$i")
                    if (!idxFile.exists()) {
                        Main.log(this.javaClass.name, "Index file does not exist: $i")
                        continue
                    }
                    Cache!![i] = CacheFile(FileOperations.ReadFile(idxFile.absolutePath)!!, CacheData!!)
                    CacheType!![i] = "/$i"
                    CacheExt!![i] = "dat"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
