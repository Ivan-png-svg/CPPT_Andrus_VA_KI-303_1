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
 * <p>Лабораторна робота №2, варіант 1.</p>
 *
 * <p>Формат CSV: title;author;pages;price</p>
 */
public final class Main {

    private static final Path DEFAULT_INPUT =
            Path.of("data", "input.csv");

    private static final Path DEFAULT_OUTPUT =
            Path.of("out", "report.txt");

    private Main() {
        // Службовий клас не потребує екземплярів.
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
                    System.out.println(VersionInfo.version());
                    return;
                }
                case "--input" -> {
                    if (i + 1 >= args.length) {
                        System.err.println(
                                "Помилка: після --input потрібно вказати шлях."
                        );
                        return;
                    }
                    input = Path.of(args[++i]);
                }
                case "--output" -> {
                    if (i + 1 >= args.length) {
                        System.err.println(
                                "Помилка: після --output потрібно вказати шлях."
                        );
                        return;
                    }
                    output = Path.of(args[++i]);
                }
                default -> {
                    System.err.println(
                            "Невідомий аргумент: " + args[i]
                    );
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

            Files.writeString(
                    output,
                    report,
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            System.err.println(
                    "Помилка роботи з файлом: " + e.getMessage()
            );
        }
    }

    /**
     * Читає CSV-каталог і формує текстовий звіт.
     *
     * @param input шлях до CSV-файлу
     * @return сформований звіт
     * @throws IOException якщо файл неможливо прочитати
     */
    static String processCatalog(Path input) throws IOException {

        List<String> lines =
                Files.readAllLines(input, StandardCharsets.UTF_8);

        List<Book> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Розбір CSV виконується класом Book.
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;

            try {
                Book book = Book.fromCsv(lines.get(i));
                books.add(book);
            } catch (IllegalArgumentException e) {
                errors.add(
                        "Рядок " + lineNumber + ": " + e.getMessage()
                );
            }
        }

        int validCount = books.size();

        // long запобігає переповненню під час
        // підсумовування значень типу int.
        long totalPages = 0L;

        double totalCatalogValue = 0.0;
        double maxPrice = Double.NEGATIVE_INFINITY;

        Book mostExpensiveBook = null;

        for (Book book : books) {

            PagePrice pagePrice = new PagePrice(
                    book.getPages(),
                    book.getPrice()
            );

            totalPages += pagePrice.pages();
            totalCatalogValue += pagePrice.price();

            if (pagePrice.price() > maxPrice) {
                maxPrice = pagePrice.price();
                mostExpensiveBook = book;
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

        if (mostExpensiveBook != null) {
            report.append(String.format(
                    Locale.ROOT,
                    "Найдорожча книга: %s (%s) (%.2f грн)%n",
                    mostExpensiveBook.getTitle(),
                    mostExpensiveBook.getAuthor(),
                    mostExpensiveBook.getPrice()
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