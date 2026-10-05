package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Консольна програма для обробки каталогу книжок.
 *
 * Формат запису:
 * title;author;pages;price
 */
public final class Main {

    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");

    private Main() {
        // Службовий клас не потребує створення екземплярів.
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        Path input = DEFAULT_INPUT;
        Path output = DEFAULT_OUTPUT;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> {
                    printHelp();
                    return;
                }
                case "--version" -> {
                    System.out.println("1.0.0");
                    return;
                }
                case "--input" -> {
                    if (i + 1 >= args.length) {
                        System.err.println("Помилка: після --input потрібно вказати шлях.");
                        return;
                    }
                    input = Path.of(args[++i]);
                }
                case "--output" -> {
                    if (i + 1 >= args.length) {
                        System.err.println("Помилка: після --output потрібно вказати шлях.");
                        return;
                    }
                    output = Path.of(args[++i]);
                }
                default -> {
                    System.err.println("Невідомий аргумент: " + args[i]);
                    printHelp();
                    return;
                }
            }
        }

        try {
            String report = processCatalog(input);
            System.out.print(report);

            Path parent = output.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(output, report, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Помилка роботи з файлом: " + e.getMessage());
        }
    }

    /**
     * Читає каталог, перевіряє записи та формує звіт.
     *
     * @param input шлях до CSV-файлу
     * @return сформований текст звіту
     * @throws IOException якщо файл неможливо прочитати
     */
    static String processCatalog(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);

        int validCount = 0;
        int totalPages = 0;
        double totalCatalogValue = 0.0;
        double maxPrice = Double.NEGATIVE_INFINITY;

        String mostExpensiveTitle = "";
        String mostExpensiveAuthor = "";

        List<String> errors = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            if (line.trim().isEmpty()) {
                errors.add("Рядок " + lineNumber + ": порожній рядок");
                continue;
            }

            String[] fields = line.split(";", -1);

            if (fields.length != 4) {
                errors.add("Рядок " + lineNumber + ": неправильна кількість полів");
                continue;
            }

            String title = fields[0].trim();
            String author = fields[1].trim();
            String pagesText = fields[2].trim();
            String priceText = fields[3].trim();

            if (title.isEmpty() || author.isEmpty()) {
                errors.add("Рядок " + lineNumber + ": порожня назва книжки або автор");
                continue;
            }

            try {
                int pages = Integer.parseInt(pagesText);
                double price = Double.parseDouble(priceText);

                if (pages <= 0) {
                    errors.add(
                            "Рядок " + lineNumber
                                    + ": кількість сторінок повинна бути більшою за 0"
                    );
                    continue;
                }

                if (price < 0) {
                    errors.add(
                            "Рядок " + lineNumber
                                    + ": ціна не може бути від'ємною"
                    );
                    continue;
                }

                validCount++;
                totalPages += pages;
                totalCatalogValue += price;

                if (price > maxPrice) {
                    maxPrice = price;
                    mostExpensiveTitle = title;
                    mostExpensiveAuthor = author;
                }
            } catch (NumberFormatException e) {
                errors.add(
                        "Рядок " + lineNumber
                                + ": неправильний числовий формат"
                );
            }
        }

        double averagePages = validCount == 0
                ? 0.0
                : (double) totalPages / validCount;

        StringBuilder report = new StringBuilder();

        report.append("===== ЗВІТ КАТАЛОГУ КНИЖОК =====")
                .append(System.lineSeparator());

        report.append(String.format(
                Locale.ROOT,
                "Коректних записів: %d%n",
                validCount
        ));

        report.append(String.format(
                Locale.ROOT,
                "Некоректних записів: %d%n",
                errors.size()
        ));

        report.append(String.format(
                Locale.ROOT,
                "Середня кількість сторінок: %.2f%n",
                averagePages
        ));

        if (validCount > 0) {
            report.append(String.format(
                    Locale.ROOT,
                    "Найдорожча книга: %s (%s) (%.2f грн)%n",
                    mostExpensiveTitle,
                    mostExpensiveAuthor,
                    maxPrice
            ));
        } else {
            report.append("Найдорожча книга: немає")
                    .append(System.lineSeparator());
        }

        report.append(String.format(
                Locale.ROOT,
                "Сумарна вартість каталогу: %.2f грн%n",
                totalCatalogValue
        ));

        if (!errors.isEmpty()) {
            report.append(System.lineSeparator())
                    .append("Помилки:")
                    .append(System.lineSeparator());

            for (String error : errors) {
                report.append(" - ")
                        .append(error)
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    /**
     * Виводить довідку про використання програми.
     */
    private static void printHelp() {
        System.out.println(
                "Використання: java -jar lab01.jar "
                        + "[--help] [--version] "
                        + "[--input <файл>] [--output <файл>]"
        );
    }
}
