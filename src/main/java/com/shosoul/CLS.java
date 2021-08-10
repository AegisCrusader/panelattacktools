package com.shosoul;

import java.io.IOException;

public final class CLS {

    /**
     * Clears the console screen by using the <code>CLS</code> command.
     */
    public static final void clearConsoleScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            System.out.println("Unable to clear the console screen.");
        }

    }

}
