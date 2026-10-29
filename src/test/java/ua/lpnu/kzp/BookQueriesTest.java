package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests all queries, empty input, ties, duplicates and limit boundaries. */
class BookQueriesTest {
    private final List<Book> books = Lab4Demo.sample();

    @Test void filterBoundaryAndMapping() {
        assertEquals(List.of("Alpha", "Beta", "Delta", "Alpha"),
                BookQueries.titles(BookQueries.since2020(books)));
        assertEquals(6, BookQueries.titles(books).size());
    }

    @Test void groupingAndStatistics() {
        assertEquals(2L, BookQueries.countByGenre(books).get(BookGenre.SCIENCE));
        var stats = BookQueries.pageStatistics(books);
        assertEquals(6, stats.getCount());
        assertEquals(1800, stats.getSum());
        assertEquals(100, stats.getMin());
        assertEquals(550, stats.getMax());
        assertEquals(300, stats.getAverage());
    }

    @Test void topAndTies() {
        assertEquals(List.of("Gamma", "Alpha", "Beta", "Delta", "Alpha"),
                BookQueries.titles(BookQueries.topN(books, 5)));
        assertTrue(BookQueries.topN(books, 0).isEmpty());
        assertEquals(6, BookQueries.topN(books, 100).size());
        assertThrows(IllegalArgumentException.class, () -> BookQueries.topN(books, -1));
    }

    @Test void optionalAndDuplicateKey() {
        assertEquals("Author A", BookQueries.findByTitle(books, "Alpha").orElseThrow().getAuthor());
        assertTrue(BookQueries.findByTitle(books, "missing").isEmpty());
    }

    @Test void starAndEmptyAndNonMutation() {
        List<Book> before = List.copyOf(books);
        assertEquals(1L, BookQueries.countByGenreAndYear(books).get(BookGenre.SCIENCE).get(2020));
        assertEquals(0, BookQueries.pageStatistics(List.of()).getCount());
        assertTrue(BookQueries.since2020(List.of()).isEmpty());
        assertTrue(BookQueries.titles(List.of()).isEmpty());
        assertTrue(BookQueries.countByGenre(List.of()).isEmpty());
        assertTrue(BookQueries.topN(List.of(), 5).isEmpty());
        assertTrue(BookQueries.findByTitle(List.of(), "none").isEmpty());
        assertTrue(BookQueries.countByGenreAndYear(List.of()).isEmpty());
        assertEquals(before, books);
    }
}
