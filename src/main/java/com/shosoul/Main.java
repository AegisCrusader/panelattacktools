package com.shosoul;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.json.JSONObject;

/**
 * Hello world!
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {

        final String panelAttackDir = getAppDataDirectory() + "\\Panel Attack\\";
        final String characterDir = panelAttackDir + "characters\\";
        final String stageDir = panelAttackDir + "stages\\";

        Path characterDirPath = Paths.get(new File(characterDir).toURI());
        Path stageDirPath = Paths.get(new File(stageDir).toURI());

        while (true) {            CLS.clearConsoleScreen();

            int modSelection = UserPrompt.promptForint("0 - Exit\n1 - Characters\n2 - Stages\n\nEnter option:");
            CLS.clearConsoleScreen();
            switch (modSelection) {
            case 1:
                doCharacterMods(characterDirPath);
                break;
            case 2:
                doStageMods(stageDirPath);
                break;
            default:
                break;
            }
            if (modSelection == 0) {
                break;
            }
        }
        System.exit(0);

    }

    public static void doCharacterMods(Path characterDirPath) {
        ArrayList<Path> characterConfigPaths = new ArrayList<>();
        ArrayList<Path> modConfigPaths = new ArrayList<>();

        // Find all the config.json files to determine mod folders
        try (Stream<Path> pathStream = Files.walk(characterDirPath)) {
            pathStream.parallel().filter(Files::isRegularFile)
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json"))
                    .forEach(characterConfigPaths::add);
        } catch (Exception e) {
        }

        try {
            for (Path path : characterConfigPaths) {
                StringBuilder sb = new StringBuilder();
                sb.append(Files.readString(path));
                JSONObject configjson = new JSONObject(sb.toString());
                String id = configjson.getString("id");
                String name = configjson.getString("name");
                boolean isMod = true;
                if (id.startsWith("pa_characters")) {
                    isMod = false;
                }

                String directoryName = path.getParent().getFileName().toString();
                if (isMod) {
                    modConfigPaths.add(path);
                    /*
                     * if (directoryName.startsWith("__")) { System.out.println(name + " [OFF]"); }
                     * else { System.out.println(name + " [ON]"); }
                     */
                }
            }

            for (int i = 0; i < modConfigPaths.size(); i++) {
                Path modpath = modConfigPaths.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(Files.readString(modpath));
                JSONObject configjson = new JSONObject(sb.toString());
                String id = configjson.getString("id");
                String name = configjson.getString("name");
                boolean isMod = true;

                String directoryName = modpath.getParent().getFileName().toString();
                // System.out.println(directoryName);

                if (directoryName.startsWith("__")) {
                    System.out.println(i + " - " + name + " [OFF]");
                } else {
                    System.out.println(i + " - " + name + " [ON]");
                }

            }
            System.out.println("On - Enable all");
            System.out.println("Off - Disable all");
            System.out.println("");//create newline
            String modSelection = UserPrompt.promptForString("Type the number of the mod to toggle, \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");

            if (!modSelection.equalsIgnoreCase("back")) {
                // disable all mods
                if (modSelection.equalsIgnoreCase("off")) {
                    for (Path path : modConfigPaths) {
                        Path parentFolderPath = path.getParent();
                        String dirname = parentFolderPath.getFileName().toString();
                        if (!dirname.startsWith("__")) {
                            Files.move(parentFolderPath, parentFolderPath.resolveSibling("__" + dirname));
                        }
                    }
                } else if (modSelection.equalsIgnoreCase("on")) {
                    for (Path path : modConfigPaths) {
                        Path parentFolderPath = path.getParent();
                        String dirname = parentFolderPath.getFileName().toString();
                        if (dirname.startsWith("__")) {
                            Files.move(parentFolderPath, parentFolderPath.resolveSibling(dirname.substring(2)));
                        }
                    }
                } else {
                    Path selection = modConfigPaths.get(Integer.parseInt(modSelection)).getParent();
                    String dirname = selection.getFileName().toString();
                    if (dirname.startsWith("__")) {
                        Files.move(selection, selection.resolveSibling(dirname.substring(2)));
                    } else {
                        Files.move(selection, selection.resolveSibling("__" + dirname));
                    }
                }
            }
            CLS.clearConsoleScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * for (int i = 0; i < modPaths.size() - 1; i++) { Path modpath =
         * modPaths.get(i); StringBuilder sb = new StringBuilder();
         * sb.append(Files.readString(modpath)); JSONObject configjson = new
         * JSONObject(sb.toString()); String id = configjson.getString("id"); String
         * name = configjson.getString("name"); boolean isMod = true;
         * 
         * String directoryName = modpath.getParent().getFileName().toString(); //
         * System.out.println(directoryName);
         * 
         * if (directoryName.startsWith("__")) { System.out.println(name + " [OFF]"); }
         * else { System.out.println(name + " [ON]"); }
         * 
         * }
         */

    }

    public static void doStageMods(Path stageDirPath) {
        ArrayList<Path> stageConfigPaths = new ArrayList<>();
        ArrayList<Path> modConfigPaths = new ArrayList<>();

        // Find all the config.json files to determine mod folders
        try (Stream<Path> pathStream = Files.walk(stageDirPath)) {
            pathStream.parallel().filter(Files::isRegularFile)
                    .filter(f -> f.toFile().getAbsolutePath().endsWith("config.json")).forEach(stageConfigPaths::add);
        } catch (Exception e) {
        }
        try {
            for (Path path : stageConfigPaths) {
                StringBuilder sb = new StringBuilder();
                sb.append(Files.readString(path));
                JSONObject configjson = new JSONObject(sb.toString());
                String id = configjson.getString("id");
                String name = configjson.getString("name");
                boolean isMod = true;
                if (id.startsWith("pa_stages")) {
                    isMod = false;
                }

                String directoryName = path.getParent().getFileName().toString();
                if (isMod) {
                    modConfigPaths.add(path);
                    /*
                     * if (directoryName.startsWith("__")) { System.out.println(name + " [OFF]"); }
                     * else { System.out.println(name + " [ON]"); }
                     */
                }
            }
            for (int i = 0; i < modConfigPaths.size(); i++) {
                Path modpath = modConfigPaths.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(Files.readString(modpath));
                JSONObject configjson = new JSONObject(sb.toString());
                String id = configjson.getString("id");
                String name = configjson.getString("name");
                boolean isMod = true;

                String directoryName = modpath.getParent().getFileName().toString();
                // System.out.println(directoryName);

                if (directoryName.startsWith("__")) {
                    System.out.println(i + " - " + name + " [OFF]");
                } else {
                    System.out.println(i + " - " + name + " [ON]");
                }

            }
            System.out.println("On - Enable all");
            System.out.println("Off - Disable all");
            System.out.println("");//create newline
            String modSelection = UserPrompt.promptForString("Type the number of the mod to toggle, \"on\" to enable all, \"off\" to disable all, or \"back\" to return to the menu.");

            if (!modSelection.equalsIgnoreCase("back")) {
                // disable all mods
                if (modSelection.equalsIgnoreCase("off")) {
                    for (Path path : modConfigPaths) {
                        Path parentFolderPath = path.getParent();
                        String dirname = parentFolderPath.getFileName().toString();
                        if (!dirname.startsWith("__")) {
                            Files.move(parentFolderPath, parentFolderPath.resolveSibling("__" + dirname));
                        }
                    }
                } else if (modSelection.equalsIgnoreCase("on")) {
                    for (Path path : modConfigPaths) {
                        Path parentFolderPath = path.getParent();
                        String dirname = parentFolderPath.getFileName().toString();
                        if (dirname.startsWith("__")) {
                            Files.move(parentFolderPath, parentFolderPath.resolveSibling(dirname.substring(2)));
                        }
                    }
                } else {
                    Path selection = modConfigPaths.get(Integer.parseInt(modSelection)).getParent();
                    String dirname = selection.getFileName().toString();
                    if (dirname.startsWith("__")) {
                        Files.move(selection, selection.resolveSibling(dirname.substring(2)));
                    } else {
                        Files.move(selection, selection.resolveSibling("__" + dirname));
                    }
                }
            }
            CLS.clearConsoleScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * for (int i = 0; i < modPaths.size() - 1; i++) { Path modpath =
         * modPaths.get(i); StringBuilder sb = new StringBuilder();
         * sb.append(Files.readString(modpath)); JSONObject configjson = new
         * JSONObject(sb.toString()); String id = configjson.getString("id"); String
         * name = configjson.getString("name"); boolean isMod = true;
         * 
         * String directoryName = modpath.getParent().getFileName().toString(); //
         * System.out.println(directoryName);
         * 
         * if (directoryName.startsWith("__")) { System.out.println(name + " [OFF]"); }
         * else { System.out.println(name + " [ON]"); }
         * 
         * }
         */

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

}
