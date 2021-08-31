package com.shosoul;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;

public class PanelAttackPanelSet {
    private static final String DEFAULT_ID = "Panel Attack";
    private Path configPath;
    private JSONObject configjson;
    private String id;
    private String name;

    private boolean isEnabled;
    private boolean isDefault;
    private boolean isDisabledByGrandParent;

    /**
     * @return whether or not the panel set is a default panel set
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * A class containing information about a panel set.
     * 
     * @param configPath the path to the panel set config.json file
     * @throws IOException if the configPath does not lead to a valid config file
     */
    public PanelAttackPanelSet(Path configPath) throws IOException {
        this.configPath = configPath;
        configjson = new JSONObject(Files.readString(configPath));
        id = configjson.getString("id");
        String dirName = this.configPath.getParent().getFileName().toString();
        if (dirName.startsWith("__")) {
            name = dirName.substring(2);
        } else {
            name = dirName;
        }
        isEnabled = !configPath.getParent().getFileName().toString().startsWith("__");
        isDefault = id.equals(DEFAULT_ID);
    }

    public PanelAttackPanelSet(Path configPath, JSONObject configjson) {
        this.configPath = configPath;
        this.configjson = configjson;
        id = configjson.getString("id");
        String dirName = this.configPath.getParent().getFileName().toString();
        if (dirName.startsWith("__")) {
            name = dirName.substring(2);
        } else {
            name = dirName;
        }
        isEnabled = !configPath.getParent().getFileName().toString().startsWith("__");
        isDefault = id.equals(DEFAULT_ID);
    }

    /**
     * @return the configPath
     */
    public Path getConfigPath() {
        return configPath;
    }

    /**
     * @param configPath the configPath to set
     */
    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
    }

    /**
     * @return the configjson
     */
    public JSONObject getConfigjson() {
        return configjson;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return if the panel set is enabled or not
     */
    public boolean isEnabled() {
        isEnabled = !configPath.getParent().getFileName().toString().startsWith("__");
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    private void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Path getPanelSetFolder() {
        return configPath.getParent();
    }

    /**
     * Toggles the stage.
     * 
     * @return if the panel set was toggled successfully.
     */
    public boolean togglePanelSet() {
        String oldDirName = getPanelSetFolder().getFileName().toString();
        try {
            String newDirName;
            if (oldDirName.startsWith("__")) {
                newDirName = oldDirName.substring(2);
            } else {
                newDirName = "__" + oldDirName;
            }
            Path newParentPath = Files.move(getPanelSetFolder(), getPanelSetFolder().resolveSibling(newDirName));
            setConfigPath(newParentPath.resolve("config.json"));
            setEnabled(!isEnabled);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean isDisabledByGrandParent() {
        Path relativePath = Main.getPanelAttackDir().resolve("panels").relativize(configPath.getParent().getParent());
        if (relativePath.toString().contains("__")) {
            isDisabledByGrandParent = true;
        }
        return isDisabledByGrandParent;

    }

    public static List<PanelAttackPanelSet> getPanelSets(boolean addDefaults) {
        List<PanelAttackPanelSet> panelsetArrayList = new ArrayList<>();
        int failedLoads = 0;
        // Find all the config.json files to determine panel set folders
        try (Stream<Path> pathStream = Files
                .walk(Paths.get(Main.getAppDataDirectory()).resolve("Panel Attack").resolve("panels"))) {
            List<Path> configPaths = new ArrayList<>();
            pathStream.parallel()// parallel streams are better
                    .filter(Files::isRegularFile)// check if not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json"))// find files
                    .forEachOrdered(configPaths::add);

            for (Path path : configPaths) {
                try {
                    PanelAttackPanelSet panelset = new PanelAttackPanelSet(path);
                    panelsetArrayList.add(panelset);
                } catch (Exception e) {

                    try {
                        JSONObject repairedjson = Main.repairjson(Files.readString(path));
                        if (repairedjson != null) {
                            PanelAttackPanelSet repairedPanelSet = new PanelAttackPanelSet(path, repairedjson);
                            panelsetArrayList.add(repairedPanelSet);
                        } else {
                            failedLoads += 1;
                        }

                    } catch (Exception e1) {
                        failedLoads += 1;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * If the modloader should also list default panels. If not, remove them.
         */
        if (!addDefaults) {
            panelsetArrayList.removeIf(PanelAttackPanelSet::isDefault);
        }
        // TODO Sort by mod or alphabetically or something
        return panelsetArrayList;
    }

}
