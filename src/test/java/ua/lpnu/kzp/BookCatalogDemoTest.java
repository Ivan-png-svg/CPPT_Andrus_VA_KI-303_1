
package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Тести демонстрації sealed-ієрархії та pattern matching.
 * Лабораторна робота №3, варіант 1.
 */
class BookCatalogDemoTest {

    @Test
    void describesHardcoverPrintedBook() {
        Book book = new PrintedBook(
                "Кобзар",
                "Тарас Шевченко",
                300,
                450.0,
                true
        );

        assertEquals(
                "Друкована книжка у твердій палітурці",
                BookCatalogDemo.describeBook(book)
        );
    }

    @Test
    void describesPaperbackPrintedBook() {
        Book book = new PrintedBook(
                "Українські казки",
                "Народна творчість",
                120,
                150.0,
                false
        );

        assertEquals(
                "Друкована книжка у м'якій палітурці",
                BookCatalogDemo.describeBook(book)
        );
    }

    @Test
    void describesElectronicBook() {
        Book book = new ElectronicBook(
                "Основи Java",
                "Іван Петренко",
                300,
                200.0,
                5.5
        );

        assertEquals(
                "Електронна книжка, розмір файлу: 5.50 МБ",
                BookCatalogDemo.describeBook(book)
        );
    }

    @Test
    void describesBaseBook() {
        Book book = new Book(
                "Алгоритми",
                "Автор",
                200,
                300.0
        );

        assertEquals(
                "Звичайна книжка каталогу: Алгоритми",
                BookCatalogDemo.describeBook(book)
        );
    }

    @Test
    void usesPolymorphicReadingTime() {
        Book printed = new PrintedBook(
                "Друкована",
                "Автор",
                300,
                100.0,
                true
        );

        Book electronic = new ElectronicBook(
                "Електронна",
                "Автор",
                300,
                100.0,
                2.0
        );

        assertEquals(10.0, printed.calculateReadingHours(), 0.0001);
        assertEquals(7.5, electronic.calculateReadingHours(), 0.0001);
    }
}
