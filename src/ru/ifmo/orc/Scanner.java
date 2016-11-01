package ru.ifmo.orc;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class Scanner {
    static List<Lexeme> lexemes = new ArrayList<>();
    static List<String> ids = new ArrayList<>();
    static List<String> consts = new ArrayList<>();
    static List<String> delims = new ArrayList<>();
    static List<String> openBrackets = new ArrayList<>();
    static List<String> closeBrackets = new ArrayList<>();
    static List<String> keyWords = new ArrayList<>();
    static List<String> logical = new ArrayList<>();
    static List<String> unary = new ArrayList<>();
    static List<String> additive = new ArrayList<>();
    static List<String> multiplicative = new ArrayList<>();
    static List<String> assignment = new ArrayList<>();

    static void scan(String sourcePath, String outputPath) throws Exception {
        delims.addAll(Arrays.asList(",", ";"));
        openBrackets.addAll(Arrays.asList("("));
        closeBrackets.addAll(Arrays.asList(")"));
        keyWords.addAll(Arrays.asList("begin", "end", "end.", "while", "do", "var"));
        logical.addAll(Arrays.asList("and", "or", "xor", ">", "<", "="));
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
                                    Lexeme lexeme = new Lexeme(keyWords.indexOf(word), LexType.KeyWord.val, lineNumb, word);
                                    lexemes.add(lexeme);
                                    logger.log(lexeme);
                                }
                                break;
                                case "end": {
                                    int dot = pbr.read();
                                    if (dot == '.') {
                                        Lexeme lexeme = new Lexeme(keyWords.indexOf("end."), LexType.KeyWord.val, lineNumb, "end.");
                                        lexemes.add(lexeme);
                                        logger.log(lexeme);
                                    } else {
                                        if (dot != -1) {
                                            pbr.unread(dot);
                                        }
                                        Lexeme lexeme = new Lexeme(keyWords.indexOf("end"), LexType.KeyWord.val, lineNumb, "end");
                                        lexemes.add(lexeme);
                                        logger.log(lexeme);
                                    }
                                }
                                break;
                                case "and":
                                case "or":
                                case "xor": {
                                    Lexeme lexeme = new Lexeme(logical.indexOf(word), LexType.Logical.val, lineNumb, word);
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
                            Lexeme l = lexemes.get(lexemes.size() - 1);
                            if (!lexemes.isEmpty() && (l.type == LexType.Const.val || l.type == LexType.CloseBracket.val || l.type == LexType.Id.val)) {
                                Lexeme lexeme = new Lexeme(additive.indexOf((char) rc + ""), LexType.Additive.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            } else {
                                Lexeme lexeme = new Lexeme(unary.indexOf((char) rc + ""), LexType.Unary.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            }
                        }
                        if (rc == '+') {
                            Lexeme lexeme = new Lexeme(additive.indexOf((char) rc + ""), LexType.Additive.val, lineNumb, (char) rc + "");
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }
                        if (rc == '*') {
                            Lexeme lexeme = new Lexeme(multiplicative.indexOf((char) rc + ""), LexType.Multiplicative.val, lineNumb, (char) rc + "");
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
                                Lexeme lexeme = new Lexeme(multiplicative.indexOf((char) rc + ""), LexType.Multiplicative.val, lineNumb, (char) rc + "");
                                lexemes.add(lexeme);
                                logger.log(lexeme);
                                continue;
                            }
                        }
                        if (rc == '>' || rc == '<' || rc == '=') {
                            Lexeme lexeme = new Lexeme(logical.indexOf((char) rc + ""), LexType.Logical.val, lineNumb, (char) rc + "");
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }

                        if (rc == ':') {
                            int eq = pbr.read();
                            if (eq == '=') {
                                Lexeme lexeme = new Lexeme(assignment.indexOf(":="), LexType.Assignment.val, lineNumb, ":=");
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
                            Lexeme lexeme = new Lexeme(delims.indexOf("" + (char) rc), LexType.Delimiter.val, lineNumb, "" + (char) rc);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }

                        if (rc == '(') {
                            Lexeme lexeme = new Lexeme(openBrackets.indexOf((char) rc + ""), LexType.OpenBracket.val, lineNumb, "" + (char) rc);
                            lexemes.add(lexeme);
                            logger.log(lexeme);
                            continue;
                        }
                        if (rc == ')') {
                            Lexeme lexeme = new Lexeme(closeBrackets.indexOf((char) rc + ""), LexType.CloseBracket.val, lineNumb, "" + (char) rc);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int checkId(String word) {
        int id = ids.indexOf(word);
        if (id == -1) {
            ids.add(word);
            return ids.size() - 1;
        } else return id;
    }

    private static int checkConst(String word) {
        int id = consts.indexOf(word);
        if (id == -1) {
            consts.add(word);
            return consts.size() - 1;
        } else return id;
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