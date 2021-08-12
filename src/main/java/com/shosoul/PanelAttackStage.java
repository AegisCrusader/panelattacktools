package com.shosoul;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class PanelAttackStage {
    private static final String DEFAULT_ID_PREFIX = "pa_stages";
    private Path configPath;
    private JSONObject configjson;
    private String id;
    private String name;

    private boolean isEnabled;
    private boolean isDefault;

    /**
     * @return whether or not the stage is a default stage
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * A class containing information about a stage.
     * 
     * @param configPath the path to the character config.json file
     * @throws IOException if the configPath does not lead to a valid config file
     */
    public PanelAttackStage(Path configPath) throws IOException {
        this.configPath = configPath;
        configjson = new JSONObject(Files.readString(configPath));
        id = configjson.getString("id");
        name = configjson.getString("name");
        isEnabled = !configPath.getParent().getFileName().toString().startsWith("__");
        isDefault = id.startsWith(DEFAULT_ID_PREFIX);
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
    private void setConfigPath(Path configPath) {
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
     * @return if the stage is enabled or not
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    private void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Path getStageFolder() {
        return configPath.getParent();
    }

    /**
     * Toggles the stage.
     * 
     * @return if the stage was toggled successfully.
     */
    public boolean toggleStage() {
        String oldDirName = getStageFolder().getFileName().toString();
        try {
            String newDirName;
            if (oldDirName.startsWith("__")) {
                newDirName = oldDirName.substring(2);
            } else {
                newDirName = "__" + oldDirName;
            }
            Path newParentPath = Files.move(getStageFolder(), getStageFolder().resolveSibling(newDirName));
            setConfigPath(newParentPath.resolve("config.json"));
            System.out.println(configPath.toAbsolutePath());
            setEnabled(!isEnabled);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
