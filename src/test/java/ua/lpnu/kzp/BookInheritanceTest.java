
package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тести спадкування та поліморфізму книжок.
 * Лабораторна робота №3, варіант 1.
 */
class BookInheritanceTest {

    @Test
    void printedBookInheritsCommonFields() {
        PrintedBook book = new PrintedBook(
                "Кобзар", "Тарас Шевченко", 300, 450.0, true
        );

        assertEquals("Кобзар", book.getTitle());
        assertEquals("Тарас Шевченко", book.getAuthor());
        assertEquals(300, book.getPages());
        assertEquals(450.0, book.getPrice());
        assertTrue(book.isHardcover());
        assertEquals(BookFormat.PRINTED, book.getFormat());
    }

    @Test
    void electronicBookInheritsCommonFields() {
        ElectronicBook book = new ElectronicBook(
                "Java", "Автор", 400, 200.0, 5.5
        );

        assertEquals("Java", book.getTitle());
        assertEquals(400, book.getPages());
        assertEquals(200.0, book.getPrice());
        assertEquals(5.5, book.getFileSizeMb());
        assertEquals(BookFormat.ELECTRONIC, book.getFormat());
    }

    @Test
    void polymorphismUsesDifferentImplementations() {
        List<Book> books = List.of(
                new PrintedBook("A", "Автор", 120, 100, true),
                new ElectronicBook("B", "Автор", 120, 80, 2.5)
        );

        assertEquals(4.0, books.get(0).calculateReadingHours());
        assertEquals(3.0, books.get(1).calculateReadingHours());

        assertNotEquals(
                books.get(0).calculateReadingHours(),
                books.get(1).calculateReadingHours()
        );
    }

    @Test
    void baseBookRemainsCompatibleWithPreviousLabs() {
        Book book = Book.fromCsv("Кобзар;Шевченко;300;450");

        assertEquals("Кобзар", book.getTitle());
        assertEquals(300, book.getPages());
        assertEquals(450.0, book.getPrice());
        assertEquals(10.0, book.calculateReadingHours());
    }

    @Test
    void electronicBookRejectsInvalidFileSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ElectronicBook(
                        "Java", "Автор", 200, 100, 0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ElectronicBook(
                        "Java", "Автор", 200, 100, Double.NaN
                )
        );
    }

    @Test
    void printedBooksWithSameDataAreEqual() {
        PrintedBook first = new PrintedBook(
                "Кобзар", "Шевченко", 300, 450, true
        );
        PrintedBook second = new PrintedBook(
                "Кобзар", "Шевченко", 300, 450, true
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        Set<Book> books = new HashSet<>();
        books.add(first);
        books.add(second);

        assertEquals(1, books.size());
    }

    @Test
    void differentCoversMakePrintedBooksDifferent() {
        PrintedBook hardcover = new PrintedBook(
                "Кобзар", "Шевченко", 300, 450, true
        );
        PrintedBook paperback = new PrintedBook(
                "Кобзар", "Шевченко", 300, 450, false
        );

        assertNotEquals(hardcover, paperback);
    }

    @Test
    void electronicBooksWithSameDataAreEqual() {
        ElectronicBook first = new ElectronicBook(
                "Java", "Автор", 400, 200, 5.5
        );
        ElectronicBook second = new ElectronicBook(
                "Java", "Автор", 400, 200, 5.5
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        Set<Book> books = new HashSet<>();
        books.add(first);
        books.add(second);

        assertEquals(1, books.size());
    }

    @Test
    void differentBookTypesAreNotEqual() {
        Book printed = new PrintedBook(
                "Java", "Автор", 200, 100, true
        );
        Book electronic = new ElectronicBook(
                "Java", "Автор", 200, 100, 3.0
        );

        assertNotEquals(printed, electronic);
        assertNotEquals(electronic, printed);
    }

    @Test
    void bookFormatContainsBothValues() {
        assertArrayEquals(
                new BookFormat[]{
                        BookFormat.PRINTED,
                        BookFormat.ELECTRONIC
                },
                BookFormat.values()
        );
    }
}
