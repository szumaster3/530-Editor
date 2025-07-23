package com.editor.object;

import com.cache.defs.ObjectDefinition;
import com.cache.store.Cache;
import com.cache.util.Utils;
import com.misc.TextPrompt;
import launcher.Main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The object selection.
 */
public class ObjectSelection extends JFrame {
    private static final long serialVersionUID = -5663827580887902170L;
    public static Cache Cache;
    private JButton addButton;
    private JButton duplicateButton;
    private JButton editButton;
    private DefaultListModel<ObjectDefinition> objectsListModel;
    private JList<ObjectDefinition> objectsList;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JMenuItem exitButton;
    private JButton deleteButton;
    private JTextField searchField;

    private final List<ObjectDefinition> allObjects = new ArrayList<>();

    public ObjectSelection(String cache) throws IOException {
        Cache = new Cache(cache);
        setTitle("Object Selection");
        setResizable(false);
        setDefaultCloseOperation(2);
        setLocationRelativeTo(null);
        initComponents();
    }

    public ObjectSelection() {
        initComponents();
    }

    public static void main(String[] args) throws IOException {
        Cache = new Cache("cache/", false);
        EventQueue.invokeLater(() -> new ObjectSelection().setVisible(true));
    }

    private void initComponents() {
        editButton = new JButton("Edit");
        addButton = new JButton("Add New");
        duplicateButton = new JButton("Duplicate");
        deleteButton = new JButton("Delete");
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu("File");
        exitButton = new JMenuItem("Close");

        this.objectsListModel = new DefaultListModel<>();
        this.objectsList = new JList<>(this.objectsListModel);
        objectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectsList.setCellRenderer(new ObjectListCellRenderer());
        JScrollPane jScrollPane1 = new JScrollPane(objectsList);

        // Search field (live search)
        searchField = new JTextField();
        searchField.setColumns(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterObjectList(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterObjectList(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterObjectList(searchField.getText());
            }
        });

        new TextPrompt("Search by ID or name...", searchField);

        editButton.addActionListener(e -> {
            try {
                editObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        addButton.addActionListener(e -> {
            try {
                addNewObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        duplicateButton.addActionListener(e -> {
            try {
                duplicateObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteButton.addActionListener(e -> deleteObject());

        exitButton.addActionListener(this::exitButtonActionPerformed);
        jMenu1.add(exitButton);
        jMenuBar1.add(jMenu1);
        setJMenuBar(jMenuBar1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(editButton)
                                        .addGap(0, 0, Short.MAX_VALUE).addComponent(addButton))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(duplicateButton)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(deleteButton)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(editButton)
                                .addComponent(addButton))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(duplicateButton)
                                .addComponent(deleteButton))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        addAllObjects();
    }

    private void exitButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

    public void addAllObjects() {
        int totalObjects = Utils.getObjectDefinitionsSize(Cache);
        for (int id = 0; id < totalObjects; ++id) {
            ObjectDefinition obj = ObjectDefinition.getObjectDefinition(Cache, id);
            if (obj != null) {
                allObjects.add(obj);
                addObjectDefs(obj);
            }
        }
        Main.log("ObjectSelection", "All Objects Loaded");
    }

    private void filterObjectList(String searchTerm) {
        List<ObjectDefinition> filtered;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filtered = allObjects;
        } else {
            filtered = new ArrayList<>();
            for (ObjectDefinition obj : allObjects) {
                if ((obj.getName() != null && obj.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                        || Integer.toString(obj.id).equals(searchTerm)) {
                    filtered.add(obj);
                }
            }
        }

        objectsListModel.clear();
        for (ObjectDefinition obj : filtered) {
            objectsListModel.addElement(obj);
        }
    }

    public void addObjectDefs(final ObjectDefinition defs) {
        EventQueue.invokeLater(() -> objectsListModel.addElement(defs));
    }

    public void updateObjectDefs(final ObjectDefinition obj) {
        EventQueue.invokeLater(() -> {
            int index = objectsListModel.indexOf(obj);
            if (index == -1) {
                objectsListModel.addElement(obj);
            } else {
                objectsListModel.setElementAt(obj, index);
            }
        });
    }

    public void removeObjectDefs(final ObjectDefinition obj) {
        EventQueue.invokeLater(() -> {
            objectsListModel.removeElement(obj);
            allObjects.remove(obj);
        });
    }

    private void editObject() throws IOException {
        int selectedIndex = objectsList.getSelectedIndex();
        if (selectedIndex != -1) {
            ObjectDefinition obj = objectsList.getSelectedValue();
            if (obj != null) {
                new ObjectEditor(this, obj);
            }
        }
    }

    private void addNewObject() throws IOException {
        ObjectDefinition obj = new ObjectDefinition(ObjectSelection.Cache, getNewObjectID(), false);
        if (obj != null && obj.id != -1) {
            System.out.println("Adding new object with ID: " + obj.id);
            new ObjectEditor(this, obj).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create a new object!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void duplicateObject() throws IOException {
        ObjectDefinition obj = objectsList.getSelectedValue();
        if (obj != null) {
            ObjectDefinition clonedObj = (ObjectDefinition) obj.clone();
            if (clonedObj != null) {
                clonedObj.id = getNewObjectID();
                if (clonedObj.id != -1) {
                    new ObjectEditor(this, clonedObj).setVisible(true);
                }
            }
        }
    }

    private void deleteObject() {
        ObjectDefinition obj = objectsList.getSelectedValue();
        if (obj != null) {
            int result = JOptionPane.showConfirmDialog(this, "Do you really want to delete object [" + obj.id + "]?");
            if (result == JOptionPane.YES_OPTION) {
                System.out.println("Deleting object: " + obj.id);
                Cache.getIndexes()[16].removeFile(obj.getArchiveId(), obj.getFileId());
                removeObjectDefs(obj);
                Main.log("ObjectSelection", "Object " + obj.id + " removed.");
            }
        }
    }

    private int getNewObjectID() {
        return Utils.getObjectDefinitionsSize(Cache);
    }

    private class ObjectListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ObjectDefinition) {
                ObjectDefinition defs = (ObjectDefinition) value;
                setText(defs.id + " - " + defs.getName());
            }
            return c;
        }
    }
}
