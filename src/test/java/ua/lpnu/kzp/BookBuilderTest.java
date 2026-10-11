
package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookBuilderTest {

    @Test
    void testBuilderCreatesBook() {
        Book book = Book.builder()
                .title("Кобзар")
                .author("Тарас Шевченко")
                .pages(720)
                .price(450.00)
                .build();

        assertEquals("Кобзар", book.getTitle());
        assertEquals("Тарас Шевченко", book.getAuthor());
        assertEquals(720, book.getPages());
        assertEquals(450.00, book.getPrice());
    }

    @Test
    void testBuilderTrimsText() {
        Book book = Book.builder()
                .title("  Лісова пісня  ")
                .author("  Леся Українка  ")
                .pages(180)
                .price(320.00)
                .build();

        assertEquals("Лісова пісня", book.getTitle());
        assertEquals("Леся Українка", book.getAuthor());
    }

    @Test
    void testBuilderRejectsMissingFields() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Book.builder()
                        .author("Автор")
                        .pages(100)
                        .price(50.00)
                        .build()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Book.builder()
                        .title("Книга")
                        .pages(100)
                        .price(50.00)
                        .build()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Book.builder()
                        .title("Книга")
                        .author("Автор")
                        .price(50.00)
                        .build()
        );
    }

    @Test
    void testBuilderRejectsInvalidPrice() {
        double[] invalidPrices = {
                -1.0,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double price : invalidPrices) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> Book.builder()
                            .title("Книга")
                            .author("Автор")
                            .pages(100)
                            .price(price)
                            .build()
            );
        }
    }

    @Test
    void testBuilderAcceptsZeroPrice() {
        Book book = Book.builder()
                .title("Безкоштовна книга")
                .author("Автор")
                .pages(100)
                .price(0.00)
                .build();

        assertEquals(0.00, book.getPrice());
    }

    @Test
    void testBuilderCreatesIndependentBooks() {
        Book.Builder builder = Book.builder()
                .title("Перша книга")
                .author("Автор")
                .pages(100)
                .price(50.00);

        Book first = builder.build();

        builder.title("Друга книга")
                .pages(200)
                .price(75.00);

        Book second = builder.build();

        assertNotSame(first, second);

        assertEquals("Перша книга", first.getTitle());
        assertEquals(100, first.getPages());
        assertEquals(50.00, first.getPrice());

        assertEquals("Друга книга", second.getTitle());
        assertEquals(200, second.getPages());
        assertEquals(75.00, second.getPrice());
    }

    @Test
    void testBuilderReturnsNewObjectEachTime() {
        Book.Builder builder = Book.builder()
                .title("Книга")
                .author("Автор")
                .pages(100)
                .price(50.00);

        Book first = builder.build();
        Book second = builder.build();

        assertNotSame(first, second);
        assertEquals(first.getTitle(), second.getTitle());
        assertEquals(first.getPages(), second.getPages());
        assertEquals(first.getPrice(), second.getPrice());
    }
}
