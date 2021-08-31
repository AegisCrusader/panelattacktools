package com.shosoul;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ModManager extends JPanel implements ActionListener {
    protected static Map<JCheckBox, PanelAttackCharacter> characterCheckboxMap = new HashMap<>();
    protected static Map<JCheckBox, PanelAttackStage> stageCheckboxMap = new HashMap<>();
    protected static Map<JCheckBox, PanelAttackTheme> themeCheckboxMap = new HashMap<>();
    protected static Map<JCheckBox, PanelAttackPanelSet> panelsetCheckboxMap = new HashMap<>();

    protected static Map<JRadioButton, PanelAttackTheme> themeRadioButtonMap = new HashMap<>();
    protected static Map<JCheckBox, PanelAttackCharacter> themeCharacterCheckboxMap = new HashMap<>();
    protected static Map<JCheckBox, PanelAttackStage> themeStageCheckboxMap = new HashMap<>();
    private List<JPanel> panelList = new ArrayList<>();
    static JFrame frame;

    static JMenuBar menuBar = new JMenuBar();

    JTabbedPane tabbedPane = new JTabbedPane();
    JComponent modManagerTab = makeGridPanel("Toggleables", 0, 4);
    JComponent themeManagerTab = makeGridPanel("Theme Manager", 0, 3);
    JComponent modCreatorTab = makeGridPanel("Mod Creator", 0, 3);

    JComponent modPanel = makePanel("Character Panel", true);
    JComponent stagePanel = makePanel("Stage Panel", true);
    JComponent themePanel = makePanel("Theme Panel", true);
    JComponent panelsetPanel = makePanel("PanelSet Panel", true);
    JComponent themeSelectPanel = makePanel("Theme Select Panel", true);
    JComponent charactertxtPanel = makePanel("Theme Mod Panel", true);
    JComponent stagetxtPanel = makePanel("Theme Stage Panel", true);
    JComponent modCreatorPanel = new JPanel(new BorderLayout());

    JScrollPane modScrollPane = new JScrollPane(modPanel);
    JScrollPane stageScrollPane = new JScrollPane(stagePanel);
    JScrollPane themeScrollPane = new JScrollPane(themePanel);
    JScrollPane panelsetScrollPane = new JScrollPane(panelsetPanel);
    JScrollPane themeSelectScrollPane = new JScrollPane(themeSelectPanel);
    JScrollPane modtxtScrollPane = new JScrollPane(charactertxtPanel);
    JScrollPane stagetxtScrollPane = new JScrollPane(stagetxtPanel);

    String[] modCreatorTypes = { "Character", "Stage" };

    JMenu fileMenu;
    JMenu editMenu;
    JMenu viewMenu;
    JMenuItem refreshItem;
    JMenuItem installItem;
    JMenuItem openItem;
    JMenuItem exitItem;
    JCheckBoxMenuItem viewDefaults = new JCheckBoxMenuItem("Show defaults");
    ButtonGroup themeRadioButtonGroup = new ButtonGroup();

    public ModManager() {
        super(new GridLayout(1, 0));
        panelList.add((JPanel) modPanel);
        panelList.add((JPanel) stagePanel);
        panelList.add((JPanel) themePanel);
        panelList.add((JPanel) panelsetPanel);
        panelList.add((JPanel) themeSelectPanel);
        panelList.add((JPanel) charactertxtPanel);
        panelList.add((JPanel) stagetxtPanel);

        // #region toggle panels

        modScrollPane.setColumnHeaderView(new JLabel("Characters", SwingConstants.CENTER));
        modScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modManagerTab.add(modScrollPane);

        stageScrollPane.setColumnHeaderView(new JLabel("Stages", SwingConstants.CENTER));
        stageScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modManagerTab.add(stageScrollPane);

        themeScrollPane.setColumnHeaderView(new JLabel("Themes", SwingConstants.CENTER));
        themeScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modManagerTab.add(themeScrollPane);

        panelsetScrollPane.setColumnHeaderView(new JLabel("Panel Sets", SwingConstants.CENTER));
        panelsetScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modManagerTab.add(panelsetScrollPane);

        themeSelectScrollPane.setColumnHeaderView(new JLabel("Themes", SwingConstants.CENTER));
        themeSelectScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        themeManagerTab.add(themeSelectScrollPane);

        modtxtScrollPane.setColumnHeaderView(new JLabel("characters.txt", SwingConstants.CENTER));
        modtxtScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        themeManagerTab.add(modtxtScrollPane);

        stagetxtScrollPane.setColumnHeaderView(new JLabel("stages.txt", SwingConstants.CENTER));
        stagetxtScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        themeManagerTab.add(stagetxtScrollPane);
        // #endregion

        modCreatorTab.add(modCreatorPanel);

        tabbedPane.addTab("Mods", modManagerTab);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.addTab("Theme Manager", themeManagerTab);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        // Add the tabbed pane to this panel.
        this.add(tabbedPane);

        // The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        updatePanels();
        // #region configuring menu
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        viewMenu = new JMenu("View");

        refreshItem = new JMenuItem("Refresh");
        installItem = new JMenuItem("Install Mod ZIP Folder");
        openItem = new JMenuItem("Open Panel Attack Folder");
        exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(this);
        installItem.addActionListener(this);
        openItem.addActionListener(this);
        exitItem.addActionListener(this);
        viewDefaults.addActionListener(this);

        // loadItem.setIcon(loadIcon);
        // saveItem.setIcon(saveIcon);
        // exitItem.setIcon(exitIcon);

        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt + f for file
        editMenu.setMnemonic(KeyEvent.VK_E); // Alt + e for edit
        viewMenu.setMnemonic(KeyEvent.VK_V); // Alt + v for view
        refreshItem.setMnemonic(KeyEvent.VK_R); // r for refresh
        openItem.setMnemonic(KeyEvent.VK_O); // o for open
        exitItem.setMnemonic(KeyEvent.VK_E); // e for exit

        fileMenu.add(installItem);
        fileMenu.add(openItem);
        fileMenu.add(exitItem);

        viewMenu.add(refreshItem);
        viewMenu.addSeparator();
        viewMenu.add(viewDefaults);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        // Validation is not ready yet.
        // tabbedPane.setEnabledAt(2, false);

        // Modmaker is not ready yet.
        // tabbedPane.setEnabledAt(3, false);
        // #endregion
    }

    protected JComponent makePanel(String text) {
        JPanel panel = new JPanel(true);
        panel.setName(text);
        panel.setLayout(new FlowLayout());
        return panel;
    }

    protected JComponent makePanel(String name, boolean doBoxLayout) {
        JPanel panel = new JPanel(true);
        panel.setName(name);
        if (doBoxLayout) {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }
        return panel;
    }

    /**
     * Creates a JPanel with a GridLayout and the specified amount of rows and
     * columns.
     * 
     * @param name The name of the panel.
     * @param rows The amount of rows to add.
     * @param cols The amount of columns.
     * @return The JPanel
     */
    protected JComponent makeGridPanel(String name, int rows, int cols) {
        JPanel panel = new JPanel(true);
        panel.setName(name);
        panel.setLayout(new GridLayout(rows, cols));
        return panel;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL;
        try {
            imgURL = Paths.get(path).toUri().toURL();
            if (!Paths.get(path).toFile().exists()) {
                imgURL = null;
            }
        } catch (MalformedURLException e) {
            imgURL = null;
            System.out.println("F");
        }

        if (!Objects.isNull(imgURL)) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked
     * from the event dispatch thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        frame = new JFrame("Panel Attack Mod Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add content to the window.
        frame.add(new ModManager(), BorderLayout.CENTER);
        frame.setJMenuBar(menuBar);
        // Display the window.
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean toggledCheckBox = false;
        if (e.getSource() == refreshItem) {
            System.out.println("Refreshed Page.");
            updatePanels();
        }
        if (e.getSource() == installItem) {
            ZIPManager.installMods(frame);
            updatePanels();

        }
        if (e.getSource() == openItem) {
            Desktop desktop = null; // On Windows, retrieve the path of the "Panel Attack" folder
            File file = Main.getPanelAttackDir().toFile();

            try {
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                    desktop.open(file);
                } else {
                    System.out.println("Desktop is not supported");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == exitItem) {
            System.exit(0);
        }
        if (e.getSource() == viewDefaults) {
            final boolean showDefaults = viewDefaults.isSelected();
            for (Map.Entry<JCheckBox, PanelAttackCharacter> characterSet : characterCheckboxMap.entrySet()) {
                if (characterSet.getValue().isDefault()) {
                    characterSet.getKey().setVisible(showDefaults);
                }
            }
            for (Map.Entry<JCheckBox, PanelAttackStage> stageSet : stageCheckboxMap.entrySet()) {
                if (stageSet.getValue().isDefault()) {
                    stageSet.getKey().setVisible(showDefaults);
                }
            }
            for (Map.Entry<JCheckBox, PanelAttackTheme> themeSet : themeCheckboxMap.entrySet()) {
                if (themeSet.getValue().isDefault()) {
                    themeSet.getKey().setVisible(showDefaults);
                }
            }
            for (Map.Entry<JCheckBox, PanelAttackPanelSet> panelsetSet : panelsetCheckboxMap.entrySet()) {
                if (panelsetSet.getValue().isDefault()) {
                    panelsetSet.getKey().setVisible(showDefaults);
                }
            }
            for (Map.Entry<JCheckBox, PanelAttackCharacter> themeCharacterSet : themeCharacterCheckboxMap.entrySet()) {
                if (themeCharacterSet.getValue().isDefault()) {
                    themeCharacterSet.getKey().setVisible(showDefaults || themeCharacterSet.getKey().isSelected());
                }
            }
            for (Map.Entry<JCheckBox, PanelAttackStage> themeStageSet : themeStageCheckboxMap.entrySet()) {
                if (themeStageSet.getValue().isDefault()) {
                    themeStageSet.getKey().setVisible(showDefaults || themeStageSet.getKey().isSelected());
                }
            }
        }

        if (characterCheckboxMap.containsKey(e.getSource())) {
            List<PanelAttackCharacter> newCharacters = new ArrayList<>();
            PanelAttackCharacter.getCharacters(true).forEach(newCharacters::add);
            // Retrieve the current checkboxes
            for (Map.Entry<JCheckBox, PanelAttackCharacter> oldCharacterSet : characterCheckboxMap.entrySet()) {
                // Getting and finalizing the needed components for speed so that the method is
                // not called and instantiated several times
                final String old_id = oldCharacterSet.getValue().getId();
                final Path oldPath = oldCharacterSet.getValue().getConfigPath();
                for (PanelAttackCharacter newPanelAttackCharacter : newCharacters) {
                    // Match the character
                    if (newPanelAttackCharacter.getId().equals(old_id)) {
                        // Check if the path has been changed
                        if (!newPanelAttackCharacter.getConfigPath().equals(oldPath)) {
                            oldCharacterSet.setValue(newPanelAttackCharacter);
                            oldCharacterSet.getKey().setSelected(newPanelAttackCharacter.isEnabled());
                            oldCharacterSet.getKey().setEnabled(!newPanelAttackCharacter.isDisabledByGrandParent());

                        }

                    }
                }

                if (oldCharacterSet.getKey().equals(e.getSource()) && !toggledCheckBox) {
                    characterCheckboxMap.get(e.getSource()).toggleCharacter();

                    oldCharacterSet.getKey().setSelected(oldCharacterSet.getValue().isEnabled());
                    oldCharacterSet.getKey().setEnabled(!oldCharacterSet.getValue().isDisabledByGrandParent());
                    toggledCheckBox = true;
                }

                if (oldCharacterSet.getValue().isEnabled()) {
                    // oldCharacterSet.getKey().setBackground(Color.GREEN);
                } else {
                    // oldCharacterSet.getKey().setBackground(Color.RED);
                }

            }
        }
        if (stageCheckboxMap.containsKey(e.getSource())) {
            List<PanelAttackStage> newStages = new ArrayList<>();
            PanelAttackStage.getStages(false).forEach(newStages::add);
            // Retrieve the current checkboxes
            for (Map.Entry<JCheckBox, PanelAttackStage> oldStageSet : stageCheckboxMap.entrySet()) {
                // Getting and finalizing the needed components for speed so that the method is
                // not called and instantiated several times
                final String old_id = oldStageSet.getValue().getId();
                final Path oldPath = oldStageSet.getValue().getConfigPath();
                for (PanelAttackStage newPanelAttackStage : newStages) {
                    // Match the stage
                    if (newPanelAttackStage.getId().equals(old_id)) {
                        // Check if the path has been changed
                        if (!newPanelAttackStage.getConfigPath().equals(oldPath)) {
                            oldStageSet.setValue(newPanelAttackStage);
                            oldStageSet.getKey().setSelected(newPanelAttackStage.isEnabled());
                            oldStageSet.getKey().setEnabled(!newPanelAttackStage.isDisabledByGrandParent());
                        }
                    }
                }

                if (oldStageSet.getKey().equals(e.getSource()) && !toggledCheckBox) {
                    stageCheckboxMap.get(e.getSource()).toggleStage();

                    oldStageSet.getKey().setSelected(oldStageSet.getValue().isEnabled());
                    oldStageSet.getKey().setEnabled(!oldStageSet.getValue().isDisabledByGrandParent());
                    toggledCheckBox = true;

                }
            }
        }
        if (themeCheckboxMap.containsKey(e.getSource())) {
            themeCheckboxMap.get(e.getSource()).toggleTheme();

        }
        if (panelsetCheckboxMap.containsKey(e.getSource())) {
            List<PanelAttackPanelSet> newStages = new ArrayList<>();
            PanelAttackPanelSet.getPanelSets(false).forEach(newStages::add);
            // Retrieve the current checkboxes
            for (Map.Entry<JCheckBox, PanelAttackPanelSet> oldpanelSet : panelsetCheckboxMap.entrySet()) {
                // Getting and finalizing the needed components for speed so that the method is
                // not called and instantiated several times
                final String old_id = oldpanelSet.getValue().getId();
                final Path oldPath = oldpanelSet.getValue().getConfigPath();
                for (PanelAttackPanelSet newPanelAttackPanelSet : newStages) {
                    // Match the panelset
                    if (newPanelAttackPanelSet.getId().equals(old_id)) {
                        // Check if the path has been changed
                        if (!newPanelAttackPanelSet.getConfigPath().equals(oldPath)) {
                            oldpanelSet.setValue(newPanelAttackPanelSet);
                            oldpanelSet.getKey().setSelected(newPanelAttackPanelSet.isEnabled());
                            oldpanelSet.getKey().setEnabled(!newPanelAttackPanelSet.isDisabledByGrandParent());
                        }
                    }
                }

                if (oldpanelSet.getKey().equals(e.getSource()) && !toggledCheckBox) {
                    panelsetCheckboxMap.get(e.getSource()).togglePanelSet();

                    oldpanelSet.getKey().setSelected(oldpanelSet.getValue().isEnabled());
                    oldpanelSet.getKey().setEnabled(!oldpanelSet.getValue().isDisabledByGrandParent());
                    toggledCheckBox = true;
                }
            }
        }
        if (themeRadioButtonMap.containsKey(e.getSource())) {
            ArrayList<PanelAttackCharacter> newCharacters = new ArrayList<>();
            PanelAttackCharacter.getCharacters(true).forEach(newCharacters::add);

            ArrayList<String> characterTxtIds = themeRadioButtonMap.get(e.getSource()).getCharacterTxtIds();
            characterTxtIds.forEach(String::trim);
            ArrayList<String> stageTxtIds = themeRadioButtonMap.get(e.getSource()).getStageTxtIds();
            stageTxtIds.forEach(String::trim);

            for (Map.Entry<JCheckBox, PanelAttackCharacter> themeCharacterSet : themeCharacterCheckboxMap.entrySet()) {

                String id = themeCharacterSet.getValue().getId().trim();
                if (themeCharacterSet.getValue().isDefault() && !viewDefaults.isSelected()) {
                    themeCharacterSet.getKey().setVisible(false);
                } else {
                    themeCharacterSet.getKey().setVisible(true);

                }

                if (characterTxtIds.contains(id)) {
                    themeCharacterSet.getKey().setVisible(true);
                    themeCharacterSet.getKey().setSelected(true);
                } else {
                    themeCharacterSet.getKey().setSelected(false);
                }

            }

            for (Map.Entry<JCheckBox, PanelAttackStage> themeStageSet : themeStageCheckboxMap.entrySet()) {

                String id = themeStageSet.getValue().getId().trim();
                if (themeStageSet.getValue().isDefault() && !viewDefaults.isSelected()) {
                    themeStageSet.getKey().setVisible(false);
                } else {
                    themeStageSet.getKey().setVisible(true);

                }

                if (stageTxtIds.contains(id)) {
                    themeStageSet.getKey().setVisible(true);
                    themeStageSet.getKey().setSelected(true);
                } else {
                    themeStageSet.getKey().setSelected(false);
                }

            }
        }
        if (themeCharacterCheckboxMap.containsKey(e.getSource())) {
            for (Map.Entry<JRadioButton, PanelAttackTheme> radioThemeSet : themeRadioButtonMap.entrySet()) {
                if (radioThemeSet.getKey().isSelected()) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    if (checkBox.isSelected()) {
                        radioThemeSet.getValue().addCharacterToTxt(themeCharacterCheckboxMap.get(e.getSource()));
                    } else {
                        radioThemeSet.getValue().removeCharacterFromTxt(themeCharacterCheckboxMap.get(e.getSource()));
                    }
                    break;
                }
            }
        }
        if (themeStageCheckboxMap.containsKey(e.getSource())) {
            for (Map.Entry<JRadioButton, PanelAttackTheme> radioThemeSet : themeRadioButtonMap.entrySet()) {
                if (radioThemeSet.getKey().isSelected()) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    if (checkBox.isSelected()) {
                        radioThemeSet.getValue().addStageToTxt(themeStageCheckboxMap.get(e.getSource()));
                    } else {
                        radioThemeSet.getValue().removeStageFromTxt(themeStageCheckboxMap.get(e.getSource()));
                    }
                    break;
                }
            }
        }
    }

    private void updatePanels() {

        for (JPanel jPanel : panelList) {
            jPanel.removeAll();
            jPanel.repaint();
            jPanel.updateUI();
        }
        for (PanelAttackCharacter character : PanelAttackCharacter.getCharacters(true)) {
            ImageIcon icon = createImageIcon(
                    character.getCharacterFolder().resolve("icon.png").toAbsolutePath().toString().replace("\\", "/"));
            JCheckBox item = new JCheckBox(character.getName(), character.isEnabled());

            item.setToolTipText(character.getId());
            item.addActionListener(this);
            item.setEnabled(!character.isDisabledByGrandParent());
            item.setVisible(!character.isDefault() || viewDefaults.isSelected());
            modPanel.add(item);
            characterCheckboxMap.put(item, character);

            JCheckBox themeItem = new JCheckBox(character.getName(), false);
            themeItem.addActionListener(this);
            themeItem.setVisible(false);
            themeCharacterCheckboxMap.put(themeItem, character);
            charactertxtPanel.add(themeItem);

        }

        for (PanelAttackStage stage : PanelAttackStage.getStages(true)) {
            JCheckBox item = new JCheckBox(stage.getName(), stage.isEnabled());
            item.addActionListener(this);
            item.setEnabled(!stage.isDisabledByGrandParent());
            item.setVisible(!stage.isDefault());
            item.setToolTipText(stage.getId());
            stagePanel.add(item);
            stageCheckboxMap.put(item, stage);

            JCheckBox themeItem = new JCheckBox(stage.getName(), false);
            themeItem.addActionListener(this);
            themeItem.setVisible(false);
            themeStageCheckboxMap.put(themeItem, stage);
            stagetxtPanel.add(themeItem);

        }

        for (PanelAttackTheme theme : PanelAttackTheme.getThemes(true)) {
            JCheckBox item = new JCheckBox(theme.getName(), theme.isEnabled());
            item.addActionListener(this);
            item.setVisible(!theme.isDefault());
            themePanel.add(item);
            themeCheckboxMap.put(item, theme);

            JRadioButton themeRadioButton = new JRadioButton(theme.getName());
            themeRadioButton.addActionListener(this);
            themeRadioButton.setVisible(true);
            themeRadioButtonGroup.add(themeRadioButton);
            themeSelectPanel.add(themeRadioButton);
            themeRadioButtonMap.put(themeRadioButton, theme);
        }

        for (PanelAttackPanelSet panelset : PanelAttackPanelSet.getPanelSets(true)) {
            JCheckBox item = new JCheckBox(panelset.getName(), panelset.isEnabled());
            item.addActionListener(this);
            item.setVisible(!panelset.isDefault());
            panelsetPanel.add(item);
            panelsetCheckboxMap.put(item, panelset);

        }
    }
}
