package com.kantenkugel.consoleutils;

import biz.source_code.utils.RawConsoleInput;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ConsoleReader {
    public static Runnable startLoop(Consumer<ConsoleInputEvent> consoleHandler) throws IOException {
        AtomicBoolean running = new AtomicBoolean(true);
        new Thread(() -> {
            try {
                loop(consoleHandler, running);
            } catch(IOException e) {
                running.set(false);
                e.printStackTrace();
            }
        }).start();
        return () -> running.set(false);
    }

    static void loop(Consumer<ConsoleInputEvent> consoleHandler, AtomicBoolean shouldRun) throws IOException {
        int read;
        final StringBuilder b = new StringBuilder();
        while(shouldRun.get()) {
            read = RawConsoleInput.read(true);
            if(read == -1)
                read = CharConstants.CHAR_CTRL_D;
            if(read == '\r')
                read = '\n';
            if(read == CharConstants.CHAR_BACKSPACE)
                b.setLength(b.length()-1);
            else
                b.append((char) read);
            ConsoleInputEvent event = new ConsoleInputEvent(b, (char) read);
            consoleHandler.accept(event);
            if(event.isShouldCancel())
                shouldRun.set(false);
        }
        resetConsoleMode();
    }

    public static void resetConsoleMode() throws IOException {
        RawConsoleInput.resetConsoleMode();
    }

    private ConsoleReader() {
    }
}
