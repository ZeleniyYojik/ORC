package ru.ifmo.orc;

public class Symbol {
    private boolean terminal;
    private int id;

    public Symbol(boolean terminal, int id) {
        this.terminal = terminal;
        this.id = id;
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
}
