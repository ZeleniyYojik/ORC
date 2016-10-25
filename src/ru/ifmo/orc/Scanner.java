package ru.ifmo.orc;

import jdk.nashorn.internal.ir.IfNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Scanner {
    private static List<Character> delimeters = new ArrayList<>();


    static void scan(String source_path, String output_path) throws FileNotFoundException {
        delimeters.add(',');
        delimeters.add(';');
        delimeters.add('\n');
        File source = new File(source_path);
        int lineNumb = 0;
        try (BufferedReader brl = new BufferedReader(new FileReader(source))) {
            while (true) {
                /*Считываем строку файла*/
                String line = brl.readLine();
                if (line == null) {
                    break;
                }
                lineNumb++;
                PushbackReader pbr = new PushbackReader(new StringReader(line));
                while (true) {
                    int rc = pbr.read();
                    if (rc == -1) {
                        break;
                    }
                    /*Word*/
                    if (Character.isLetter(rc)) {
                        pbr.unread(rc);
                        String word = readWord(pbr);
                        rc = pbr.read();
                        if (rc != -1 && !delimeters.contains((char) rc)) {
                            pbr.unread(rc);
                            String err = readErr(pbr);
                            Logger.log(word + err, LexType.Error, lineNumb);
                            continue;
                        } else {
                            pbr.unread(rc);
                        }
                        switch (word) {
                            case "Begin":
                            case "Do":
                            case "Var":
                            case "While": {
                                Logger.log(word, LexType.KeyWord, lineNumb);
                            }
                            break;
                            case "End": {
                                int dot = pbr.read();
                                if (dot == '.') {
                                    Logger.log("End.", LexType.KeyWord, lineNumb);
                                } else {
                                    pbr.unread(dot);
                                }
                                Logger.log("End", LexType.KeyWord, lineNumb);
                            }
                            break;
                            case "AND":
                            case "OR":
                            case "XOR": {
                                Logger.log(word, LexType.Logical, lineNumb);
                            }
                            break;
                            /*Word is Id*/
                            case "!0": {

                            }
                            break;
                            default: {
                                Logger.log(word, LexType.Id, lineNumb);
                            }
                            break;
                        }
                    }
                    /*const*/
                    if (Character.isDigit(rc)) {
                        pbr.unread(rc);
                        String constt = readConst(pbr);
                        Logger.log(constt, LexType.Const, lineNumb);
                    }
                    if (rc == '-') {
                        int digit = pbr.read();
                        if (Character.isDigit(digit)) {
                            pbr.unread(rc);
                            String constt = readConst(pbr);
                            Logger.log(String.valueOf((char) rc), LexType.Unary, lineNumb);
                            Logger.log(constt, LexType.Const, lineNumb);
                        } else {
                            Logger.log(String.valueOf((char) rc), LexType.Additive, lineNumb);
                        }
                    }
                    if (rc == '+') {
                        Logger.log("" + (char) rc, LexType.Additive, lineNumb);
                    }
                    if (rc == '*' || rc == '/') {
                        Logger.log("" + (char) rc, LexType.Multyplicative, lineNumb);
                    }
                    if (rc == '>' || rc == '<' || rc == '=') {
                        Logger.log("" + (char) rc, LexType.Logical, lineNumb);
                    }
                    if (rc == ':') {
                        int eq = pbr.read();
                        if (eq == '=') {
                            Logger.log(":=", LexType.Assignment, lineNumb);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readWord(PushbackReader pbr) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int rc = pbr.read();
            if (!Character.isLetter(rc)) {
                pbr.unread(rc);
                return sb.toString();
            }
            sb.append((char) rc);
        }
    }

    private static String readConst(PushbackReader pbr) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int rc = pbr.read();
            if (!Character.isDigit(rc)) {
                pbr.unread(rc);
                return sb.toString();
            }
            sb.append((char) rc);
        }
    }

    private static String readErr(PushbackReader pbr) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int rc = pbr.read();
            if (rc == ' ') {
                pbr.unread(rc);
                return sb.toString();
            } else if (rc == -1) {
                return sb.toString();
            }
            sb.append((char) rc);
        }
    }
}