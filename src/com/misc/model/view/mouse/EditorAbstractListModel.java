package com.misc.model.view.mouse;

import com.misc.model.view.Main;

import javax.swing.*;

public final class EditorAbstractListModel extends AbstractListModel {
    private final String[] field24;
    private final String[] field26;

    public EditorAbstractListModel(Main var1, int var2, String[] var3) {
        this.field26 = var3;
        this.field24 = this.field26;
    }

    public int getSize() {
        return this.field24.length;
    }

    public Object getElementAt(int var1) {
        return this.field24[var1];
    }
}
