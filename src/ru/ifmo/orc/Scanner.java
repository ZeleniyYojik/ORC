package ru.ifmo.orc;

import java.io.*;
import java.util.ArrayList;


class Scanner {
    static ArrayList<String> ids = new ArrayList<>();
    static ArrayList<String> consts = new ArrayList<>();
    static ArrayList<String> delims = new ArrayList<>();
    static ArrayList<String> brackets = new ArrayList<>();
    static ArrayList<String> keyWords = new ArrayList<>();
    static ArrayList<String> logical = new ArrayList<>();
    static ArrayList<String> unary = new ArrayList<>();
    static ArrayList<String> additive = new ArrayList<>();
    static ArrayList<String> multiplicative = new ArrayList<>();
    static ArrayList<String> assignment = new ArrayList<>();

    static void scan(String sourcePath, String outputPath) throws Exception {
        delims.add(",");
        delims.add(";");
        brackets.add("(");
        brackets.add(")");
        keyWords.add("begin");
        keyWords.add("while");
        keyWords.add("do");
        keyWords.add("end");
        keyWords.add("end.");
        keyWords.add("var");
        logical.add("and");
        logical.add("or");
        logical.add("xor");
        logical.add("<");
        logical.add(">");
        logical.add("=");
        unary.add("-");
        additive.add("-");
        additive.add("+");
        multiplicative.add("/");
        multiplicative.add("*");
        assignment.add(":=");
        File source = new File(sourcePath);
        Logger logger = null;
        logger = new Logger(outputPath);
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
                            String word = readWord(pbr).toLowerCase();
                            switch (word) {
                                case "begin":
                                case "do":
                                case "var":
                                case "while": {
                                    logger.log(checkLex(keyWords, word) + "", LexType.KeyWord, lineNumb);
                                }
                                break;
                                case "end": {
                                    int dot = pbr.read();
                                    if (dot == '.') {
                                        logger.log(checkLex(keyWords, "end.") + "", LexType.KeyWord, lineNumb);
                                    } else {
                                        if (dot != -1) {
                                            pbr.unread(dot);
                                        }
                                        logger.log(checkLex(keyWords, "end") + "", LexType.KeyWord, lineNumb);
                                    }
                                }
                                break;
                                case "and":
                                case "or":
                                case "xor": {
                                    logger.log(checkLex(logical, word) + "", LexType.Logical, lineNumb);
                                }
                                break;
                            /*Word is Id*/
                                default: {
                                    int id = checkId(word);
                                    logger.log(id + "", LexType.Id, lineNumb);
                                }
                                break;
                            }
                            continue;
                        }
                    /*const*/
                        if (Character.isDigit(rc)) {
                            pbr.unread(rc);
                            String constWord = readConst(pbr);
                            constWord = Integer.toHexString(Integer.parseInt(constWord));
                            int id = checkConst(constWord);
                            logger.log(id + "", LexType.Const, lineNumb);
                            continue;
                        }
                        if (rc == '-') {
                            int digit = pbr.read();
                            if (Character.isDigit(digit)) {
                                pbr.unread(digit);
                                String constWord = readConst(pbr);
                                constWord = Integer.toHexString(Integer.parseInt(constWord));
                                int id = checkConst(constWord);
                                logger.log(checkLex(unary, (char) rc + "") + "", LexType.Unary, lineNumb);
                                logger.log(id + "", LexType.Const, lineNumb);
                                continue;
                            } else {
                                if (digit != -1) {
                                    pbr.unread(digit);
                                }
                                logger.log(checkLex(additive, (char) rc + "") + "", LexType.Additive, lineNumb);
                                continue;
                            }
                        }
                        if (rc == '+') {
                            logger.log(checkLex(additive, (char) rc + "") + "", LexType.Additive, lineNumb);
                            continue;
                        }
                        if (rc == '*') {
                            logger.log(checkLex(multiplicative, (char) rc + "") + "", LexType.Multiplicative, lineNumb);
                            continue;
                        }
                    /*Проверка на комментарий*/
                        if (rc == '/') {
                            int mul = pbr.read();
                            if (mul == '*') {
                                comment = true;
                                continue;
                            } else if (mul != -1) {
                                pbr.unread(mul);
                                logger.log(checkLex(multiplicative, (char) rc + "") + "", LexType.Multiplicative, lineNumb);
                                continue;
                            }
                        }
                        if (rc == '>' || rc == '<' || rc == '=') {
                            logger.log(checkLex(logical, (char) rc + "") + "", LexType.Logical, lineNumb);
                            continue;
                        }

                        if (rc == ':') {
                            int eq = pbr.read();
                            if (eq == '=') {
                                logger.log(checkLex(assignment, ":=") + "", LexType.Assignment, lineNumb);
                                continue;
                            } else {
                                logger.log("" + (char) rc, LexType.Error, lineNumb);
                                if (eq != -1) {
                                    pbr.unread(eq);
                                }
                                continue;
                            }
                        }
                        if (rc == ',' || rc == ';') {
                            logger.log(checkLex(delims, "" + (char) rc) + "", LexType.Delimiter, lineNumb);
                            continue;
                        }

                        if (rc == '(' || rc == ')') {
                            logger.log(checkLex(brackets, "" + (char) rc) + "", LexType.Bracket, lineNumb);
                            continue;
                        }
                        if (rc == Character.MAX_VALUE) {
                            break;
                        }
                        if (rc != ' ') {
                            logger.log((char) rc + "", LexType.Error, lineNumb);
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
            logger.endLogging();
        } catch (IOException e)

        {
            e.printStackTrace();
        }

    }

    private static int checkId(String word) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals(word)) {
                return i;
            }
        }
        ids.add(word);
        return ids.size() - 1;
    }

    private static int checkConst(String word) {
        for (int i = 0; i < consts.size(); i++) {
            if (consts.get(i).equals(word)) {
                return i;
            }
        }
        consts.add(word);
        return consts.size() - 1;
    }

    private static int checkLex(ArrayList<String> arl, String word) {
        for (int i = 0; i < arl.size(); i++) {
            if (arl.get(i).equals(word)) {
                return i;
            }
        }
        return -1;
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