package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Головний клас консольного застосунку для обробки каталогу книжок (Варіант 1).
 */
public final class Main {

    private Main() {
        // Приватний конструктор для запобігання створенню екземплярів службового класу
    }
     /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка
     */

   
    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");
        Path outputPath = Path.of("out", "report.txt");
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> {
                    printHelp();
                    return;
                }
                case "--version" -> {
                    printVersion();
                    return;
                }
                case "--input" -> {
                    if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                        inputPath = Path.of(args[++i]);
                    } else {
                        System.err.println("Помилка: Не вказано шлях до вхідного файлу після --input");
                        return;
                    }
                }
                case "--output" -> {
                    if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                        outputPath = Path.of(args[++i]);
                    } else {
                        System.err.println("Помилка: Не вказано шлях до вихідного файлу після --output");
                        return;
                    }
                }
                default -> {
                    System.err.println("Невідомий аргумент: " + args[i]);
                    printHelp();
                    return;
                }
            }
        }

        processCatalog(inputPath, outputPath);
    }

    /**
     * Виводить довідку про використання програми.
     */
    public static void printHelp() {
        System.out.println("""
            Використання: java -jar lab01.jar [опції]
            Опції:
              --help           Вивести цю довідку
              --version        Вивести версію програми та номер збірки
              --input <path>   Вказати шлях до вхідного CSV файлу (за замовчуванням: data/input.csv)
              --output <path>  Вказати шлях до вихідного файлу звіту (за замовчуванням: out/report.txt)
            """);
    }

    /**
     * Виводить інформацію про версію та номер CI-збірки.
     */
    /**
     * Виводить інформацію про версію та номер CI-збірки.
     */
    public static void printVersion() {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                prop.load(input);
                System.out.printf(Locale.ROOT, "LAB01 - Варіант 1 (Каталог книжок) v%s (build: %s)%n",
                        prop.getProperty("version", "1.0.0"),
                        prop.getProperty("buildNumber", "local"));
            } else {
                System.out.println("LAB01 - Варіант 1 (Каталог книжок) v1.0.0 (local build)");
            }
        } catch (IOException e) {
            System.out.println("LAB01 - Варіант 1 (Каталог книжок) v1.0.0 (local build)");
        }
    }

    /**
     * Зчитує, валідує CSV-файл, обчислює показники та формує звіт.
     *
     * @param inputPath  шлях до вхідного CSV
     * @param outputPath шлях для збереження звіту
     */
    public static void processCatalog(Path inputPath, Path outputPath) {
        if (!Files.exists(inputPath)) {
            System.err.println("Помилка: Вхідний файл не знайдено за шляхом " + inputPath);
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Помилка зчитування файлу: " + e.getMessage());
            return;
        }

        int validCount = 0;
        int invalidCount = 0;
        int totalPages = 0;
        double totalPriceSum = 0.0;
        double maxPrice = -1.0;
        String mostExpensiveBook = "";

        List<String> errors = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] fields = line.split(";", -1);
            if (fields.length != 4) {
                invalidCount++;
                errors.add(String.format("Рядок %d: некоректна кількість полів (%d замість 4)", i + 1, fields.length));
                continue;
            }

            String title = fields[0].trim();
            String author = fields[1].trim();
            String pagesStr = fields[2].trim();
            String priceStr = fields[3].trim();

            if (title.isEmpty() || author.isEmpty()) {
                invalidCount++;
                errors.add(String.format("Рядок %d: порожня назва книжки або автор", i + 1));
                continue;
            }

            int pages;
            double price;

            try {
                pages = Integer.parseInt(pagesStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                invalidCount++;
                errors.add(String.format("Рядок %d: помилка числового формату для сторінок або ціни", i + 1));
                continue;
            }

            if (pages <= 0 || price < 0) {
                invalidCount++;
                errors.add(String.format("Рядок %d: від'ємна ціна або кількість сторінок <= 0", i + 1));
                continue;
            }

            validCount++;
            totalPages += pages;
            totalPriceSum += price;

            if (price > maxPrice) {
                maxPrice = price;
                mostExpensiveBook = String.format("%s (%s)", title, author);
            }
        }

        double averagePages = validCount > 0 ? (double) totalPages / validCount : 0.0;

        StringBuilder report = new StringBuilder();
        report.append("===== ЗВІТ КАТАЛОГУ КНИЖОК =====\n");
        report.append(String.format(Locale.ROOT, "Правильних записів: %d%n", validCount));
        report.append(String.format(Locale.ROOT, "Неправильних записів: %d%n", invalidCount));
        report.append(String.format(Locale.ROOT, "Середня кількість сторінок: %.2f%n", averagePages));
        report.append(String.format(Locale.ROOT, "Найдорожча книга: %s (%.2f грн)%n",
                mostExpensiveBook.isEmpty() ? "N/A" : mostExpensiveBook, maxPrice < 0 ? 0.0 : maxPrice));
        report.append(String.format(Locale.ROOT, "Сумарна вартість каталогу: %.2f грн%n", totalPriceSum));

        if (!errors.isEmpty()) {
            report.append("\nДеталі помилок:\n");
            for (String err : errors) {
                report.append(" - ").append(err).append("\n");
            }
        }

        System.out.print(report.toString());

        try {
    Path parentDir = outputPath.getParent();
    if (parentDir != null) {
        Files.createDirectories(parentDir);
    }
    Files.writeString(outputPath, report.toString(), StandardCharsets.UTF_8);
} catch (IOException e) {
    System.err.println("Помилка запису файлу звіту: " + e.getMessage());
}
    }
}