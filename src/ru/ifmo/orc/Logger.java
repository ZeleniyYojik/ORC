package ru.ifmo.orc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class Logger {
    private String outPath;
    private FileWriter fw;
    private StringBuilder sb = new StringBuilder();

    Logger(String outPath) {
        this.outPath = outPath;
    }

    void log(String lex, LexType lexType, int lineNumber) throws IOException {
        sb.append("<" + lex + ";" + lexType.toString() + ";" + lineNumber + ">" + "\n");
        if (lexType == LexType.Error) {
            System.out.println("Error at line " + lineNumber + " - \"" + lex + "\"");
        }
        System.out.println("<" + lex + ";" + lexType.val + ";" + lineNumber + ">");
    }

    void endLogging(ArrayList<String> ids, ArrayList<String> consts) throws IOException {
        this.fw = new FileWriter(this.outPath);
        fw.append("IDs\n");
        for (int i = 0; i < ids.size(); i++) {
            fw.append("<" + ids.get(i) + ";" + i+">\n");
        }
        fw.append("Consts\n");
        for (int i = 0; i < consts.size(); i++) {
            fw.append("<" + consts.get(i) + ";" + i+">\n");
        }
        fw.append("Lexemas\n");
        fw.append(sb.toString());
        fw.close();
    }
}
