package cache.alex.util

import java.awt.Component
import java.awt.Container
import javax.swing.Spring
import javax.swing.SpringLayout




object SpringUtilities {
    
    @JvmStatic
    fun printSizes(c: Component) {
        println("minimumSize = " + c.minimumSize)
        println("preferredSize = " + c.preferredSize)
        println("maximumSize = " + c.maximumSize)
    }

    
    @JvmStatic
    fun makeGrid(
        parent: Container,
        rows: Int,
        cols: Int,
        initialX: Int,
        initialY: Int,
        xPad: Int,
        yPad: Int,
    ) {
        val layout: SpringLayout
        try {
            layout = parent.layout as SpringLayout
        } catch (exc: ClassCastException) {
            System.err.println("The first argument to makeGrid must use SpringLayout.")
            return
        }

        val xPadSpring = Spring.constant(xPad)
        val yPadSpring = Spring.constant(yPad)
        val initialXSpring = Spring.constant(initialX)
        val initialYSpring = Spring.constant(initialY)
        val max = rows * cols



        var maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth()

        var maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getHeight()

        for (i in 1 until max) {
            val cons =
                layout.getConstraints(
                    parent.getComponent(i),
                )

            maxWidthSpring = Spring.max(maxWidthSpring, cons.width)
            maxHeightSpring = Spring.max(maxHeightSpring, cons.height)
        }



        for (i in 0 until max) {
            val cons =
                layout.getConstraints(
                    parent.getComponent(i),
                )

            cons.width = maxWidthSpring
            cons.height = maxHeightSpring
        }



        var lastCons: SpringLayout.Constraints? = null
        var lastRowCons: SpringLayout.Constraints? = null
        for (i in 0 until max) {
            val cons =
                layout.getConstraints(
                    parent.getComponent(i),
                )
            if (i % cols == 0) {
                lastRowCons = lastCons
                cons.x = initialXSpring
            } else {
                cons.x =
                    Spring.sum(
                        lastCons!!.getConstraint(SpringLayout.EAST),
                        xPadSpring,
                    )
            }

            if (i / cols == 0) {
                cons.y = initialYSpring
            } else {
                cons.y =
                    Spring.sum(
                        lastRowCons!!.getConstraint(SpringLayout.SOUTH),
                        yPadSpring,
                    )
            }
            lastCons = cons
        }


        val pCons = layout.getConstraints(parent)
        pCons.setConstraint(
            SpringLayout.SOUTH,
            Spring.sum(
                Spring.constant(yPad),
                lastCons!!.getConstraint(SpringLayout.SOUTH),
            ),
        )
        pCons.setConstraint(
            SpringLayout.EAST,
            Spring.sum(
                Spring.constant(xPad),
                lastCons.getConstraint(SpringLayout.EAST),
            ),
        )
    }


    private fun getConstraintsForCell(
        row: Int,
        col: Int,
        parent: Container,
        cols: Int,
    ): SpringLayout.Constraints {
        val layout = parent.layout as SpringLayout
        val c = parent.getComponent(row * cols + col)
        return layout.getConstraints(c)
    }

    
    @JvmStatic
    fun makeCompactGrid(
        parent: Container,
        rows: Int,
        cols: Int,
        initialX: Int,
        initialY: Int,
        xPad: Int,
        yPad: Int,
    ) {
        val layout: SpringLayout
        try {
            layout = parent.layout as SpringLayout
        } catch (exc: ClassCastException) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.")
            return
        }


        var x = Spring.constant(initialX)
        for (c in 0 until cols) {
            var width = Spring.constant(0)
            for (r in 0 until rows) {
                width =
                    Spring.max(
                        width,
                        getConstraintsForCell(r, c, parent, cols).width,
                    )
            }
            for (r in 0 until rows) {
                val constraints = getConstraintsForCell(r, c, parent, cols)
                constraints.x = x
                constraints.width = width
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)))
        }


        var y = Spring.constant(initialY)
        for (r in 0 until rows) {
            var height = Spring.constant(0)
            for (c in 0 until cols) {
                height =
                    Spring.max(
                        height,
                        getConstraintsForCell(r, c, parent, cols).height,
                    )
            }
            for (c in 0 until cols) {
                val constraints = getConstraintsForCell(r, c, parent, cols)
                constraints.y = y
                constraints.height = height
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)))
        }


        val pCons = layout.getConstraints(parent)
        pCons.setConstraint(SpringLayout.SOUTH, y)
        pCons.setConstraint(SpringLayout.EAST, x)
    }
}
