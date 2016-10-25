package ru.ifmo.orc;

import java.io.IOException;

class Logger {
    static void log(String lex, LexType lexType, int lineNumber) throws IOException {
        System.out.println("<"+lex+";"+lexType.toString()+";"+lineNumber+">");
    }
}
