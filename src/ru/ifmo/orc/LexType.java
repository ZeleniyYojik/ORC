package ru.ifmo.orc;

enum LexType {
    Const(0),
    Id(1),
    Unary(2),
    Additive(3),
    Multyplicative(4),
    Logical(5),
    Assignment(6),
    Error(7),
    Delimeter(8),
    KeyWord(1000);
    int val;

    LexType(int i) {
        val = i;
    }
}
