package ru.ifmo.orc;

import java.io.*;
import java.util.ArrayList;


class Scanner {
    private static ArrayList<String> ids = new ArrayList<>();
    private static ArrayList<String> consts = new ArrayList<>();

    static void scan(String sourcePath, String outputPath) throws Exception {
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
                            String word = readWord(pbr);
                            switch (word) {
                                case "Begin":
                                case "Do":
                                case "Var":
                                case "While": {
                                    logger.log(word, LexType.KeyWord, lineNumb);
                                }
                                break;
                                case "End": {
                                    int dot = pbr.read();
                                    if (dot == '.') {
                                        logger.log("End.", LexType.KeyWord, lineNumb);
                                    } else {
                                        if (dot != -1) {
                                            pbr.unread(dot);
                                        }
                                        logger.log("End", LexType.KeyWord, lineNumb);
                                    }
                                }
                                break;
                                case "AND":
                                case "OR":
                                case "XOR": {
                                    logger.log(word, LexType.Logical, lineNumb);
                                }
                                break;
                            /*Word is Id*/
                                default: {
                                    int id = checkId(word);
                                    logger.log("" + id, LexType.Id, lineNumb);
                                }
                                break;
                            }
                        }
                    /*const*/
                        if (Character.isDigit(rc)) {
                            pbr.unread(rc);
                            String constWord = readConst(pbr);
                            constWord = Integer.toHexString(Integer.parseInt(constWord));
                            int id = checkConst(constWord);
                            logger.log("" + id, LexType.Const, lineNumb);
                        }
                        if (rc == '-') {
                            int digit = pbr.read();
                            if (Character.isDigit(digit)) {
                                pbr.unread(digit);
                                String constt = readConst(pbr);
                                logger.log(String.valueOf((char) rc), LexType.Unary, lineNumb);
                                logger.log(constt, LexType.Const, lineNumb);
                            } else {
                                if (digit != -1) {
                                    pbr.unread(digit);
                                }
                                logger.log(String.valueOf((char) rc), LexType.Additive, lineNumb);
                            }
                        }
                        if (rc == '+') {
                            logger.log("" + (char) rc, LexType.Additive, lineNumb);
                        }
                        if (rc == '*') {
                            logger.log("" + (char) rc, LexType.Multyplicative, lineNumb);
                        }
                    /*Проверка на комментарий*/
                        if (rc == '/') {
                            int mul = pbr.read();
                            if (mul == '*') {
                                comment = true;
                                continue;
                            } else if (mul != -1) {
                                pbr.unread(mul);
                                logger.log("" + (char) rc, LexType.Multyplicative, lineNumb);
                            }
                        }
                        if (rc == '>' || rc == '<' || rc == '=') {
                            logger.log("" + (char) rc, LexType.Logical, lineNumb);
                        }
                        if (rc == ':') {
                            int eq = pbr.read();
                            if (eq == '=') {
                                logger.log(":=", LexType.Assignment, lineNumb);
                            } else {
                                logger.log("" + (char) rc, LexType.Error, lineNumb);
                                if (eq != -1) {
                                    pbr.unread(eq);
                                }
                            }
                        }
                        if (rc == ',' || rc == ';') {
                            logger.log("" + (char) rc, LexType.Delimiter, lineNumb);
                        }
                        if (rc == '(' || rc == ')') {
                            logger.log("" + (char) rc, LexType.Bracket, lineNumb);
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
            logger.endLogging(ids, consts);
        } catch (
                IOException e)

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