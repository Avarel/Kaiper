package com.kantenkugel.consoleutils;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class AutoCompleter implements Consumer<ConsoleInputEvent> {
    private final Consumer<String> callback;
    private final Function<String, String[]> optionProvider;

    private Runnable loopStopper;

    public AutoCompleter(Consumer<String> callback, Function<String, String[]> optionProvider) {
        this.callback = callback;
        this.optionProvider = optionProvider;
    }

    public void run() throws IOException {
        loopStopper = ConsoleReader.startLoop(this);
    }

    public void stop() {
        if(loopStopper != null)
            loopStopper.run();
    }

    private String currentAuto = null;
    private int matchLength = 0;

    @Override
    public void accept(ConsoleInputEvent e) {
        char addedChar = e.getAddedChar();
        if(addedChar == CharConstants.CHAR_CTRL_C || addedChar == CharConstants.CHAR_CTRL_D
                || addedChar == CharConstants.CHAR_CTRL_Z) {
            e.cancelLoop();
            callback.accept(null);
            return;
        }
        if(addedChar == '\n') {
            if(currentAuto != null) {
                clear(currentAuto.length() - matchLength);
            }
            callback.accept(e.getCurrentBuffer().toString());
            e.clearBuffer();
        }
        if(addedChar != CharConstants.CHAR_TAB) {
            if(addedChar == CharConstants.CHAR_BACKSPACE)
                System.out.print(CharConstants.CHAR_BACKSPACE + " ");
            System.out.print(addedChar);
        }
        if(addedChar == CharConstants.CHAR_BACKSPACE && currentAuto != null) {
            matchLength--;
            clear(currentAuto.length() - matchLength);
        }
        if(addedChar == CharConstants.CHAR_TAB && currentAuto != null) {
            String substring = currentAuto.substring(matchLength);
            System.out.print(substring);
            e.getCurrentBuffer().replace(e.getCurrentBuffer().length() - 1, e.getCurrentBuffer().length(), substring);
        }
        int index = e.getCurrentBuffer().lastIndexOf(" ");
        String lastWord = e.getCurrentBuffer().substring(index + 1);
        String nextAuto = null;
        if(lastWord.length() > 0) {
            for(String s : optionProvider.apply(e.getCurrentBuffer().substring(0, Math.max(0, index)))) {
                if(s.startsWith(lastWord) && s.length() != lastWord.length()) {
                    nextAuto = s;
                    matchLength = lastWord.length();
                    if(nextAuto.equals(currentAuto))
                        break;
                    System.out.print(s.substring(matchLength));
                    for(int i = 0; i < s.length() - matchLength; i++) {
                        System.out.print(CharConstants.CHAR_BACKSPACE);
                    }
                    break;
                }
            }
        }
        if(nextAuto == null && currentAuto != null) {
            clear(currentAuto.length() - matchLength);
        }
        currentAuto = nextAuto;
    }

    private void clear(int amount) {
        for(int i = 0; i < amount; i++) {
            System.out.print(" ");
        }
        for(int i = 0; i < amount; i++) {
            System.out.print(CharConstants.CHAR_BACKSPACE);
        }
    }
}
