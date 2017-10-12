package com.kantenkugel.consoleutils;

import biz.source_code.utils.RawConsoleInput;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class ConsoleUtils {
    public static String readHidden(String placeholder) throws IOException {
        StringBuilder b = new StringBuilder();
        int read;
        while ((read = RawConsoleInput.read(true)) != -1) {
            if (read == '\r' || read == '\n') return b.toString();
            if(!isPrintableChar((char) read)) {
                if(read == CharConstants.CHAR_BACKSPACE) {
                    if(b.length() == 0) continue;
                    b.setLength(b.length() - 1);
                    for(int i = 0; i < placeholder.length(); i++) {
                        backspace();
                    }
                    continue;
                } else {
                    return b.toString();
                }
            }
            b.append((char) read);
            System.out.print(placeholder);
        }
        return b.toString();
    }

    public static String readWithInitialBuffer(String init) throws IOException {
        System.out.print(init);
        StringBuilder b = new StringBuilder(init);
        int read;
        while ((read = RawConsoleInput.read(true)) != -1) {
            if (read == '\r' || read == '\n') return b.toString();
            if (!isPrintableChar((char) read)) {
                if(read == CharConstants.CHAR_BACKSPACE) {
                    if(b.length() == 0) continue;
                    b.setLength(b.length() - 1);
                    ConsoleUtils.backspace();
                    continue;
                } else {
                    return b.toString();
                }
            }
            b.append((char) read);
            System.out.print((char) read);
        }
        return b.toString();
    }

    public static void backspace() {
        System.out.print(CharConstants.CHAR_BACKSPACE);
        System.out.print(' ');
        System.out.print(CharConstants.CHAR_BACKSPACE);
    }

    public static boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    private ConsoleUtils() {

    }
}
