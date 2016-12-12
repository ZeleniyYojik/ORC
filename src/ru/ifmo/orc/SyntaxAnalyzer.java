package ru.ifmo.orc;

import java.util.*;

public class SyntaxAnalyzer {
    private List<List<Symbol>> grammar = new ArrayList<>();
    private List<Map<Integer, Integer>> table = new ArrayList<>();

    public SyntaxAnalyzer() {
        grammar.addAll(Arrays.asList(
                //Для сдвига индекса, дабы не пересчитывать индексы всех правил
                new ArrayList<Symbol>(),
                //Программа
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(false, 2),
                        new Symbol(false, 3))),
                //Объявление_переменных
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 103),
                        new Symbol(false, 5))),
                //Описание_вычислений
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 101),
                        new Symbol(false, 4),
                        new Symbol(true, 102)
                )),
                //Список_операторов
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(false, 6),
                        new Symbol(false, 20)
                )),
                //Список_переменных
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 104),
                        new Symbol(false, 21)
                )),
                //Оператор 1
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 7)
                )),
                //Оператор 2
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 8)
                )),
                //Оператор 3
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 9)
                )),
                //Присваивание
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 104),
                        new Symbol(true, 108),
                        new Symbol(false, 10),
                        new Symbol(true, 105)
                )),
                //Сложный_оператор
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 16)
                )),
                //Составной_оператор
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 101),
                        new Symbol(false, 4),
                        new Symbol(true, 107)
                )),
                //Выражение 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(false, 11),
                        new Symbol(false, 15)
                )),
                //ВЫражение 2
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 15)
                )),
                //Унарный оператор
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(true, 110)
                )),
                //Подвыражение
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(false, 13),
                        new Symbol(false, 17)
                )),
                //T
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //F 1
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 15)
                )),
                //F 2
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 118),
                        new Symbol(false, 10),
                        new Symbol(true, 119)
                )),
                //Операнд 1
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(true, 104)
                )),
                //Операнд 2
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(true, 120)
                )),
                //Оператор_цикла
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 121),
                        new Symbol(false, 10),
                        new Symbol(true, 122),
                        new Symbol(false, 6)
                )),
                //Подвыражение#1 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 109),
                        new Symbol(false, 13),
                        new Symbol(false, 17)
                )),
                //Подвыражение#1 2
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 110),
                        new Symbol(false, 13),
                        new Symbol(false, 17)
                )),
                //Подвыражение#1 3
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 124)
                )),
                //T#1 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 111),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 2
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 112),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 3
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 113),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 4
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 114),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 5
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 115),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 6
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 116),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 7
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 117),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //T#1 8
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 124),
                        new Symbol(false, 14),
                        new Symbol(false, 18)
                )),
                //Список_переменных#1 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 124)
                )),
                //Список_переменных#1 2
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 5)
                )),
                //Список_операторов#1 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 124)
                )),
                //Список_операторов#1 2
                new ArrayList<Symbol>(Collections.singletonList(
                        new Symbol(false, 4)
                )),
                //Список_переменных#2 1
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 106),
                        new Symbol(false, 5)
                )),
                //Список_переменных#2 2
                new ArrayList<Symbol>(Arrays.asList(
                        new Symbol(true, 105),
                        new Symbol(false, 19)
                ))

        ));
        table.addAll(
                Arrays.asList(
                        //1
                        new HashMap<Integer, Integer>() {{
                            put(103, 1);
                        }},
                        //2
                        new HashMap<Integer, Integer>() {{
                            put(103, 2);
                        }},
                        //3
                        new HashMap<Integer, Integer>() {{
                            put(101, 3);
                        }},
                        //4
                        new HashMap<Integer, Integer>() {{
                            put(101, 4);
                            put(104, 4);
                            put(121, 4);
                        }},
                        //5
                        new HashMap<Integer, Integer>() {{
                            put(104, 5);
                        }},
                        //6
                        new HashMap<Integer, Integer>() {{
                            put(101, 8);
                            put(104, 6);
                            put(121, 7);
                        }},
                        //7
                        new HashMap<Integer, Integer>() {{
                            put(104, 9);
                        }},
                        //8
                        new HashMap<Integer, Integer>() {{
                            put(121, 10);
                        }},
                        //9
                        new HashMap<Integer, Integer>() {{
                            put(101, 11);
                        }},
                        //10
                        new HashMap<Integer, Integer>() {{
                            put(104, 13);
                            put(110, 12);
                            put(118, 13);
                            put(120, 13);
                        }},
                        //11
                        new HashMap<Integer, Integer>() {{
                            put(110, 14);
                        }},
                        //12
                        new HashMap<Integer, Integer>() {{
                            put(104, 15);
                            put(118, 15);
                            put(120, 15);
                        }},
                        //13
                        new HashMap<Integer, Integer>() {{
                            put(104, 16);
                            put(118, 16);
                            put(120, 16);
                        }},
                        //14
                        new HashMap<Integer, Integer>() {{
                            put(104, 17);
                            put(118, 18);
                            put(120, 17);
                        }},
                        //15
                        new HashMap<Integer, Integer>() {{
                            put(104, 19);
                            put(120, 20);
                        }},
                        //16
                        new HashMap<Integer, Integer>() {{
                            put(121, 21);
                        }},
                        //17
                        new HashMap<Integer, Integer>() {{
                            put(105, 24);
                            put(109, 22);
                            put(110, 23);
                            put(119, 17);
                            put(122, 24);
                        }},
                        //18
                        new HashMap<Integer, Integer>() {{
                            put(105, 32);
                            put(109, 32);
                            put(110, 32);
                            put(111, 25);
                            put(112, 26);
                            put(113, 27);
                            put(114, 28);
                            put(115, 29);
                            put(116, 30);
                            put(117, 31);
                            put(119, 32);
                            put(122, 32);
                        }},
                        //19
                        new HashMap<Integer, Integer>() {{
                            put(101, 33);
                            put(104, 34);
                        }},
                        //20
                        new HashMap<Integer, Integer>() {{
                            put(101, 36);
                            put(102, 35);
                            put(104, 36);
                            put(107, 35);
                            put(121, 36);
                        }},
                        //21
                        new HashMap<Integer, Integer>() {{
                            put(105, 38);
                            put(106, 37);
                        }}
                ));
    }

    public static String analyze(List<Symbol> symbols) {
        Symbol program = new Symbol(false, 1);
        Deque<Symbol> stack = new ArrayDeque<>();
        int head = 1;
        stack.push(program);
        for (int i = 0; i < symbols.size(); i++) {

        }
        return "Programm is correct";
    }
}
