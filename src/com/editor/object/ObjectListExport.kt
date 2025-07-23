package com.editor.`object`

import com.cache.defs.ObjectDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getObjectDefinitionsSize
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ObjectListExport(
    cache: String,
) {
    private val cache: Cache = Cache(cache)

    init {
        val directory = File("data/export/lists/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "object_list.txt")

        try {
            file.createNewFile()

            BufferedWriter(FileWriter(file)).use { writer ->
                writer.appendLine("Id - name")

                for (id in 0 until getObjectDefinitionsSize(this.cache)) {
                    val def = ObjectDefinition.getObjectDefinition(this.cache, id)
                    val itemInfo = "$id - ${def.name}"
                    writer.appendLine(itemInfo)
                    println(itemInfo)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
