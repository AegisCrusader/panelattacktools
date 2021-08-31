package com.shosoul;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PanelAttackTheme {
    private static final String DEFAULT_THEME_NAME = "Panel Attack";
    private Path charactertxtPath;
    private Path stagetxtPath;
    private ArrayList<String> characterTxtIds = new ArrayList<>();
    private ArrayList<String> stageTxtIds = new ArrayList<>();

    private Path themePath;
    private final String id;
    private String name;

    private boolean isEnabled;
    private boolean isDefault;
    private boolean isDisabledByGrandParent;

    /**
     * @return whether or not the theme is a default theme (such as yoshi, lip,
     *         bowser, etc.)
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * A class containing information about a character.
     * 
     * @param themePath the path to the theme config.json file
     * @throws IOException if the configPath does not lead to a valid config file
     */
    public PanelAttackTheme(Path themePath) throws IOException {
        this.themePath = themePath;
        String fileName = this.themePath.getFileName().toString();
        if (fileName.startsWith("__")) {
            id = fileName.substring(2);
        } else {
            id = fileName;
        }
        name = id;
        isEnabled = !themePath.getFileName().toString().startsWith("__");
        isDefault = id.equals(DEFAULT_THEME_NAME);
        charactertxtPath = themePath.resolve("characters.txt");
        stagetxtPath = themePath.resolve("stages.txt");
        if (charactertxtPath.toFile().exists()) {
            Files.readAllLines(charactertxtPath).forEach(characterTxtIds::add);
        }
        if (stagetxtPath.toFile().exists()) {
            Files.readAllLines(stagetxtPath).forEach(stageTxtIds::add);
        }
    }

    /**
     * @return the themePath
     */
    public Path getThemePath() {
        return themePath;
    }

    /**
     * @param themePath the configPath to set
     */
    public void setThemePath(Path themePath) {
        this.themePath = themePath;
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
     * @return if the theme is enabled or not
     */
    public boolean isEnabled() {
        this.isEnabled = !themePath.getFileName().toString().startsWith("__");
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    private void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Toggles the character.
     * 
     * @return if the theme was toggled successfully.
     */
    public boolean toggleTheme() {
        String oldPath = themePath.getFileName().toString();
        try {
            String newDirName;
            if (oldPath.startsWith("__")) {
                newDirName = oldPath.substring(2);
            } else {
                newDirName = "__" + oldPath;
            }
            Path newPath = Files.move(this.themePath, themePath.resolveSibling(newDirName));
            setThemePath(newPath);
            // System.out.println(configPath.toAbsolutePath());
            setEnabled(!isEnabled);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * @return the isDisabledByGrandParent
     */
    public boolean isDisabledByGrandParent() {
        Path relativePath = Main.getPanelAttackDir().resolve("characters")
                .relativize(themePath.getParent().getParent());
        if (relativePath.toString().contains("__")) {
            isDisabledByGrandParent = true;
        }
        return isDisabledByGrandParent;

    }

    /**
     * @return the characterTxtIds
     */
    public ArrayList<String> getCharacterTxtIds() {
        characterTxtIds.clear();
        if (charactertxtPath.toFile().exists()) {
            try {
                Files.readAllLines(charactertxtPath).forEach(characterTxtIds::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return characterTxtIds;
    }

    public void addCharacterToTxt(PanelAttackCharacter character) {
        String character_id = character.getId();
        characterTxtIds.add(character_id);
        try (FileWriter fw = new FileWriter(charactertxtPath.toFile())) {
            for (String characterid : characterTxtIds) {
                fw.write(characterid);
                fw.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void removeCharacterFromTxt(PanelAttackCharacter character) {
        String character_id = character.getId();
        characterTxtIds.remove(character_id);
        try (FileWriter fw = new FileWriter(charactertxtPath.toFile())) {
            for (String characterid : characterTxtIds) {
                fw.write(characterid);
                fw.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void addStageToTxt(PanelAttackStage stage) {
        String stage_id = stage.getId();
        stageTxtIds.add(stage_id);
        try (FileWriter fw = new FileWriter(stagetxtPath.toFile())) {
            for (String stageid : stageTxtIds) {
                fw.write(stageid);
                fw.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void removeStageFromTxt(PanelAttackStage stage) {
        String stage_id = stage.getId();
        stageTxtIds.remove(stage_id);
        try (FileWriter fw = new FileWriter(stagetxtPath.toFile())) {
            for (String stageid : stageTxtIds) {
                fw.write(stageid);
                fw.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return the stageTxtIds
     */
    public ArrayList<String> getStageTxtIds() {
        stageTxtIds.clear();
        if (stagetxtPath.toFile().exists()) {
            try {
                Files.readAllLines(stagetxtPath).forEach(stageTxtIds::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stageTxtIds;
    }

    public static List<PanelAttackTheme> getThemes(boolean addDefaults) {
        List<PanelAttackTheme> themeArrayList = new ArrayList<>();
        int failedLoads = 0;
        Path themeDir = Paths.get(Main.getAppDataDirectory()).resolve("Panel Attack").resolve("themes");
        try (Stream<Path> pathStream = Files.walk(themeDir, 1)) {
            List<Path> dirPaths = new ArrayList<>();
            pathStream.parallel()// parallel streams are better
                    .filter(Predicate.not(themeDir::equals))// don't list itself
                    .filter(Files::isDirectory)// check if a directory
                    .forEachOrdered(dirPaths::add);

            for (Path path : dirPaths) {
                try {
                    PanelAttackTheme theme = new PanelAttackTheme(path);
                    themeArrayList.add(theme);
                } catch (Exception e) {
                    e.printStackTrace();
                    failedLoads += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO Sort by mod or alphabetically or something
        return themeArrayList;
    }
}
