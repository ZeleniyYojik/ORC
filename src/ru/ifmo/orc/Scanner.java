package ru.ifmo.orc;

import java.io.*;


class Scanner {

    static void scan(String source_path, String output_path) throws FileNotFoundException {
        File source = new File(source_path);
        int lineNumb = 0;
        boolean comment = false;
        try (BufferedReader brl = new BufferedReader(new FileReader(source))) {
            while (true) {
                /*Считываем строку файла*/
                String line = brl.readLine();
                if (line == null) {
                    break;
                }
                lineNumb++;
                /*Идем по строке*/
                PushbackReader pbr = new PushbackReader(new StringReader(line));
                while (true) {
                    int rc = pbr.read();
                    if (rc == -1) {
                        break;
                    }
                    if (!comment) {
                    /*Word*/
                        if (Character.isLetter(rc)) {
                            pbr.unread(rc);
                            String word = readWord(pbr);
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
                                        if (dot != -1) {
                                            pbr.unread(dot);
                                        }
                                        Logger.log("End", LexType.KeyWord, lineNumb);
                                    }
                                }
                                break;
                                case "AND":
                                case "OR":
                                case "XOR": {
                                    Logger.log(word, LexType.Logical, lineNumb);
                                }
                                break;
                            /*Word is Id*/
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
                                pbr.unread(digit);
                                String constt = readConst(pbr);
                                Logger.log(String.valueOf((char) rc), LexType.Unary, lineNumb);
                                Logger.log(constt, LexType.Const, lineNumb);
                            } else {
                                if (digit != -1) {
                                    pbr.unread(digit);
                                }
                                Logger.log(String.valueOf((char) rc), LexType.Additive, lineNumb);
                            }
                        }
                        if (rc == '+') {
                            Logger.log("" + (char) rc, LexType.Additive, lineNumb);
                        }
                        if (rc == '*') {
                            Logger.log("" + (char) rc, LexType.Multyplicative, lineNumb);
                        }
                    /*Проверка на комментарий*/
                        if (rc == '/') {
                            int mul = pbr.read();
                            if (mul == '*') {
                                comment = true;
                                continue;
                            } else if (mul != -1) {
                                pbr.unread(mul);
                                Logger.log("" + (char) rc, LexType.Multyplicative, lineNumb);
                            }
                        }
                        if (rc == '>' || rc == '<' || rc == '=') {
                            Logger.log("" + (char) rc, LexType.Logical, lineNumb);
                        }
                        if (rc == ':') {
                            int eq = pbr.read();
                            if (eq == '=') {
                                Logger.log(":=", LexType.Assignment, lineNumb);
                            } else {
                                Logger.log("" + (char) rc, LexType.Error, lineNumb);
                                if (eq != -1) {
                                    pbr.unread(eq);
                                }
                            }
                        }
                        if (rc == ',' || rc == ';') {
                            Logger.log("" + (char) rc, LexType.Delimiter, lineNumb);
                        }
                        if (rc == '(' || rc == ')') {
                            Logger.log("" + (char) rc, LexType.Bracket, lineNumb);
                        }
                    } else {
                        pbr.unread(rc);
                        while (true) {
                            int mul = pbr.read();
                            if (mul == '*') {
                                rc = pbr.read();
                                if (rc == '/') {
                                    comment = false;
                                    break;
                                } else if (rc == -1) {
                                    break;
                                }
                            } else if (mul == -1) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (
                IOException e)

        {
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
}