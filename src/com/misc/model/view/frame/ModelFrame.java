package com.misc.model.view.frame;

import com.cache.store.Cache;
import com.cache.util.Utils;
import com.misc.model.view.render.Canvas;
import com.misc.model.view.render.Model;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class ModelFrame extends JFrame {
    public static Cache Cache;
    private JTextField txtModel;
    private final DefaultListModel<Integer> listModelM = new DefaultListModel<>();
    private final JList<Integer> listModel = new JList<>(listModelM);
    private final JFileChooser chooser = new JFileChooser();
    private final JButton btnDelete = new JButton("Delete");
    private ModelPanel modelPanel;
    private Thread modelViewThread;
    private Model model = null;
    private final JTabbedPane tabbedPane;  // Add a tabbed pane

    public ModelFrame(String cache) throws IOException {
        Cache = new Cache(cache);
        loadCache(Cache);
        setTitle("Model Viewer");
        setDefaultCloseOperation(1);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        JPanel cachePanel = createCachePanel();
        tabbedPane.addTab("cache", cachePanel);

        JPanel datPanel = createDatPanel();
        tabbedPane.addTab("Folder", datPanel);


    }

    @NotNull
    private static JTextField getJTextField(DefaultListModel<String> datListModel, JList<String> datList) {
        JTextField txtFilter = new JTextField();
        txtFilter.setPreferredSize(new Dimension(100, 25));
        txtFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String filterText = txtFilter.getText().toLowerCase();
                DefaultListModel<String> filteredModel = new DefaultListModel<>();
                for (int i = 0; i < datListModel.getSize(); i++) {
                    String fileName = datListModel.getElementAt(i);
                    if (fileName.toLowerCase().contains(filterText)) {
                        filteredModel.addElement(fileName);
                    }
                }
                datList.setModel(filteredModel);
            }
        });
        return txtFilter;
    }

    public static void main(String[] args) {
        try {
            String cache = "";
            ModelFrame frame = new ModelFrame(cache);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createCachePanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(controlPanel, BorderLayout.WEST);

        JScrollPane scrollPaneModel = new JScrollPane();
        scrollPaneModel.setPreferredSize(new Dimension(200, 400));
        controlPanel.add(scrollPaneModel);
        scrollPaneModel.setViewportView(listModel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(5, 1, 5, 5));
        controlPanel.add(buttonsPanel);
        listModel.addListSelectionListener(arg0 -> {
            if (!modelPanel.loaded) {
                modelPanel.loaded = true;
                modelPanel.render();
                modelViewThread = new Thread(modelPanel);
                modelViewThread.start();
                Canvas.load();
            }
            int id = listModel.getSelectedIndex();
            byte[] data = Cache.getIndexes()[7].getFile(id, 0, null);
            if (data != null) {
                model = new Model(id, data);
                modelPanel.loadModelFromBytes(data);
            }
        });
        txtModel = new JTextField("", 10);
        txtModel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    int id = 0;
                    try {
                        id = Integer.parseInt(txtModel.getText());
                    } catch (Exception error) {
                        id = 0;
                    }
                    listModel.setSelectedIndex(id);
                    listModel.ensureIndexIsVisible(id);
                }
            }
        });
        buttonsPanel.add(txtModel);

        JLabel lblModels = new JLabel("Models", JLabel.CENTER);
        buttonsPanel.add(lblModels);

        JButton btnReplace = createButton("Replace", e -> {
            File file = getFile();
            if (file != null) {
                replaceCustomModel(Cache, file, 64);
            }
        });
        buttonsPanel.add(btnReplace);

        JButton btnDelete = createButton("Delete", e -> {
            int view = listModel.getSelectedIndex() - 1;
            Cache.getIndexes()[7].removeArchive(listModel.getSelectedIndex() + 1);
            listModelM.remove(listModel.getSelectedIndex());
            listModel.setSelectedIndex(view > -1 ? view : 0);
            listModel.ensureIndexIsVisible(view > -1 ? view : 0);
        });
        buttonsPanel.add(btnDelete);

        JButton btnAdd = createButton("Add", e -> {
            File file = getFile();
            if (file != null) {
                int index = addCustomModel(Cache, file);
                if (index != -1) listModelM.addElement(index);
                listModel.setSelectedIndex(index);
                listModel.ensureIndexIsVisible(index);
            }
        });
        buttonsPanel.add(btnAdd);

        JButton btnDump = createButton("Dump", e -> {
            try {
                dumpModel(Cache.getIndexes()[7].getFile(64, 0, null), "" + model.modelId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        buttonsPanel.add(btnDump);

        modelPanel = new ModelPanel();
        JScrollPane modelScrollPane = new JScrollPane(modelPanel);
        modelScrollPane.setPreferredSize(new Dimension(350, 400));
        mainPanel.add(modelScrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createDatPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(controlPanel, BorderLayout.WEST);

        DefaultListModel<String> datListModel = new DefaultListModel<>();
        JList<String> datList = new JList<>(datListModel);
        JScrollPane scrollPaneDat = new JScrollPane(datList);
        scrollPaneDat.setPreferredSize(new Dimension(200, 650));
        controlPanel.add(scrollPaneDat);

        JLabel lblSearch = new JLabel("Search");

        lblSearch.setFont(new Font("Default", Font.PLAIN, 12));
        lblSearch.setText("");
        controlPanel.add(lblSearch);
        JTextField txtFilter = getJTextField(datListModel, datList);
        controlPanel.add(txtFilter);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 10, 10));
        controlPanel.add(buttonsPanel);

        JButton loadDatButton = getJButton(datListModel);

        buttonsPanel.add(loadDatButton);

        JButton processDatButton = new JButton("Nothing");
        processDatButton.addActionListener(e -> {
            String selectedFileName = datList.getSelectedValue();
            if (selectedFileName != null) {
                System.out.println("Processing file: " + selectedFileName);
            }
        });
        buttonsPanel.add(processDatButton);
        return mainPanel;
    }

    @NotNull
    private JButton getJButton(DefaultListModel<String> datListModel) {
        JButton loadDatButton = new JButton("Select Folder");
        loadDatButton.addActionListener(e -> {
            String directoryPath = getDirectory();
            if (directoryPath != null) {
                File folder = new File(directoryPath);
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    datListModel.clear();
                    for (File file : files) {
                        datListModel.addElement(file.getName());
                    }
                }
            }
        });
        return loadDatButton;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    private File getFile() {
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                return chooser.getSelectedFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getDirectory() {
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.isDirectory()) {
                try {
                    return file.getAbsolutePath() + "/";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void replaceCustomModel(Cache cache, File file, int index) {
        try {
            Utils.packCustomModel(cache, getBytesFromFile(file), index);
        } catch (IOException ex) {
            System.out.println("Error Packing Custom Model.");
        }
    }

    public int addCustomModel(Cache cache, File file) {
        try {
            return Utils.packCustomModel(cache, getBytesFromFile(file), cache.getIndexes()[7].getLastArchiveId() + 1);
        } catch (IOException ex) {
            System.out.println("Error Packing Custom Model.");
        }
        return -1;
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        if (file == null) return null;
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
            offset += numRead;
        if (offset < bytes.length) {
            System.out.println("Error");
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public void dumpModel(byte[] bytes, String name) throws IOException {
        if (bytes == null) return;
        FileOutputStream fileOuputStream = null;
        try {
            String dir = getDirectory() + name + ".dat";
            File outputFile = new File(dir);
            outputFile.createNewFile();
            fileOuputStream = new FileOutputStream(outputFile);
            fileOuputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                fileOuputStream.close();
            }
        }
    }

    private void loadCache(Cache cache) {
        cache.getIndexes()[7].getArchive(64);
        for (int id = 0; id < cache.getIndexes()[7].getLastArchiveId(); id++) {
            listModelM.addElement(id);
        }
    }

}
