import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Что-то пошло не так при создании файла.");
            e.printStackTrace();
        }
    }

    public static HashMap<Toy, Double> getStartingMap() {
        File input = new File("starting_input.txt");
        createFile(input);
        File inputAbsolute = new File(input.getAbsolutePath());
        HashMap<Toy, Double> startingMap = new HashMap<>();
        try {
            Scanner scanner = new Scanner(inputAbsolute);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String toy = line.substring(0, line.lastIndexOf(' '));
                double weight = Double.parseDouble(line.substring(line.lastIndexOf(' ') + 1));
                try {
                    startingMap.putIfAbsent(Toy.parseToy(toy), weight);
                } catch (IllegalArgumentException e) {
                    System.out.println("Не удалось обработать строку: " + line);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
        }
        return startingMap;
    }

    public static void giveawayAll(ToyGiveaway tg) {
        File output = new File("output.txt");
        createFile(output);
        try {
            FileWriter fw = new FileWriter(output);
            Set<Toy> toySet = tg.getToySet();
            fw.append("Начат розыгрыш всего. Таблица шансов:\n");
            fw.append(tg.toString());
            fw.append('\n');
            while (!tg.isEmpty()) {
                String winner = String.format("%s\n", tg.giveaway().toString());
                fw.append(winner);
                Set<Toy> newToySet = tg.getToySet();
                if(!newToySet.containsAll(toySet)) {
                    toySet.removeAll(newToySet);
                    for (Toy toy : toySet) {
                        fw.append(String.format("Разыграны все %s\n", toy.getName()));
                    }
                    if (!newToySet.isEmpty()) {
                        fw.append("Новая таблица шансов:\n");
                        fw.append(tg.toString());
                        fw.append('\n');
                        toySet = newToySet;
                    } else {
                        fw.append("Разыграны все игрушки.");
                    }
                }
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Что-то пошло не так при записи в файл.");
            e.printStackTrace();
        }
    }

    public static void printInfo(ToyGiveaway tg) {
        double sum = 0;
        for (double weight : tg.getWeights()) {
            sum += weight;
        }
        System.out.println(tg);
        System.out.printf("Сумма шансов: %f\n", sum);
    }

    public static void main(String[] args) {
        ToyGiveaway tg = new ToyGiveaway(getStartingMap());
        printInfo(tg);
        System.out.println("Добавляем игрушку без указанного шанса:");
        tg.addToyFromString("5 toy5 6");
        printInfo(tg);
        System.out.println("Добавляем игрушку с указанным шансом:");
        tg.addToyFromString("6 toy6 8 10");
        printInfo(tg);
        System.out.println("Меняем шанс выпадения первой игрушки:");
        tg.changeWeight(Toy.parseToy("1 toy1 12"), 15);
        printInfo(tg);
        giveawayAll(tg);
    }
}
