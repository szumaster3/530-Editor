package launcher

import java.awt.EventQueue
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * The Main.
 */
object Main {
    private const val APP_VERSION = "1.0.3a"
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    @JvmStatic
    fun main(args: Array<String>) {
        println("Application version: $APP_VERSION")
        Console.redirectSystemStreams()
        EventQueue.invokeLater {
            val console = Console()
            console.isVisible = true

            val toolSelection = ToolSelection()

            val consoleBounds = console.bounds

            val toolSize = toolSelection.size

            toolSelection.setLocation(consoleBounds.x - toolSize.width, consoleBounds.y)
            toolSelection.isVisible = true
        }
    }

    /**
     * Log.
     *
     * @param className the class name
     * @param message   the message
     */
    @JvmStatic
    fun log(
        className: String,
        message: String,
    ) {
        println("[$className]: $message")
        printDebug(className, message)
    }

    private fun printDebug(
        className: String,
        message: String,
    ) {
        val f = File("logs.txt")

        try {
            if (!f.exists()) {
                f.createNewFile()
            }

            val strFilePath = f.absolutePath

            FileOutputStream(strFilePath, true).use { var9 ->
                val strContent = Date().toString() + ": [" + className + "]: " + message
                val lineSep = System.getProperty("line.separator")
                var9.write(strContent.toByteArray())
                var9.write(lineSep.toByteArray())
            }
        } catch (var8: IOException) {
            log("Main", "IOException: " + var8.message)
        }
    }
}
