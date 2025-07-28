package com.editor.object;

import com.cache.defs.ObjectDefinition;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectEditor extends JFrame {
    private ObjectDefinition definitions;
    private ObjectSelection objectSelection;

    private JTextField name, options, configFileId, configId, clipType;
    private JCheckBox projectileClipped, notClipped;
    private JTextField sizeX, sizeY;
    private JTextField models, childrenIds, modelColors, textureColors;
    private JTextField scaleX, scaleY, scaleZ, rotationX, rotationY, rotationZ;
    private JTextField animationId;
    private JCheckBox isWalkable, isSolid, isInteractive, castsShadow, isProjectile;
    private JTextField heightOffsetX, heightOffsetY;
    private JTextField[] csk = new JTextField[7];
    private JTextField[] csv = new JTextField[7];

    private Map < Integer, Object > clientScriptData;
    private ClientScriptTableModel clientScriptModel;
    private JTable clientScriptTable;

    public ObjectEditor(ObjectSelection objectSelection, ObjectDefinition definitions) {
        this.definitions = definitions;
        this.objectSelection = objectSelection;

        setTitle("Object Editor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        clientScriptData = definitions.clientScriptData != null ? definitions.clientScriptData : new HashMap < > ();

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Properties", basicPanel());
        tabbedPane.addTab("Advanced", secondPanel());
        tabbedPane.addTab("Client Scripts", scriptPanel());

        setContentPane(tabbedPane);

        setPreferredSize(new Dimension(700, 550));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private JPanel basicPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel basicInfoPanel = new JPanel(new GridBagLayout());
        basicInfoPanel.setBorder(BorderFactory.createTitledBorder("Basic information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel idLabel = new JLabel("Object ID:");
        idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        idLabel.setPreferredSize(new Dimension(140, idLabel.getPreferredSize().height));
        JLabel idValueLabel = new JLabel(String.valueOf(definitions.id));
        idValueLabel.setFont(idValueLabel.getFont().deriveFont(Font.BOLD));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        basicInfoPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        basicInfoPanel.add(idValueLabel, gbc);

        row++;

        name = new JTextField(definitions.getName());
        name.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        basicInfoPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(name, gbc);
        row++;

        options = new JTextField(optionsArrayToString(definitions.options));
        options.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        basicInfoPanel.add(new JLabel("Options:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(options, gbc);
        row++;

        configFileId = new JTextField(String.valueOf(definitions.getConfigFileId()));
        configFileId.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        basicInfoPanel.add(new JLabel("Config file ID:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(configFileId, gbc);
        row++;

        configId = new JTextField(String.valueOf(definitions.getConfigId()));
        configId.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        basicInfoPanel.add(new JLabel("Config ID:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(configId, gbc);
        row++;

        clipType = new JTextField(String.valueOf(definitions.getClipType()));
        clipType.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        basicInfoPanel.add(new JLabel("Clip type:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(clipType, gbc);
        row++;

        animationId = new JTextField(String.valueOf(definitions.anInt3855));
        animationId.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        basicInfoPanel.add(new JLabel("Animation ID:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        basicInfoPanel.add(animationId, gbc);
        row++;

        panel.add(basicInfoPanel);
        panel.add(Box.createVerticalStrut(15));

        JPanel basicPropsPanel = new JPanel(new GridBagLayout());
        basicPropsPanel.setBorder(BorderFactory.createTitledBorder("Properties"));

        row = 0;
        gbc.weightx = 0;

        JPanel clippingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        projectileClipped = new JCheckBox("Projectile Clipped", definitions.isProjectileClipped());
        notClipped = new JCheckBox("Not Clipped", definitions.getClipped());
        clippingPanel.add(projectileClipped);
        clippingPanel.add(notClipped);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        basicPropsPanel.add(new JLabel("Clipping:"), gbc);

        row++;
        gbc.gridy = row;
        basicPropsPanel.add(clippingPanel, gbc);

        row++;
        gbc.gridwidth = 1;

        sizeX = new JTextField(String.valueOf(definitions.getSizeX()));
        sizeX.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        basicPropsPanel.add(new JLabel("Size X:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        basicPropsPanel.add(sizeX, gbc);

        row++;

        sizeY = new JTextField(String.valueOf(definitions.getSizeY()));
        sizeY.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        basicPropsPanel.add(new JLabel("Size Y:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        basicPropsPanel.add(sizeY, gbc);

        row++;

        panel.add(basicPropsPanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton applyButton = new JButton("Save Object");
        applyButton.addActionListener(e -> save());

        JButton exportButton = new JButton("Dump to TXT");
        exportButton.addActionListener(e ->
                export ());

        buttonsPanel.add(applyButton);
        buttonsPanel.add(exportButton);
        panel.add(buttonsPanel);

        return panel;
    }

    private JPanel secondPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel modelsColorsPanel = new JPanel(new GridBagLayout());
        modelsColorsPanel.setBorder(BorderFactory.createTitledBorder("Models and Colors"));

        int row = 0;

        models = new JTextField(intArrayToSemicolonString(definitions.models));
        models.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        modelsColorsPanel.add(new JLabel("Models:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        modelsColorsPanel.add(models, gbc);

        row++;

        childrenIds = new JTextField(intArrayToSemicolonString(definitions.childrenIds));
        childrenIds.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        modelsColorsPanel.add(new JLabel("Child IDs:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        modelsColorsPanel.add(childrenIds, gbc);

        row++;

        modelColors = new JTextField(pairColorsToString(
                shortArrayToIntArray(definitions.originalModelColors),
                shortArrayToIntArray(definitions.modifiedModelColors)
        ));
        modelColors.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        modelsColorsPanel.add(new JLabel("Model Colors:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        modelsColorsPanel.add(modelColors, gbc);

        row++;

        textureColors = new JTextField(pairColorsToString(
                shortArrayToIntArray(definitions.originalTextureColors),
                shortArrayToIntArray(definitions.modifiedTextureColors)
        ));
        textureColors.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        modelsColorsPanel.add(new JLabel("Texture Colors:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        modelsColorsPanel.add(textureColors, gbc);

        panel.add(modelsColorsPanel);
        panel.add(Box.createVerticalStrut(15));

        JPanel flagsPanel = new JPanel(new GridBagLayout());
        flagsPanel.setBorder(BorderFactory.createTitledBorder("Flags"));

        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        isWalkable = new JCheckBox("Is Walkable", definitions.isWalkable());
        isSolid = new JCheckBox("Is Solid", definitions.isSolid());
        isInteractive = new JCheckBox("Is Interactive", definitions.isInteractive());
        castsShadow = new JCheckBox("Casts Shadow", definitions.castsShadow());
        isProjectile = new JCheckBox("Is Projectile", definitions.blocksProjectile());

        gbc.gridx = 0;
        gbc.gridy = 0;
        flagsPanel.add(isWalkable, gbc);
        gbc.gridx = 1;
        flagsPanel.add(isSolid, gbc);
        gbc.gridx = 2;
        flagsPanel.add(isInteractive, gbc);
        gbc.gridx = 3;
        flagsPanel.add(castsShadow, gbc);
        gbc.gridx = 4;
        flagsPanel.add(isProjectile, gbc);

        panel.add(flagsPanel);
        panel.add(Box.createVerticalStrut(15));

        // HEIGHT PANEL
        JPanel heightPanel = new JPanel(new GridBagLayout());
        heightPanel.setBorder(BorderFactory.createTitledBorder("Height Offsets"));
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        row = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row;
        heightPanel.add(new JLabel("Height offset X:"), gbc);
        heightOffsetX = new JTextField(String.valueOf(definitions.anInt3883));
        heightOffsetX.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        heightPanel.add(heightOffsetX, gbc);

        row++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row;
        heightPanel.add(new JLabel("Height offset Y:"), gbc);
        heightOffsetY = new JTextField(String.valueOf(definitions.anInt3915));
        heightOffsetY.setPreferredSize(new Dimension(180, 24));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        heightPanel.add(heightOffsetY, gbc);

        panel.add(heightPanel);
        panel.add(Box.createVerticalStrut(15));

        // TRANSFORMATIONS PANEL
        JPanel transformPanel = new JPanel(new GridBagLayout());
        transformPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
        row = 0;

        scaleX = new JTextField(String.valueOf(definitions.anInt3841));
        scaleX.setPreferredSize(new Dimension(60, 24));
        scaleY = new JTextField(String.valueOf(definitions.anInt3917));
        scaleY.setPreferredSize(new Dimension(60, 24));
        scaleZ = new JTextField(String.valueOf(definitions.anInt3902));
        scaleZ.setPreferredSize(new Dimension(60, 24));

        rotationX = new JTextField(String.valueOf(definitions.anInt3840));
        rotationX.setPreferredSize(new Dimension(60, 24));
        rotationY = new JTextField(String.valueOf(definitions.anInt3878));
        rotationY.setPreferredSize(new Dimension(60, 24));
        rotationZ = new JTextField(String.valueOf(definitions.anInt3876));
        rotationZ.setPreferredSize(new Dimension(60, 24));

        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        transformPanel.add(new JLabel("Scale (X,Y,Z):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel scalePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        scalePanel.add(scaleX);
        scalePanel.add(scaleY);
        scalePanel.add(scaleZ);
        transformPanel.add(scalePanel, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        transformPanel.add(new JLabel("Rotation (X,Y,Z):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel rotationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rotationPanel.add(rotationX);
        rotationPanel.add(rotationY);
        rotationPanel.add(rotationZ);
        transformPanel.add(rotationPanel, gbc);

        panel.add(transformPanel);

        return panel;
    }

    private JPanel scriptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        clientScriptModel = new ClientScriptTableModel(clientScriptData);
        clientScriptTable = new JTable(clientScriptModel);

        panel.add(new JScrollPane(clientScriptTable), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> clientScriptModel.addRow());

        JButton removeRowButton = new JButton("Remove Row");
        removeRowButton.addActionListener(e -> {
            int selectedRow = clientScriptTable.getSelectedRow();
            if (selectedRow >= 0) {
                clientScriptModel.removeRow(selectedRow);
            }
        });

        JButton saveButton = new JButton("Save Client Scripts");
        saveButton.addActionListener(e -> saveClientScripts());

        JButton loadButton = new JButton("Load Client Scripts");
        loadButton.addActionListener(e -> loadClientScripts());

        buttonsPanel.add(addRowButton);
        buttonsPanel.add(removeRowButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(loadButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void saveClientScripts() {
        if (definitions == null) {
            JOptionPane.showMessageDialog(this, "Definitions object is not initialized!");
            return;
        }
        if (clientScriptModel == null) {
            JOptionPane.showMessageDialog(this, "ClientScriptModel is not initialized!");
            return;
        }

        Map < Integer, Object > newData = new HashMap < > ();
        for (Map.Entry < Integer, Object > entry: clientScriptModel.getEntries()) {
            Integer key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                newData.put(key, value);
            }
        }

        if (definitions.clientScriptData == null) {
            definitions.clientScriptData = new HashMap < > ();
        }
        definitions.clientScriptData.clear();
        definitions.clientScriptData.putAll(newData);

        JOptionPane.showMessageDialog(this, "Client scripts saved to definitions.");
    }

    private void loadClientScripts() {
        if (definitions.clientScriptData == null) {
            definitions.clientScriptData = new HashMap < > ();
        }
        clientScriptData = definitions.clientScriptData;
        clientScriptModel.setEntries(new ArrayList < > (clientScriptData.entrySet()));
        clientScriptModel.fireTableDataChanged();

        JOptionPane.showMessageDialog(this, "Client scripts loaded from definitions.");
    }

    private void save() {
        try {
            definitions.setName(name.getText());
            definitions.setOptions(parseOptionsString(options.getText()));

            definitions.models = parseIntArray(models.getText(), ";");
            definitions.childrenIds = parseIntArray(childrenIds.getText(), ";");

            definitions.originalModelColors = toShortArray(parseOriginalColors(modelColors.getText()));
            definitions.modifiedModelColors = toShortArray(parseModifiedColors(modelColors.getText()));

            definitions.originalTextureColors = toShortArray(parseOriginalColors(textureColors.getText()));
            definitions.modifiedTextureColors = toShortArray(parseModifiedColors(textureColors.getText()));

            definitions.setSizeX(parseInt(sizeX.getText(), 1));
            definitions.setSizeY(parseInt(sizeY.getText(), 1));

            definitions.setBlockProjectile(projectileClipped.isSelected());
            definitions.setClipped(notClipped.isSelected());

            definitions.anInt3841 = parseInt(scaleX.getText(), 128);
            definitions.anInt3917 = parseInt(scaleY.getText(), 128);
            definitions.anInt3902 = parseInt(scaleZ.getText(), 128);

            definitions.anInt3840 = parseInt(rotationX.getText(), 0);
            definitions.anInt3878 = parseInt(rotationY.getText(), 0);
            definitions.anInt3876 = parseInt(rotationZ.getText(), 0);

            definitions.anInt3855 = parseInt(animationId.getText(), -1);

            definitions.setWalkable(isWalkable.isSelected());
            definitions.setSolid(isSolid.isSelected());
            definitions.setInteractive(isInteractive.isSelected());
            definitions.setCastsShadow(castsShadow.isSelected());
            definitions.setBlockProjectile(isProjectile.isSelected());

            definitions.anInt3883 = parseInt(heightOffsetX.getText(), 0);
            definitions.anInt3915 = parseInt(heightOffsetY.getText(), 0);

            definitions.setConfigFileId(parseInt(configFileId.getText(), -1));
            definitions.setConfigId(parseInt(configId.getText(), -1));
            definitions.setClipType(parseInt(clipType.getText(), -1));

      /*Map<Integer, Object> csd = new HashMap<>();
      for (int i = 0; i < 7; i++) {
          String keyStr = csk[i].getText();
          String valueStr = csv[i].getText();

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
              } catch (NumberFormatException ignored) {
              }
          }
      }

      if (csd.isEmpty()) {
          definitions.clientScriptData = null;
      } else {
          if (definitions.clientScriptData == null || !definitions.clientScriptData.equals(csd)) {
              definitions.clientScriptData = new HashMap<>(csd);
          }
      }
      */

            this.definitions.write(ObjectSelection.Companion.getCACHE());
            this.objectSelection.updateObjectDefs(this.definitions);

            JOptionPane.showMessageDialog(this, "Object saved.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving object: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void
    export () {
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
        for (String option: options) {
            sb.append(option == null ? "null" : option).append(";");
        }
        return sb.toString();
    }

    private String intArrayToSemicolonString(int[] arr) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i: arr) {
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
        for (String part: parts) {
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
        for (String pair: pairs) {
            String[] split = pair.split(":");
            if (split.length == 2) {
                try {
                    originals[idx++] = Integer.parseInt(split[0].trim());
                } catch (NumberFormatException ignored) {}
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
        for (String pair: pairs) {
            String[] split = pair.split(":");
            if (split.length == 2) {
                try {
                    modified[idx++] = Integer.parseInt(split[1].trim());
                } catch (NumberFormatException ignored) {}
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
            intArr[i] = arr[i] & 0xFFFF;
        }
        return intArr;
    }
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    private String joinArray(String[] arr, String sep) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String s: arr) {
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
        for (int v: arr) {
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