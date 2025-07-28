package com.editor.object;

import javax.swing.table.AbstractTableModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ClientScriptTableModel extends AbstractTableModel {
    private final List < Map.Entry < Integer, Object >> entries;

    public ClientScriptTableModel(Map < Integer, Object > clientScriptData) {
        entries = new ArrayList < > (clientScriptData.entrySet());
    }

    public void addRow() {
        entries.add(new AbstractMap.SimpleEntry < > (0, ""));
        fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= entries.size()) return;
        entries.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public List < Map.Entry < Integer, Object >> getEntries() {
        return new ArrayList < > (entries);
    }

    public void setEntries(List < Map.Entry < Integer, Object >> newEntries) {
        entries.clear();
        if (newEntries != null) {
            entries.addAll(newEntries);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Key" : "Value";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map.Entry < Integer, Object > entry = entries.get(rowIndex);
        return columnIndex == 0 ? entry.getKey() : entry.getValue();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Map.Entry < Integer, Object > entry = entries.get(rowIndex);
        try {
            if (columnIndex == 0) {
                int key = Integer.parseInt(aValue.toString());
                entries.set(rowIndex, new AbstractMap.SimpleEntry < > (key, entry.getValue()));
            } else {
                entries.set(rowIndex, new AbstractMap.SimpleEntry < > (entry.getKey(), aValue));
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}