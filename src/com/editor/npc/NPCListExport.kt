package com.editor.npc

import com.cache.defs.NPCDefinition
import com.cache.store.Cache
import com.cache.util.Utils.getNPCDefinitionsSize
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class NPCListExport(
    cache: String,
) {
    private val cache: Cache = Cache(cache)

    init {
        val directory = File("data/export/lists/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "npc_list.txt")

        try {
            file.createNewFile()

            BufferedWriter(FileWriter(file)).use { writer ->
                writer.appendLine("Id - name")

                for (id in 0 until getNPCDefinitionsSize(this.cache)) {
                    val def = NPCDefinition.getNPCDefinition(this.cache, id)
                    //val npcInfo = "const val ${def.name.capitalize()}_$id = $id"
                    val npcInfo = "$id - ${def.name}"
                    writer.appendLine(npcInfo)
                    println(npcInfo)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
