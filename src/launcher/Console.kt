package launcher

import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.io.OutputStream
import java.io.PrintStream
import javax.swing.*
import kotlin.system.exitProcess

/**
 * The type Console.
 */
class Console : JFrame() {
    /**
     * Instantiates a new Console.
     */
    init {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this.title = "Console"
        this.isResizable = false
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.initComponents()
        Main.log("Console", "Console Started.")
    }

    private fun initComponents() {
        val jScrollPane1 = JScrollPane()
        output = JTextArea()
        val jMenuBar1 = JMenuBar()
        val jMenu1 = JMenu()
        val jMenuItem1 = JMenuItem()
        val jMenuItem2 = JMenuItem()
        this.defaultCloseOperation = EXIT_ON_CLOSE
        output!!.isEditable = false
        output!!.columns = 20
        output!!.lineWrap = true
        output!!.rows = 5
        output!!.margin.left = 2
        output!!.font = output!!.font.deriveFont(11f)
        jScrollPane1.setViewportView(output)
        jMenu1.text = "File"
        jMenuItem1.text = "Close Console"
        jMenuItem1.addActionListener { evt: ActionEvent -> this@Console.jMenuItem1ActionPerformed(evt) }
        jMenu1.add(jMenuItem1)
        jMenuItem2.text = "Exit Program"
        jMenuItem2.addActionListener { evt: ActionEvent -> this@Console.jMenuItem2ActionPerformed(evt) }
        jMenu1.add(jMenuItem2)
        jMenuBar1.add(jMenu1)
        this.jMenuBar = jMenuBar1
        val layout = GroupLayout(this.contentPane)
        this.contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, -1, 618, 32767),
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, -1, 240, 32767),
        )
        this.pack()
    }

    private fun jMenuItem2ActionPerformed(evt: ActionEvent) {
        exitProcess(0)
    }

    private fun jMenuItem1ActionPerformed(evt: ActionEvent) {
        this.dispose()
    }

    companion object {
        private const val serialVersionUID = -5782108221111094876L

        /**
         * The constant output.
         */
        var output: JTextArea? = null

        /**
         * The entry point of application.
         *
         * @param args the input arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater { (Console()).isVisible = true }
        }

        private fun updateTextArea(text: String) {
            SwingUtilities.invokeLater { output!!.append(text) }
        }

        /**
         * Redirect system streams.
         */
        @JvmStatic
        fun redirectSystemStreams() {
            val out: OutputStream =
                object : OutputStream() {
                    override fun write(b: Int) {
                        updateTextArea(b.toChar().toString())
                    }

                    override fun write(
                        b: ByteArray,
                        off: Int,
                        len: Int,
                    ) {
                        updateTextArea(String(b, off, len))
                    }

                    override fun write(b: ByteArray) {
                        this.write(b, 0, b.size)
                    }
                }
            System.setOut(PrintStream(out, true))
            System.setErr(PrintStream(out, true))
        }
    }
}
