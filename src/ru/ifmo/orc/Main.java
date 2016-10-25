package ru.ifmo.orc;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner.scan("resources/program.txt", "resources/out.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
