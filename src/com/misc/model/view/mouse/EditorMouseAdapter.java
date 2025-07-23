package com.misc.model.view.mouse;

import com.misc.model.view.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class EditorMouseAdapter extends MouseAdapter {
    private final Main main;

    public EditorMouseAdapter(Main main) {
        this.main = main;
    }

    public void mousePressed(MouseEvent var1) {
    }

    public void mouseReleased(MouseEvent var1) {
        Main.mouseReleasedCallback(this.main, var1);
    }
}
