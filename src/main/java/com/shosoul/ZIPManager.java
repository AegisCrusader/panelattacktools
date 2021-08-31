package com.shosoul;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ZIPManager {
    public static void main(String[] args) throws Exception {

    }

    public static boolean installMods(JFrame frame) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("Unable to set look and feel.");
        }
        boolean foundLegalFiles = false;
        boolean foundBadFiles = false;
        try {
            File[] files = showOpenDialog();
            if (files == null) {
                try {
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                return false;
            }
            for (File selectedFile : files) {

                String fileZip = selectedFile.toPath().toAbsolutePath().toString();
                File destDir = new File(
                        Paths.get(Main.getAppDataDirectory(), "Panel Attack").toAbsolutePath().toString());
                byte[] buffer = new byte[1024];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {
                    File newFile = newFile(destDir, zipEntry);
                    // System.out.println(newFile.getAbsolutePath());
                    if (zipEntry.getName().startsWith("characters") || zipEntry.getName().startsWith("stages")
                            || zipEntry.getName().startsWith("themes") || zipEntry.getName().startsWith("panels")) {
                        if (zipEntry.isDirectory()) {
                            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                                throw new IOException("Failed to create directory " + newFile);
                            }
                        } else {
                            // fix for Windows-created archives
                            File parent = newFile.getParentFile();
                            if (!parent.isDirectory() && !parent.mkdirs()) {
                                throw new IOException("Failed to create directory " + parent);
                            }

                            // write file content
                            FileOutputStream fos = new FileOutputStream(newFile);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }
                        foundLegalFiles = true;
                    } else {
                        foundBadFiles = true;
                        System.out.println(zipEntry.getName() + ": This is illegal!");
                    }

                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
            }
            if (foundLegalFiles && foundBadFiles) {
                JOptionPane.showMessageDialog(frame,
                        "Partially installed files. Root directories must start with \"characters\", \"stages\", \"themes\", or \"panels\".",
                        "Mod Partially Installed", JOptionPane.WARNING_MESSAGE);
            } else if (foundBadFiles) {
                JOptionPane.showMessageDialog(frame,
                        "Unable to install mod(s). Root directories must start with \"characters\", \"stages\", \"themes\", or \"panels\".",
                        "Install Failed", JOptionPane.ERROR_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(frame, "Mod(s) Successfully installed!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static File[] showOpenDialog() {

        // Create a filter so that we only see .zip files
        FileFilter filter = new FileNameExtensionFilter(null, "zip");

        // Create and show the file filter
        JFileChooser fc = new JFileChooser(
                Paths.get(System.getProperty("user.home"), "Downloads").toAbsolutePath().toString());
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(true);

        Action details = fc.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        int response = fc.showOpenDialog(null);

        // Check the user pressed OK, and not Cancel.
        if (response == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFiles();
            // Do whatever you want with the file
            // ...
        } else
            return null;
    }
}
