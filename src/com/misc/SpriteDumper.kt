package com.misc

import com.displee.cache.CacheLibrary
import core.cache.CacheIndex
import openrs.cache.sprite.Sprite
import openrs.util.ImageUtils
import java.awt.Color
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object SpriteDumper {

    /**
     * Dumps sprites to selected directory.
     *
     * @param cache path to cache.
     * @param o folder to save sprites.
     * @throws IOException in case of IO errors.
     */
    fun dumpSprites(cache: String, o: File) {
        if (!o.exists()) {
            o.mkdirs()
        }

        val lib = CacheLibrary.create(cache)
        val id = CacheIndex.SPRITES
        val index = lib.index(id.id) ?: throw IOException("Index $id not found")

        val archiveIds = index.archiveIds()

        for ((i, archiveId) in archiveIds.withIndex()) {
            val data = lib.data(id.id, archiveId) ?: continue
            val buffer = java.nio.ByteBuffer.wrap(data)
            val s = Sprite.decode(buffer)

            for (f in 0 until s.size()) {
                val frameImage = s.getFrame(f)
                //                val transparent = ImageUtils.makeColorTransparent(frameImage, Color.WHITE)
                //                val image = ImageUtils.createColoredBackground(transparent, Color(0xFF00FF, false))
                val transparent = ImageUtils.makeColorTransparent(frameImage, Color.WHITE)

                val outFile = File(o, "${archiveId}_frame${f}.png")
                ImageIO.write(transparent, "png", outFile)
            }

            val progress = (i + 1).toDouble() / archiveIds.size * 100
            println("$archiveId out of ${archiveIds.size} %.2f%%".format(progress))
        }
    }

}
