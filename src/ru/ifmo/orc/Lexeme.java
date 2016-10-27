package ru.ifmo.orc;

class Lexeme {
    final int id;
    final int type;
    final int lineNum;
    final String word;

    Lexeme(int id, int type, int lineNum, String word) {
        this.id = id;
        this.type = type;
        this.lineNum = lineNum;
        this.word = word;
    }

    @Override
    public String toString() {
        if (type == LexType.Error.val) {
            return "<" + word + ";" + type/* + ";" + lineNum */ + ">";
        } else {
            return "<" + id + ";" + type/* + ";" + lineNum */ + ">";
        }
    }
}
