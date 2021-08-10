package com.shosoul;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class UserPrompt {
    private static String input = null;
    private static Scanner scanner = new Scanner(System.in);
    private static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    private static Console console = System.console();

    public static String promptForString(String prompt) {
        out.println(prompt);
        try {
            input = keyboard.readLine();
        } catch (IOException e) {

        }
        return input;
    }

    /**
     * 
     * @param prompt
     * @return
     */// XXX: Prompt totally messes up everyting
    public static byte[] promptForPassword(String prompt) {
        out.print(prompt);
        char[] _password = console.readPassword();
        byte[] _input = _password.toString().getBytes();
        return _input;
    }

    public static int promptForint(String prompt) {
        out.println(prompt);
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    public void finalize() throws IOException {
        keyboard.close();
    }

}
