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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Main {
    private static Path panelAttackDir = Paths.get(getAppDataDirectory() + "\\Panel Attack\\");
    private static boolean includeDefaults = false;


    public static void main(String[] args) {
        if (Files.exists(panelAttackDir)) {
            final String characterDir = panelAttackDir + "\\characters\\";
            final String stageDir = panelAttackDir + "\\stages\\";
            Path characterDirPath = Paths.get(new File(characterDir).toURI());
            Path stageDirPath = Paths.get(new File(stageDir).toURI());
            while (true) {
                CLS.clearConsoleScreen();
                int modSelection = UserPrompt.promptForint(
                        "0 - Exit\n1 - Characters\n2 - Stages\n3 - Mod Creator\n4 - Open Panel Attack Folder\n5 - Validate config files\n6 - Help\n\nEnter option:");
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

                    Desktop desktop = null; // On Windows, retrieve the path of the "Panel Attack" folder
                    File file = panelAttackDir.toFile();

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

                case 5:
                    validateConfigFiles(panelAttackDir);
                    UserPrompt.promptForString("Press enter to continue...");

                    break;

                case 6:
                    getHelp();
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

    public static JSONObject repairjson(String json) {
        ArrayList<String> lineList = new ArrayList<>();
        StringBuilder line2electricboogaloo = new StringBuilder();
        json.lines().forEachOrdered(lineList::add);

        for (String string : lineList) {
            if (string.trim().equalsIgnoreCase("{") || string.trim().equalsIgnoreCase("}")) {
                line2electricboogaloo.append(string);
            } else if (string.endsWith(",")) {
                line2electricboogaloo.append(string);
            } else {
                String repairAttempt = string + ",";
                line2electricboogaloo.append(repairAttempt);
            }
        }
        try {
            JSONObject configjson = new JSONObject(line2electricboogaloo.toString());
            configjson.getString("id");
            configjson.optString("name");
            return configjson;
        } catch (JSONException e) {
        }

        return null;
    }

    private static void doCharacters(Path characterDirPath) {
        ArrayList<PanelAttackCharacter> characterArrayList = new ArrayList<>();
        int failedLoads = 0;
        // Find all the config.json files to determine character folders
        try (Stream<Path> pathStream = Files.walk(characterDirPath)) {
            ArrayList<Path> configPaths = new ArrayList<>();
            pathStream.parallel()// parallel streams are better
                    .filter(Files::isRegularFile)// check if not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json"))// find files
                    .forEachOrdered(configPaths::add);

            for (Path path : configPaths) {
                try {
                    PanelAttackCharacter character = new PanelAttackCharacter(path);
                    characterArrayList.add(character);
                } catch (Exception e) {

                    try {
                        JSONObject repairedjson = repairjson(Files.readString(path));
                        if (repairedjson != null) {
                            PanelAttackCharacter repairedCharacter = new PanelAttackCharacter(path, repairedjson);
                            characterArrayList.add(repairedCharacter);
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
         * If the modloader should also list default characters. If not, remove them.
         */
        if (!includeDefaults) {
            characterArrayList.removeIf(PanelAttackCharacter::isDefault);
        }
        ArrayList<String> subIDList = new ArrayList<>();
        for (PanelAttackCharacter panelAttackCharacter : characterArrayList) {

            JSONArray array = panelAttackCharacter.getConfigjson().optJSONArray("sub_ids");
            if (array != null) {
                array.toList().forEach(subID -> subIDList.add((String) subID));
            }
        }
        for (String subid : subIDList) {
            characterArrayList.removeIf(character -> character.getId().equals(subid));
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
            if (failedLoads > 0) {
                sb.append("Failed to load ").append(failedLoads)
                        .append(" character(s). Please use the validate function for more details.");
            }
            sb.append("\n");// create newline
            sb.append(
                    "Type the numbers of the characters you wish to toggle, seperated by commas. Otherwise, type \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");
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
                    String[] selections = characterSelection.split("[\\,]+");
                    ArrayList<PanelAttackCharacter> selectedArrayList = new ArrayList<>();

                    for (String string : selections) {
                        int selection = Integer.parseInt(string.trim());
                        selectedArrayList.add(characterArrayList.get(selection));
                    }

                }
            }
            // CLS.clearConsoleScreen();

        } catch (Exception e) {
            e.printStackTrace();
            UserPrompt.promptForString("Press enter to continue...");

        } finally {
            UserPrompt.promptForString("Press enter to continue...");

        }

    }

    private static void doStages(Path stageDirPath) {
        ArrayList<PanelAttackStage> stageArrayList = new ArrayList<>();
        int failedLoads = 0;
        // Find all the config.json files to determine stage folders
        try (Stream<Path> pathStream = Files.walk(stageDirPath)) {
            ArrayList<Path> configPaths = new ArrayList<>();
            pathStream.parallel()// parallel streams are better
                    .filter(Files::isRegularFile)// check if not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json"))// find files
                    .forEachOrdered(configPaths::add);

            for (Path path : configPaths) {
                try {
                    PanelAttackStage character = new PanelAttackStage(path);
                    stageArrayList.add(character);
                } catch (Exception e) {

                    try {
                        JSONObject repairedjson = repairjson(Files.readString(path));
                        if (repairedjson != null) {
                            PanelAttackStage repairedCharacter = new PanelAttackStage(path, repairedjson);
                            stageArrayList.add(repairedCharacter);
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
         * If the modloader should also consider default stages. If not, remove them.
         */
        if (!includeDefaults) {
            stageArrayList.removeIf(PanelAttackStage::isDefault);
        }
        ArrayList<String> subIDList = new ArrayList<>();

        for (PanelAttackStage panelAttackStage : stageArrayList) {

            JSONArray array = panelAttackStage.getConfigjson().optJSONArray("sub_ids");
            if (array != null) {
                array.toList().forEach(subID -> subIDList.add((String) subID));
            }
        }
        for (String subid : subIDList) {
            stageArrayList.removeIf(character -> character.getId().equals(subid));
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
            if (failedLoads > 0) {
                sb.append("Failed to load ").append(failedLoads)
                        .append(" character(s). Please use the validate function for more details.");
            }
            sb.append("\n");
            sb.append(
                    "Type the numbers of the stages you wish to toggle, seperated by commas. Otherwise, type \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");
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
                    String[] selections = stageSelection.split("[\\,]+");
                    for (String string : selections) {
                        int selection = Integer.parseInt(string.trim());
                        stageArrayList.get(selection).toggleStage();

                    }

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
    private static boolean createMod(String parentDir) {
        JSONObject configjson = new JSONObject();

        try {
            String folderName = UserPrompt.promptForString("Please name the mod folder:");
            String id = UserPrompt.promptForString("Please enter the id of the mod:");
            String name = UserPrompt.promptForString("Please enter the name of the mod:");

            Path newModFolderPath = Files.createDirectory(Paths.get(parentDir, folderName));
            configjson.put("id", id);
            configjson.put("name", name);
            try (FileWriter fw = new FileWriter(newModFolderPath + "\\config.json")) {

                fw.write(configjson.toString(1));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static boolean createCharacterMod(String parentDir) {
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

                fw.write(configjson.toString(1));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void validateConfigFiles(Path dir) {
        int configFileTotal = 0;
        int flaggedFileTotal = 0;
        ArrayList<Path> configPaths = new ArrayList<>();

        try (Stream<Path> pathStream = Files.walk(dir)) {
            pathStream.parallel()// parallel streams are better
                    .filter(Files::isRegularFile)// check if not a directory
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json")).forEach(configPaths::add);

        } catch (IOException e) {
        }
        StringBuilder sb = new StringBuilder();
        final String JSON_WARNING_SUFFIX = " [JSON WARNING]\n";
        final String JSON_ERROR_SUFFIX = " [JSON ERROR]\n";
        final String IO_ERROR_SUFFIX = " [I/O ERROR]\n";

        for (Path configPath : configPaths) {
            configFileTotal += 1;
            try {
                JSONObject configjson = new JSONObject(Files.readString(configPath));
                configjson.getString("id");
                configjson.optString("name");

            } catch (JSONException e) {
                flaggedFileTotal += 1;
                sb.append(configPath.toAbsolutePath());

                try {
                    if (repairjson(Files.readString(configPath)) != null) {
                        sb.append(JSON_WARNING_SUFFIX);
                    } else {
                        sb.append(JSON_ERROR_SUFFIX);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (IOException e) {
                flaggedFileTotal += 1;
                sb.append(configPath.toAbsolutePath()).append(IO_ERROR_SUFFIX);
            }
        }
        final String badFiles = sb.toString().trim();
        if (badFiles.isBlank()) {
            System.out.println("All files are valid! Validated " + configFileTotal + " files.");
        } else {
            System.out.println(badFiles + "\nFlagged " + flaggedFileTotal + " out of " + configFileTotal + " files.");
        }

    }

    private static void getHelp() {
        CLS.clearConsoleScreen();
        int helpPage = 0;
        helpPage = UserPrompt.promptForint("5 - Validate Config Files");
        CLS.clearConsoleScreen();

        switch (helpPage) {

        case 1:

            break;
        case 2:

            break;
        case 3:

            break;
        case 5:
            try {
                System.out.println(Files.readString(Paths.get("./doc/ValidateHelp.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserPrompt.promptForString("Press enter to continue...");

            break;
        default:
            break;
        }
    }

    /**
     * @return the panelAttackDir
     */
    public static Path getPanelAttackDir() {
        return panelAttackDir;
    }
}
