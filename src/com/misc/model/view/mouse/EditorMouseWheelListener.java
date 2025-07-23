package com.misc.model.view.mouse;

import com.misc.model.view.Main;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public final class EditorMouseWheelListener implements MouseWheelListener {
    private final Main field30;

    public EditorMouseWheelListener(Main var1) {
        this.field30 = var1;
    }

    public void mouseWheelMoved(MouseWheelEvent var1) {
        Main.mouseWheelMoved(this.field30, var1);
    }
}
