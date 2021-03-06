package ru.ifmo.orc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class Logger {
    private String outPath;
    private FileWriter fw;
    private StringBuilder sb = new StringBuilder();

    Logger(String outPath) {
        this.outPath = outPath;
    }

    void log(Lexeme lexeme) throws IOException {
        if (lexeme.type == LexType.Error.val) {
            System.out.println("Error at line " + lexeme.lineNum + " - \"" + lexeme.word + "\"");
        }
        sb.append(lexeme+"\n");
//        System.out.println(lexeme);
    }

    void endLogging() throws IOException {
        this.fw = new FileWriter(this.outPath);
        fw.append("=IDs=\n");
        logTable(Scanner.ids);
        fw.append("=Consts=\n");
        logTable(Scanner.consts);
        fw.append("=Delims=\n");
        logTable(Scanner.delims);
        fw.append("=OpenBrackets=\n");
        logTable(Scanner.openBrackets);
        fw.append("=CloseBrackets=\n");
        logTable(Scanner.closeBrackets);
        fw.append("=KeyWords=\n");
        logTable(Scanner.keyWords);
        fw.append("=Logical=\n");
        logTable(Scanner.logical);
        fw.append("=Unary=\n");
        logTable(Scanner.unary);
        fw.append("=Additive=\n");
        logTable(Scanner.additive);
        fw.append("=Multiplicative=\n");
        logTable(Scanner.multiplicative);
        fw.append("=Assignment=\n");
        logTable(Scanner.assignment);
        fw.append("=Lexemes=\n");
        fw.append(sb.toString());
        fw.close();
    }

    private void logTable(List<String> arr) throws IOException {
        for (int i = 0; i < arr.size(); i++) {
            this.fw.append("<" + arr.get(i) + ";" + i + ">\n");
        }
    }
}
