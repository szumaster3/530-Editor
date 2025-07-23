package com.editor.object;

import com.cache.defs.ObjectDefinition;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectEditor extends JFrame {
    private ObjectDefinition definitions;
    private ObjectSelection objectSelection;
    private JTextField name, options, models, childrenIds, modelColors, textureColors;
    private JCheckBox projectileClipped, notClipped;
    private JCheckBox isWalkable, isSolid, isInteractive, castsShadow, isProjectile;
    private JTextField configFileId;
    private JTextField configId;
    private JTextField clipType;
    private JSpinner sizeX, sizeY, scaleX, scaleY, scaleZ;
    private JSpinner rotationX, rotationY, rotationZ;
    private JSpinner animationId;
    private JSpinner heightOffsetX, heightOffsetY;

    private JTextField[] csk = new JTextField[7];
    private JTextField[] csv = new JTextField[7];

    private Map<Integer, Object> clientScriptData;
    private ClientScriptTableModel clientScriptModel;
    private JTable clientScriptTable;

    public ObjectEditor(ObjectSelection objectSelection, ObjectDefinition definitions) {
        this.definitions = definitions;
        this.objectSelection = objectSelection;

        setLocationRelativeTo(null);
        setTitle("Object Editor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        clientScriptData = definitions.clientScriptData != null ? definitions.clientScriptData : new HashMap<>();

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel mainPanel = createMainPropertiesPanel();
        tabbedPane.addTab("Properties", new JScrollPane(mainPanel));

        JPanel clientScriptPanel = createClientScriptPanel();
        tabbedPane.addTab("Client Scripts", clientScriptPanel);

        setContentPane(tabbedPane);

        setPreferredSize(new Dimension(820, 900));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createMainPropertiesPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Basic information.
        JPanel basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new BoxLayout(basicInfoPanel, BoxLayout.Y_AXIS));
        basicInfoPanel.setBorder(BorderFactory.createTitledBorder("Basic information"));
        basicInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel idLabel = new JLabel("Object ID:");
        idLabel.setPreferredSize(new Dimension(120, idLabel.getPreferredSize().height));
        JLabel idValueLabel = new JLabel(String.valueOf(definitions.id));
        idValueLabel.setFont(idValueLabel.getFont().deriveFont(Font.BOLD));
        idPanel.add(idLabel);
        idPanel.add(idValueLabel);
        idPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        basicInfoPanel.add(idPanel);

        basicInfoPanel.add(Box.createVerticalStrut(8));
        basicInfoPanel.add(createLabel("Name:", name = new JTextField(definitions.getName())));
        basicInfoPanel.add(Box.createVerticalStrut(8));

        options = new JTextField(optionsArrayToString(definitions.options));
        basicInfoPanel.add(createLabel("Options:", options));
        basicInfoPanel.add(Box.createVerticalStrut(8));

        basicInfoPanel.add(createLabel("Config file ID:", configFileId = new JTextField(String.valueOf(definitions.getConfigFileId()))));
        basicInfoPanel.add(createLabel("Config ID:", configId = new JTextField(String.valueOf(definitions.getConfigId()))));
        basicInfoPanel.add(createLabel("Clip type:", clipType = new JTextField(String.valueOf(definitions.getClipType()))));

        mainPanel.add(basicInfoPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Basic properties.
        JPanel basicPropsPanel = new JPanel();
        basicPropsPanel.setLayout(new BoxLayout(basicPropsPanel, BoxLayout.Y_AXIS));
        basicPropsPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        basicPropsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel clippingPanel = new JPanel();
        clippingPanel.setLayout(new BoxLayout(clippingPanel, BoxLayout.X_AXIS));
        projectileClipped = new JCheckBox("Projectile Clipped", definitions.isProjectileClipped());
        notClipped = new JCheckBox("Not Clipped", definitions.getClipped());
        clippingPanel.add(projectileClipped);
        clippingPanel.add(Box.createHorizontalStrut(15));
        clippingPanel.add(notClipped);
        clippingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        basicPropsPanel.add(wrap("Clipping:", clippingPanel));
        basicPropsPanel.add(Box.createVerticalStrut(8));

        JPanel sizePanel = new JPanel(new GridBagLayout());
        sizePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        addLabel(sizePanel, gbc, 0, "Size X:", definitions.getSizeX());
        addLabel(sizePanel, gbc, 2, "Size Y:", definitions.getSizeY());

        basicPropsPanel.add(wrap("Size:", sizePanel));

        mainPanel.add(basicPropsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Models and Colors.
        JPanel modelsColorsPanel = new JPanel();
        modelsColorsPanel.setLayout(new BoxLayout(modelsColorsPanel, BoxLayout.Y_AXIS));
        modelsColorsPanel.setBorder(BorderFactory.createTitledBorder("Models and Colors"));
        modelsColorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        models = new JTextField(intArrayToSemicolonString(definitions.models));
        modelsColorsPanel.add(createLabel("Models:", models));

        childrenIds = new JTextField(intArrayToSemicolonString(definitions.childrenIds));
        modelsColorsPanel.add(createLabel("Child IDs:", childrenIds));

        modelColors = new JTextField(pairColorsToString(
                shortArrayToIntArray(definitions.originalModelColors),
                shortArrayToIntArray(definitions.modifiedModelColors)
        ));
        modelsColorsPanel.add(createLabel("Model Colors:", modelColors));

        textureColors = new JTextField(pairColorsToString(
                shortArrayToIntArray(definitions.originalTextureColors),
                shortArrayToIntArray(definitions.modifiedTextureColors)
        ));
        modelsColorsPanel.add(createLabel("Texture Colors:", textureColors));

        mainPanel.add(modelsColorsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Transformations.
        JPanel transformPanel = new JPanel();
        transformPanel.setLayout(new BoxLayout(transformPanel, BoxLayout.Y_AXIS));
        transformPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
        transformPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Scale panel.
        JPanel scalePanel = new JPanel(new GridBagLayout());
        scalePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        addLabel(scalePanel, gbc, 0, "Scale X:", definitions.anInt3841);
        addLabel(scalePanel, gbc, 2, "Scale Y:", definitions.anInt3917);
        addLabel(scalePanel, gbc, 4, "Scale Z:", definitions.anInt3902);
        transformPanel.add(wrap("Scale:", scalePanel));

        // Rotation panel.
        JPanel rotationPanel = new JPanel(new GridBagLayout());
        rotationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        addLabel(rotationPanel, gbc, 0, "Rotation X:", definitions.anInt3840);
        addLabel(rotationPanel, gbc, 2, "Rotation Y:", definitions.anInt3878);
        addLabel(rotationPanel, gbc, 4, "Rotation Z:", definitions.anInt3876);
        transformPanel.add(wrap("Rotation:", rotationPanel));

        mainPanel.add(transformPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Animations.
        animationId = new JSpinner(new SpinnerNumberModel(definitions.anInt3855, -1, 5000, 1));
        limitSize(animationId);
        mainPanel.add(createLabel("Animation ID:", animationId));
        mainPanel.add(Box.createVerticalStrut(15));

        // Flags.
        JPanel flagsPanel = new JPanel();
        flagsPanel.setLayout(new BoxLayout(flagsPanel, BoxLayout.X_AXIS));
        flagsPanel.setBorder(BorderFactory.createTitledBorder("Flags"));
        flagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        isWalkable = new JCheckBox("Walkable", definitions.isWalkable());
        isSolid = new JCheckBox("Solid", definitions.isSolid());
        isInteractive = new JCheckBox("Interactive", definitions.isInteractive());
        castsShadow = new JCheckBox("Casts Shadow", definitions.castsShadow());
        isProjectile = new JCheckBox("Block Projectile", definitions.blocksProjectile());

        flagsPanel.add(isWalkable);
        flagsPanel.add(Box.createHorizontalStrut(10));
        flagsPanel.add(isSolid);
        flagsPanel.add(Box.createHorizontalStrut(10));
        flagsPanel.add(isInteractive);
        flagsPanel.add(Box.createHorizontalStrut(10));
        flagsPanel.add(castsShadow);
        flagsPanel.add(Box.createHorizontalStrut(10));
        flagsPanel.add(isProjectile);

        mainPanel.add(flagsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Height offsets.
        JPanel heightOffsetPanel = new JPanel(new GridBagLayout());
        heightOffsetPanel.setBorder(BorderFactory.createTitledBorder("Height offsets"));
        heightOffsetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        addLabel(heightOffsetPanel, gbc, 0, "Height offset X:", definitions.anInt3883);
        addLabel(heightOffsetPanel, gbc, 2, "Height offset Y:", definitions.anInt3915);

        mainPanel.add(heightOffsetPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Buttons.
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton applyButton = new JButton("Save Object");
        applyButton.addActionListener(
                actionEvent -> save()
        );

        JButton exportButton = new JButton("Dump to TXT");
        exportButton.addActionListener(
                actionEvent -> export()
        );

        buttonsPanel.add(applyButton);
        buttonsPanel.add(exportButton);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(buttonsPanel);

        return mainPanel;
    }

    private JPanel createClientScriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        clientScriptModel = new ClientScriptTableModel(clientScriptData);
        clientScriptTable = new JTable((TableModel) clientScriptModel);
        clientScriptTable.setFillsViewportHeight(true);

        panel.add(new JScrollPane(clientScriptTable), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            clientScriptModel.addEntry(0, "");
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            int selected = clientScriptTable.getSelectedRow();
            if (selected >= 0) {
                clientScriptModel.removeEntry(selected);
            }
        });

        JButton saveButton = new JButton("Save Client Scripts");
        saveButton.addActionListener(e -> {
            saveClientScripts();
        });

        JButton loadButton = new JButton("Load Client Scripts");
        loadButton.addActionListener(e -> {
            loadClientScripts();
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(loadButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void saveClientScripts() {
        Map<Integer, Object> newData = new HashMap<>();
        for (Map.Entry<Integer, Object> entry : clientScriptModel.getEntries()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                newData.put(entry.getKey(), entry.getValue());
            }
        }
        definitions.clientScriptData = (HashMap<Integer, Object>) newData;
        JOptionPane.showMessageDialog(this, "Client scripts saved to definitions.");
    }

    private void loadClientScripts() {
        clientScriptData = definitions.clientScriptData != null ? definitions.clientScriptData : new HashMap<>();
        clientScriptModel.setEntries(new ArrayList<>(clientScriptData.entrySet()));
        clientScriptModel.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Client scripts loaded from definitions.");
    }


    private void save() {
        try {
            definitions.setName(name.getText().trim());
            definitions.setOptions(parseOptionsString(options.getText()));
            definitions.models = parseIntArray(models.getText().trim(), ";");
            definitions.childrenIds = parseIntArray(childrenIds.getText().trim(), ";");
            definitions.originalModelColors = toShortArray(parseOriginalColors(modelColors.getText().trim()));
            definitions.modifiedModelColors = toShortArray(parseModifiedColors(modelColors.getText().trim()));
            definitions.originalTextureColors = toShortArray(parseOriginalColors(textureColors.getText().trim()));
            definitions.modifiedTextureColors = toShortArray(parseModifiedColors(textureColors.getText().trim()));

            definitions.setSizeX((Integer) sizeX.getValue());
            definitions.setSizeY((Integer) sizeY.getValue());
            definitions.setBlockProjectile(projectileClipped.isSelected());
            definitions.setClipped(notClipped.isSelected());
            definitions.anInt3841 = (Integer) scaleX.getValue();
            definitions.anInt3917 = (Integer) scaleY.getValue();
            definitions.anInt3902 = (Integer) scaleZ.getValue();
            definitions.anInt3840 = (Integer) rotationX.getValue();
            definitions.anInt3878 = (Integer) rotationY.getValue();
            definitions.anInt3876 = (Integer) rotationZ.getValue();
            definitions.anInt3855 = (Integer) animationId.getValue();
            definitions.setWalkable(isWalkable.isSelected());
            definitions.setSolid(isSolid.isSelected());
            definitions.setInteractive(isInteractive.isSelected());
            definitions.setCastsShadow(castsShadow.isSelected());
            definitions.setBlockProjectile(isProjectile.isSelected());
            definitions.anInt3883 = (Integer) heightOffsetX.getValue();
            definitions.anInt3915 = (Integer) heightOffsetY.getValue();

            try {
                definitions.setConfigFileId(Integer.parseInt(configFileId.getText().trim()));
            } catch (NumberFormatException e) {
                definitions.setConfigFileId(-1);
            }
            try {
                definitions.setConfigId(Integer.parseInt(configId.getText().trim()));
            } catch (NumberFormatException e) {
                definitions.setConfigId(-1);
            }
            try {
                definitions.setClipType(Integer.parseInt(clipType.getText().trim()));
            } catch (NumberFormatException e) {
                definitions.setClipType(-1);
            }

            Map<Integer, Object> csd = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                String keyStr = csk[i].getText().trim();
                String valueStr = csv[i].getText().trim();
                if (!keyStr.isEmpty() && !valueStr.isEmpty()) {
                    try {
                        int key = Integer.parseInt(keyStr);
                        Object value;
                        try {
                            value = Integer.parseInt(valueStr);
                        } catch (NumberFormatException ex) {
                            value = valueStr;
                        }
                        csd.put(key, value);
                    } catch (NumberFormatException ignored) {}
                }
            }
            definitions.clientScriptData = (HashMap<Integer, Object>) csd;

            definitions.write(ObjectSelection.Cache);
            objectSelection.updateObjectDefs(definitions);

            JOptionPane.showMessageDialog(this, "Object saved.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving object: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void export() {
        File f = new File("./data/export/items/");
        f.mkdirs();
        String lineSep = System.lineSeparator();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(f.getPath(), this.definitions.id + ".txt")), StandardCharsets.UTF_8))) {

            writer.write("name = " + definitions.getName() + lineSep);
            writer.write("options = " + joinArray(definitions.options, ";") + lineSep);
            writer.write("models = " + joinIntArray(definitions.models, ";") + lineSep);
            writer.write("childrenIds = " + joinIntArray(definitions.childrenIds, ";") + lineSep);
            writer.write("model colors = " + getColorPairs(definitions.originalModelColors, definitions.modifiedModelColors) + lineSep);
            writer.write("texture colors = " + getColorPairs(definitions.originalTextureColors, definitions.modifiedTextureColors) + lineSep);
            writer.write("clipType = " + definitions.getClipType() + lineSep);
            writer.write("sizeX = " + definitions.getSizeX() + lineSep);
            writer.write("sizeY = " + definitions.getSizeY() + lineSep);
            writer.write("configFileId = " + definitions.getConfigFileId() + lineSep);
            writer.write("configId = " + definitions.getConfigId() + lineSep);
            writer.write("animationId = " + definitions.anInt3855 + lineSep);
            writer.write("scaleX = " + definitions.anInt3841 + lineSep);
            writer.write("scaleY = " + definitions.anInt3917 + lineSep);
            writer.write("scaleZ = " + definitions.anInt3902 + lineSep);
            writer.write("rotationX = " + definitions.anInt3840 + lineSep);
            writer.write("rotationY = " + definitions.anInt3878 + lineSep);
            writer.write("rotationZ = " + definitions.anInt3876 + lineSep);
            writer.write("heightOffsetX = " + definitions.anInt3883 + lineSep);
            writer.write("heightOffsetY = " + definitions.anInt3915 + lineSep);
            writer.write("projectileClipped = " + definitions.isProjectileClipped() + lineSep);
            writer.write("notClipped = " + definitions.getClipped() + lineSep);
            writer.write("isWalkable = " + definitions.isWalkable() + lineSep);
            writer.write("isSolid = " + definitions.isSolid() + lineSep);
            writer.write("isInteractive = " + definitions.isInteractive() + lineSep);
            writer.write("castsShadow = " + definitions.castsShadow() + lineSep);
            writer.write("isProjectile = " + definitions.blocksProjectile() + lineSep);

            writer.flush();
            JOptionPane.showMessageDialog(this, "Object exported to file: " + this.definitions.id + ".txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createLabel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, 25));
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel wrap(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, int x, String text, int value) {
        gbc.gridx = x;
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(100, 25));
        panel.add(label, gbc);
        gbc.gridx = x + 1;
        JTextField field = new JTextField(String.valueOf(value), 4);
        panel.add(field, gbc);
    }

    private String optionsArrayToString(String[] options) {
        if (options == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            sb.append(option == null ? "null" : option).append(";");
        }
        return sb.toString();
    }

    private String intArrayToSemicolonString(int[] arr) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i : arr) {
            sb.append(i).append(";");
        }
        return sb.toString();
    }

    private String pairColorsToString(int[] original, int[] modified) {
        if (original == null || modified == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(original.length, modified.length); i++) {
            sb.append(original[i]).append("=").append(modified[i]).append(";");
        }
        return sb.toString();
    }

    private String[] parseOptionsString(String text) {
        if (text == null || text.isEmpty()) return new String[0];
        String[] parts = text.split(";", -1);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase("null")) {
                parts[i] = null;
            }
        }
        return parts;
    }

    private short[] toShortArray(int[] intArray) {
        if (intArray == null) return null;
        short[] shortArray = new short[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            shortArray[i] = (short) intArray[i];
        }
        return shortArray;
    }

    private int[] parseIntArray(String text, String delimiter) {
        if (text == null || text.isEmpty()) return new int[0];
        String[] parts = text.split(delimiter);
        int[] result = new int[parts.length];
        int idx = 0;
        for (String part : parts) {
            try {
                result[idx++] = Integer.parseInt(part.trim());
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        if (idx < result.length) {
            return Arrays.copyOf(result, idx);
        }
        return result;
    }

    private int[] parseOriginalColors(String text) {
        if (text == null || text.isEmpty()) return new int[0];
        String[] pairs = text.split(";");
        int[] originals = new int[pairs.length];
        int idx = 0;
        for (String pair : pairs) {
            String[] split = pair.split(":");
            if (split.length == 2) {
                try {
                    originals[idx++] = Integer.parseInt(split[0].trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (idx < originals.length) {
            return Arrays.copyOf(originals, idx);
        }
        return originals;
    }

    private int[] parseModifiedColors(String text) {
        if (text == null || text.isEmpty()) return new int[0];
        String[] pairs = text.split(";");
        int[] modified = new int[pairs.length];
        int idx = 0;
        for (String pair : pairs) {
            String[] split = pair.split(":");
            if (split.length == 2) {
                try {
                    modified[idx++] = Integer.parseInt(split[1].trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (idx < modified.length) {
            return Arrays.copyOf(modified, idx);
        }
        return modified;
    }

    private int[] shortArrayToIntArray(short[] arr) {
        if (arr == null) return null;
        int[] intArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            intArr[i] = arr[i];
        }
        return intArr;
    }
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    private String joinArray(String[] arr, String sep) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            if (s != null && !s.isEmpty()) {
                if (sb.length() > 0) sb.append(sep);
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private String joinIntArray(int[] arr, String sep) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int v : arr) {
            if (sb.length() > 0) sb.append(sep);
            sb.append(v);
        }
        return sb.toString();
    }
    private String getColorPairs(short[] original, short[] modified) {
        if (original == null || modified == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(original.length, modified.length); i++) {
            if (i > 0) sb.append(";");
            sb.append(original[i]).append("=").append(modified[i]);
        }
        return sb.toString();
    }

    private void limitSize(JSpinner spinner) {
        if (spinner == null) return;
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
        spinner.setEditor(editor);
    }
}
