package com.misc.model.view.mouse;

import com.misc.model.view.Main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class EditorWindowAdapter extends WindowAdapter {
    private final Main field31;

    public EditorWindowAdapter(Main var1) {
        this.field31 = var1;
    }

    public void windowClosing(WindowEvent var1) {
        Main.windowClosingCallback(this.field31, var1);
    }
}
