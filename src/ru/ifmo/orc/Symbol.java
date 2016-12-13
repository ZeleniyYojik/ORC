package ru.ifmo.orc;

public class Symbol {
    private boolean terminal;
    private int id;
    private int line;

    public Symbol(boolean terminal, int id, int line) {
        this.terminal = terminal;
        this.id = id;
        this.line = line;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return terminal + " " + id;
    }
}
