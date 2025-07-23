package com.editor.object;

import javax.swing.table.AbstractTableModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientScriptTableModel extends AbstractTableModel {

    private final List<Map.Entry<Integer, Object>> entries;

    public ClientScriptTableModel(Map<Integer, Object> data) {
        this.entries = new ArrayList<>(data.entrySet());
    }

    public void addEntry(Integer key, Object value) {
        entries.add(new AbstractMap.SimpleEntry<>(key, value));
        fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }

    public List<Map.Entry<Integer, Object>> getEntries() {
        return entries;
    }

    public void setEntries(List<Map.Entry<Integer, Object>> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return 2; // key, value
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map.Entry<Integer, Object> entry = entries.get(rowIndex);
        return columnIndex == 0 ? entry.getKey() : entry.getValue();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Map.Entry<Integer, Object> entry = entries.get(rowIndex);
        if (columnIndex == 0) {
            try {
                Integer key = Integer.parseInt(aValue.toString());
                entries.set(rowIndex, new AbstractMap.SimpleEntry<>(key, entry.getValue()));
            } catch (NumberFormatException ignored) {
            }
        } else if (columnIndex == 1) {
            entries.set(rowIndex, new AbstractMap.SimpleEntry<>(entry.getKey(), aValue));
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // both key and value are editable
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Key" : "Value";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }
}