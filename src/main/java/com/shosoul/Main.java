package com.shosoul;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.json.JSONObject;

/**
 *
 */
public class Main {
    public static String panelAttackDir;
    private static boolean includeDefaults = false;

    public static void main(String[] args) {

        panelAttackDir = getAppDataDirectory() + "\\Panel Attack\\";
        if (Files.exists(Paths.get(panelAttackDir))) {
            final String characterDir = panelAttackDir + "characters\\";
            final String stageDir = panelAttackDir + "stages\\";

            Path characterDirPath = Paths.get(new File(characterDir).toURI());
            Path stageDirPath = Paths.get(new File(stageDir).toURI());
            while (true) {
                CLS.clearConsoleScreen();
                int modSelection = UserPrompt.promptForint(
                        "0 - Exit\n1 - Characters\n2 - Stages\n3 - Mod Creator\n4 - Open Panel Attack Folder\n\nEnter option:");
                CLS.clearConsoleScreen();
                switch (modSelection) {
                case 1:
                    doCharacters(characterDirPath);
                    break;
                case 2:
                    doStages(stageDirPath);
                    break;
                case 3:
                    int modDetail = UserPrompt.promptForint("0 - Back\n1 - Basic");
                    CLS.clearConsoleScreen();
                    int modType = 0;
                    if (modDetail != 0) {
                        modType = UserPrompt.promptForint("0 - Back\n1 - Create Character\n2 - Create Stage");

                    }

                    switch (modDetail) {
                    // Back
                    case 0:

                        break;
                    // Basic Mod Maker
                    case 1:
                        switch (modType) {
                        case 0:
                            break;
                        case 1:
                            createMod(characterDir);
                            break;
                        case 2:
                            createMod(stageDir);
                            break;
                        default:
                            break;
                        }
                        break;
                    // Advanced Mod Maker
                    case 2:
                        switch (modType) {
                        case 0:
                            break;
                        case 1:
                            createCharacterMod(characterDir);
                            break;
                        case 2:
                            createMod(stageDir);
                            break;
                        default:
                            break;
                        }
                        break;
                    default:
                        break;
                    }

                    break;
                case 4:
                    Desktop desktop = null;
                    // On Windows, retrieve the path of the "Panel Attack" folder
                    File file = new File(panelAttackDir);

                    try {
                        if (Desktop.isDesktopSupported()) {
                            desktop = Desktop.getDesktop();
                            desktop.open(file);
                        } else {
                            System.out.println("Desktop is not supported");
                        }
                    } catch (IOException e) {
                    }
                    break;
                default:
                    break;
                }
                if (modSelection == 0) {
                    break;
                }
            }
        } else {
            UserPrompt.promptForString("Unable to detect Panel Attack Folder. Press enter to continue...");
        }

        System.exit(0);

    }

    public static void doCharacters(Path characterDirPath) {
        ArrayList<PanelAttackCharacter> characterArrayList = new ArrayList<>();

        // Find all the config.json files to determine character folders
        try (Stream<Path> pathStream = Files.walk(characterDirPath)) {
            pathStream.parallel()// parallel streams are better
                    .filter(Files::isRegularFile)// check if not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json")).map(t -> {
                        try {
                            return new PanelAttackCharacter(t);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).forEach(characterArrayList::add);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * If the modloader should also list default characters. If not, remove them.
         */
        if (!includeDefaults) {
            characterArrayList.removeIf(PanelAttackCharacter::isDefault);
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < characterArrayList.size(); i++) {
                PanelAttackCharacter character = characterArrayList.get(i);
                final String CHARACTER_ENABLED = " [ON]\n";
                final String CHARACTER_DISABLED = " [OFF]\n";
                if (character.isEnabled()) {
                    sb.append(i).append(" - ").append(character.getName()).append(CHARACTER_ENABLED);
                } else {
                    sb.append(i).append(" - ").append(character.getName()).append(CHARACTER_DISABLED);
                }

            }
            sb.append("On - Enable all\n");
            sb.append("Off - Disable all\n");
            sb.append("\n");// create newline
            sb.append(
                    "Type the number of the character to toggle, \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");
            String characterSelection = UserPrompt.promptForString(sb.toString());

            if (!characterSelection.equalsIgnoreCase("back")) {
                // Disable all characters
                if (characterSelection.equalsIgnoreCase("off")) {
                    for (PanelAttackCharacter character : characterArrayList) {
                        if (character.isEnabled()) {
                            character.toggleCharacter();
                        }
                    }
                    // Enable all characters
                } else if (characterSelection.equalsIgnoreCase("on")) {
                    for (PanelAttackCharacter character : characterArrayList) {
                        if (!character.isEnabled()) {
                            character.toggleCharacter();
                        }
                    }
                    // Toggle selected character
                } else {
                    characterArrayList.get(Integer.parseInt(characterSelection)).toggleCharacter();

                }
            }
            CLS.clearConsoleScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void doStages(Path stageDirPath) {
        ArrayList<PanelAttackStage> stageArrayList = new ArrayList<>();

        // Find all the config.json files to determine stage folders
        try (Stream<Path> pathStream = Files.walk(stageDirPath)) {
            pathStream.parallel()// Parallel streams are better
                    .filter(Files::isRegularFile)// Ensure path is not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json")).map(t -> {
                        try {
                            return new PanelAttackStage(t);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).forEach(stageArrayList::add);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * If the modloader should also consider default stages. If not, remove them.
         */
        if (!includeDefaults) {
            stageArrayList.removeIf(PanelAttackStage::isDefault);
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stageArrayList.size(); i++) {
                PanelAttackStage stage = stageArrayList.get(i);

                final String STAGE_ENABLED = " [ON]\n";
                final String STAGE_DISABLED = " [OFF]\n";
                if (stage.isEnabled()) {
                    sb.append(i).append(" - ").append(stage.getName()).append(STAGE_ENABLED);
                } else {
                    sb.append(i).append(" - ").append(stage.getName()).append(STAGE_DISABLED);
                }

            }
            sb.append("On - Enable all\n");
            sb.append("Off - Disable all\n");
            sb.append("\n");
            sb.append(
                    "Type the number of the stage to toggle, \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");
            String stageSelection = UserPrompt.promptForString(sb.toString());

            if (!stageSelection.equalsIgnoreCase("back")) {
                // Disable all stages
                if (stageSelection.equalsIgnoreCase("off")) {
                    for (PanelAttackStage stage : stageArrayList) {
                        if (stage.isEnabled()) {
                            stage.toggleStage();
                        }
                    }
                    // Enable all stages
                } else if (stageSelection.equalsIgnoreCase("on")) {
                    for (PanelAttackStage stage : stageArrayList) {
                        if (!stage.isEnabled()) {
                            stage.toggleStage();
                        }
                    }
                    // Toggle selected stage
                } else {
                    stageArrayList.get(Integer.parseInt(stageSelection)).toggleStage();

                }
            }
            CLS.clearConsoleScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getAppDataDirectory() {
        String operatingSystemString = (System.getProperty("os.name")).toUpperCase();
        String appdataDirectory;
        // here, we assign the name of the OS, according to Java, to a variable...
        // to determine what the workingDirectory is.
        // if it is some version of Windows
        if (operatingSystemString.contains("WIN")) {
            // it is simply the location of the "AppData" folder
            appdataDirectory = System.getenv("AppData");
        }
        // Otherwise, we assume Linux or Mac
        else {
            // in either case, we would start in the user's home directory
            appdataDirectory = System.getProperty("user.home");
            // if we are on a Mac, we are not done, we look for "Application Support"
            appdataDirectory += "/Library/Application Support";
        }
        // we are now free to set the workingDirectory to the subdirectory that is our
        // folder.
        return appdataDirectory;
    }

    // TODO:finish creation of advanced modmaker
    public static boolean createMod(String parentDir) {
        JSONObject configjson = new JSONObject();

        try {
            String folderName = UserPrompt.promptForString("Please name the mod folder:");
            String id = UserPrompt.promptForString("Please enter the id of the mod:");
            String name = UserPrompt.promptForString("Please enter the name of the mod:");

            Path newModFolderPath = Files.createDirectory(Paths.get(parentDir, folderName));
            configjson.put("id", id);
            configjson.put("name", name);
            try (FileWriter fw = new FileWriter(newModFolderPath + "\\config.json")) {

                fw.write(configjson.toString());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean createCharacterMod(String parentDir) {
        JSONObject configjson = new JSONObject();

        try {
            String folderName = UserPrompt.promptForString("Name the character folder:");
            String id = UserPrompt.promptForString("Enter the id for the character:");
            String name = UserPrompt.promptForString("Enter the name for the character:");
            String stage = UserPrompt.promptForString("Enter the stage id for the character (optional):");

            Path newModFolderPath = Files.createDirectory(Paths.get(parentDir, folderName));
            configjson.put("id", id);
            configjson.put("name", name);
            if (!stage.isBlank()) {
                configjson.put("stage", stage);
            }

            try (FileWriter fw = new FileWriter(newModFolderPath + "\\config.json")) {

                fw.write(configjson.toString());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
