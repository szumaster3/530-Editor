/*
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Google, Inc. - initial API and implementation
 */
package com.cache.util

import java.awt.Component
import java.awt.Container
import java.awt.FocusTraversalPolicy

/**
 * Cyclic focus traversal policy based on array of components.
 *
 *
 * This class may be freely distributed as part of any application or plugin.
 *
 * @author scheglov_ke
 */
class FocusTraversalOnArray(
    private val m_Components: Array<Component>,
) : FocusTraversalPolicy() {
    private fun indexCycle(
        index: Int,
        delta: Int,
    ): Int {
        val size = m_Components.size
        val next = (index + delta + size) % size
        return next
    }

    private fun cycle(
        currentComponent: Component,
        delta: Int,
    ): Component {
        var index = -1
        loop@ for (i in m_Components.indices) {
            val component = m_Components[i]
            var c: Component? = currentComponent
            while (c != null) {
                if (component === c) {
                    index = i
                    break@loop
                }
                c = c.parent
            }
        }
        // try to find enabled component in "delta" direction
        val initialIndex = index
        while (true) {
            val newIndex = indexCycle(index, delta)
            if (newIndex == initialIndex) {
                break
            }
            index = newIndex
            val component = m_Components[newIndex]
            if (component.isEnabled && component.isVisible && component.isFocusable) {
                return component
            }
        }
        return currentComponent
    }

    override fun getComponentAfter(
        container: Container,
        component: Component,
    ): Component = cycle(component, 1)!!

    override fun getComponentBefore(
        container: Container,
        component: Component,
    ): Component = cycle(component, -1)!!

    override fun getFirstComponent(container: Container): Component = m_Components[0]

    override fun getLastComponent(container: Container): Component = m_Components[m_Components.size - 1]

    override fun getDefaultComponent(container: Container): Component = getFirstComponent(container)
}
