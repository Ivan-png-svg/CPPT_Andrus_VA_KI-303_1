
package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testConstructorAndGetters() {
        Book book = new Book(
                "Кобзар",
                "Тарас Шевченко",
                720,
                450.00
        );

        assertEquals("Кобзар", book.getTitle());
        assertEquals("Тарас Шевченко", book.getAuthor());
        assertEquals(720, book.getPages());
        assertEquals(450.00, book.getPrice());
    }

    @Test
    void testTrimming() {
        Book book = new Book(
                "  Кобзар  ",
                "  Тарас Шевченко  ",
                720,
                450.00
        );

        assertEquals("Кобзар", book.getTitle());
        assertEquals("Тарас Шевченко", book.getAuthor());
    }

    @Test
    void testFromCsv() {
        Book book = Book.fromCsv(
                "Лісова пісня;Леся Українка;180;320.00"
        );

        assertEquals("Лісова пісня", book.getTitle());
        assertEquals("Леся Українка", book.getAuthor());
        assertEquals(180, book.getPages());
        assertEquals(320.00, book.getPrice());
    }

    @Test
    void testToString() {
        Book book = new Book(
                "Кобзар",
                "Тарас Шевченко",
                720,
                450.00
        );

        assertEquals(
                "Кобзар (Тарас Шевченко): 720 стор., 450.00 грн",
                book.toString()
        );
    }

    @Test
    void testInvalidTitleAndAuthor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Book("", "Автор", 100, 50.00)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Book("Книга", " ", 100, 50.00)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Book(null, "Автор", 100, 50.00)
        );
    }

    @Test
    void testInvalidPages() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Book("Книга", "Автор", 0, 50.00)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Book("Книга", "Автор", -10, 50.00)
        );
    }

    @Test
    void testInvalidPrices() {
        double[] invalidPrices = {
                -1.0,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double price : invalidPrices) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new Book("Книга", "Автор", 100, price)
            );
        }
    }

    @Test
    void testZeroPrice() {
        Book book = new Book(
                "Безкоштовна книга",
                "Автор",
                100,
                0.00
        );

        assertEquals(0.00, book.getPrice());
    }

    @Test
    void testInvalidCsv() {
        String[] invalidLines = {
                "",
                "Книга;Автор;100",
                "Книга;Автор;abc;50.00",
                "Книга;Автор;0;50.00",
                "Книга;Автор;100;NaN",
                "Книга;Автор;100;Infinity"
        };

        for (String line : invalidLines) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> Book.fromCsv(line)
            );
        }
    }
}
