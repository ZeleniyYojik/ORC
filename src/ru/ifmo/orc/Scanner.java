package ru.ifmo.orc;

import java.io.*;
import java.util.ArrayList;


class Scanner {
    static ArrayList<Lexeme> lexemes = new ArrayList<>();
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
                    //Проверка на нахождение в цикле чтения комментария
                    if (!comment) {
                        /*Word
                        *Если первый символ слова - буква, начинаем чтение всего слова целиком*/
                        if (Character.isLetter(rc)) {
                            pbr.unread(rc);
                            String word = readWord(pbr).toLowerCase();
                            switch (word) {
                                case "begin":
                                case "do":
                                case "var":
                                case "while": {
                                    Lexeme lexeme = new Lexeme(checkLex(keyWords, word), LexType.KeyWord.val, lineNumb, word);
                                    lexemes.add(lexeme);
                                    logger.log(lexeme);
                                }
                                break;
                                case "end": {
                                    int dot = pbr.read();
                                    if (dot == '.') {
                                        Lexeme lexeme = new Lexeme(checkLex(keyWords, "end."), LexType.KeyWord.val, lineNumb, "end.");
                                        lexemes.add(lexeme);
                                        logger.log(lexeme);
                                    } else {
                                        if (dot != -1) {
                                            pbr.unread(dot);
                                        }
                                        Lexeme lexeme = new Lexeme(checkLex(keyWords, "end"), LexType.KeyWord.val, lineNumb, "end");
                                        lexemes.add(lexeme);
                                        logger.log(lexeme);
                                    }
                                }
                                break;
                                case "and":
                                case "or":
                                case "xor": {
                                    Lexeme lexeme = new Lexeme(checkLex(logical, word), LexType.Logical.val, lineNumb, word);
                                    lexemes.add(lexeme);
                                    logger.log(lexeme);
                                }
                                break;
                            /*Word is Id
                            * Если слово не попало ни в один case, значит оно не является ключевым словом
                            * Является идентификатором*/
                                default: {
                                    int id = checkId(word);
                                    Lexeme lexeme = new Lexeme(id, LexType.Id.val, lineNumb, word);
                                    lexemes.add(lexeme);
                                    logger.log(lexeme);
                                }
                                break;
                            }
                            continue;
                        }
                    /*const
                    * Если первый символ - цифра, читаем комнстанту*/
                        if (Character.isDigit(rc)) {
                            pbr.unread(rc);
                            String constWord = readConst(pbr);
                            constWord = Integer.toHexString(Integer.parseInt(constWord));
                            int id = checkConst(constWord);
                            Lexeme lexeme = new Lexeme(id, LexType.Const.val, lineNumb, constWord);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }
                        if (rc == '-') {
                            int digit = pbr.read();
                            if (Character.isDigit(digit)) {
                                pbr.unread(digit);
                                String constWord = readConst(pbr);
                                constWord = Integer.toHexString(Integer.parseInt(constWord));
                                int id = checkConst(constWord);
                                Lexeme lexeme = new Lexeme(checkLex(unary, (char) rc + ""), LexType.Unary.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                lexeme = new Lexeme(id, LexType.Const.val, lineNumb, constWord);
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            } else {
                                if (digit != -1) {
                                    pbr.unread(digit);
                                }
                                Lexeme lexeme = new Lexeme(checkLex(additive, (char) rc + ""), LexType.Additive.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            }
                        }
                        if (rc == '+') {
                            Lexeme lexeme = new Lexeme(checkLex(additive, (char) rc + ""), LexType.Additive.val, lineNumb, (char) rc + "");
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }
                        if (rc == '*') {
                            Lexeme lexeme = new Lexeme(checkLex(multiplicative, (char) rc + ""), LexType.Multiplicative.val, lineNumb, (char) rc + "");
                            lexemes.add(lexeme);
                            logger.log(lexeme);
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
                                Lexeme lexeme = new Lexeme(checkLex(multiplicative, (char) rc + ""), LexType.Multiplicative.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            }
                        }
                        if (rc == '>' || rc == '<' || rc == '=') {
                            Lexeme lexeme = new Lexeme(checkLex(logical, (char) rc + ""), LexType.Logical.val, lineNumb, (char) rc + "");
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }

                        if (rc == ':') {
                            int eq = pbr.read();
                            if (eq == '=') {
                                Lexeme lexeme = new Lexeme(checkLex(assignment, ":="), LexType.Assignment.val, lineNumb, ":=");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            } else {
                                Lexeme lexeme = new Lexeme(-1, LexType.Error.val, lineNumb, "" + (char) rc);
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                if (eq != -1) {
                                    pbr.unread(eq);
                                }
                                continue;
                            }
                        }
                        if (rc == ',' || rc == ';') {
                            Lexeme lexeme = new Lexeme(checkLex(delims, "" + (char) rc), LexType.Delimiter.val, lineNumb, "" + (char) rc);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }

                        if (rc == '(' || rc == ')') {
                            Lexeme lexeme = new Lexeme(checkLex(brackets, "" + (char) rc), LexType.Bracket.val, lineNumb, "" + (char) rc);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }
                        if (rc == Character.MAX_VALUE) {
                            break;
                        }
                        if (rc != ' ') {
                            Lexeme lexeme = new Lexeme(-1, LexType.Error.val, lineNumb, "" + (char) rc);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
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
            Lexeme lexeme = new Lexeme(-2, LexType.EOF.val, lineNumb, "EOF");
            lexemes.add(lexeme);
            logger.log(lexeme);
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