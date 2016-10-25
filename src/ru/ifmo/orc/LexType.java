package ru.ifmo.orc;

enum LexType {
    Const(0),
    Id(1),
    Unary(2),
    Additive(3),
    Multiplicative(4),
    Logical(5),
    Assignment(6),
    Delimiter(7),
    Bracket(8),
    Error(9),
    KeyWord(1000);
    int val;

    LexType(int i) {
        val = i;
    }
}
