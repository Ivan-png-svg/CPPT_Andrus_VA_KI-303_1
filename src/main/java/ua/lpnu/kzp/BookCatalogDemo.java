
package ua.lpnu.kzp;

import java.util.List;
import java.util.Locale;

/**
 * Демонстрація спадкування, поліморфізму та sealed-ієрархії.
 * Лабораторна робота №3, варіант 1.
 */
public final class BookCatalogDemo {

    /**
     * Забороняє створення екземплярів демонстраційного класу.
     */
    private BookCatalogDemo() {
    }

    /**
     * Повертає опис конкретного виду книжки.
     *
     * <p>Switch охоплює всі можливі типи ієрархії Book:
     * PrintedBook, ElectronicBook та сам Book.</p>
     *
     * <p>Метод calculateReadingHours() тут не дублюється:
     * час читання визначається поліморфним викликом.</p>
     *
     * @param book книжка
     * @return опис виду книжки
     */
    public static String describeBook(Book book) {
        return switch (book) {
            case PrintedBook printed ->
                    printed.isHardcover()
                            ? "Друкована книжка у твердій палітурці"
                            : "Друкована книжка у м'якій палітурці";

            case ElectronicBook electronic ->
                    String.format(
                            Locale.ROOT,
                            "Електронна книжка, розмір файлу: %.2f МБ",
                            electronic.getFileSizeMb()
                    );

            case Book base ->
                    "Звичайна книжка каталогу: " + base.getTitle();
        };
    }

    /**
     * Запускає демонстрацію поліморфізму та pattern matching.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        List<Book> books = List.of(
                new PrintedBook(
                        "Кобзар",
                        "Тарас Шевченко",
                        300,
                        450.0,
                        true
                ),
                new ElectronicBook(
                        "Основи Java",
                        "Іван Петренко",
                        300,
                        200.0,
                        5.5
                )
        );

        System.out.println("===== ПОЛІМОРФІЗМ КНИЖОК =====");

        for (Book book : books) {
            System.out.println("Назва: " + book.getTitle());
            System.out.println("Формат: " + book.getFormat().label());

            // Поліморфізм: викликається реалізація фактичного підтипу.
            System.out.printf(
                    Locale.ROOT,
                    "Час читання: %.2f год%n",
                    book.calculateReadingHours()
            );

            // Pattern matching: описуємо специфічні властивості підтипів.
            System.out.println("Опис: " + describeBook(book));
            System.out.println();
        }
    }
}
