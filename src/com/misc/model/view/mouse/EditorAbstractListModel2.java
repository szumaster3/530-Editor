package com.misc.model.view.mouse;

import com.misc.model.view.Main;

import javax.swing.*;

public final class EditorAbstractListModel2 extends AbstractListModel {
    private final String[] field2;
    private final String[] field4;

    public EditorAbstractListModel2(Main var1, int var2, String[] var3) {
        this.field4 = var3;
        this.field2 = this.field4;
    }

    public int getSize() {
        return this.field2.length;
    }

    public Object getElementAt(int var1) {
        return this.field2[var1];
    }
}
