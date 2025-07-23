package com.editor.object;

import javax.swing.table.AbstractTableModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ClientScripts extends AbstractTableModel {
    private final String[] columnNames = {"Key", "Type", "Value"};
    private final List<Map.Entry<Integer, Object>> entries;

    public ClientScripts(Map<Integer, Object> clientScriptData) {
        this.entries = new ArrayList<>(clientScriptData.entrySet());
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Map.Entry<Integer, Object> entry = entries.get(row);
        switch (col) {
            case 0:
                return entry.getKey();
            case 1:
                return entry.getValue() instanceof String ? "String" : "Integer";
            case 2:
                return entry.getValue().toString();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        Map.Entry<Integer, Object> entry = entries.get(row);
        try {
            switch (col) {
                case 0:
                    int newKey = Integer.parseInt(aValue.toString());
                    entries.set(row, new AbstractMap.SimpleEntry<>(newKey, entry.getValue()));
                    break;
                case 1:
                    String type = aValue.toString();
                    Object val = entry.getValue();
                    if (type.equals("String") && !(val instanceof String)) {
                        val = "";
                    } else if (type.equals("Integer") && !(val instanceof Integer)) {
                        val = 0;
                    }
                    entries.set(row, new AbstractMap.SimpleEntry<>(entry.getKey(), val));
                    break;
                case 2:
                    if (entry.getValue() instanceof String) {
                        entries.set(row, new AbstractMap.SimpleEntry<>(entry.getKey(), aValue.toString()));
                    } else {
                        entries.set(row, new AbstractMap.SimpleEntry<>(entry.getKey(), Integer.parseInt(aValue.toString())));
                    }
                    break;
            }
            fireTableCellUpdated(row, col);
        } catch (Exception ex) {

        }
    }

    public List<Map.Entry<Integer, Object>> getEntries() {
        return entries;
    }
}
