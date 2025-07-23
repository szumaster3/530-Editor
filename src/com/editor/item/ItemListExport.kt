package com.editor.item

import com.cache.defs.ItemDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getItemDefinitionsSize
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ItemListExport(
    cache: String,
) {
    private val cache: Cache = Cache(cache)

    init {
        val directory = File("data/export/lists/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "item_list.txt")

        try {
            file.createNewFile()

            BufferedWriter(FileWriter(file)).use { writer ->
                writer.appendLine("Id - name")

                for (id in 0 until getItemDefinitionsSize(this.cache)) {
                    val def = ItemDefinition.getItemDefinition(this.cache, id)
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
