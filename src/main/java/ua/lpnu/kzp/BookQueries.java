package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Pure Stream API queries over a polymorphic book collection. */
public final class BookQueries {
    private BookQueries() { }

    /** Returns books published since 2020 (inclusive). */
    public static List<Book> since2020(List<Book> books) {
        return Objects.requireNonNull(books).stream()
                .filter(book -> book.getPublicationYear() >= 2020).toList();
    }

    /** Projects book titles, preserving duplicates and source order. */
    public static List<String> titles(List<Book> books) {
        return Objects.requireNonNull(books).stream().map(Book::getTitle).toList();
    }

    /** Counts books in each genre. */
    public static Map<BookGenre, Long> countByGenre(List<Book> books) {
        return Objects.requireNonNull(books).stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    /** Calculates count, min, max, sum and average of page counts. */
    public static IntSummaryStatistics pageStatistics(List<Book> books) {
        return Objects.requireNonNull(books).stream()
                .collect(Collectors.summarizingInt(Book::getPages));
    }

    /** Orders by pages descending, then title ascending; rejects negative N. */
    public static List<Book> topN(List<Book> books, int n) {
        if (n < 0) throw new IllegalArgumentException("N must be non-negative");
        Comparator<Book> order = Comparator.comparingInt(Book::getPages)
                .reversed().thenComparing(Book::getTitle);
        return Objects.requireNonNull(books).stream().sorted(order).limit(n).toList();
    }

    /** Returns the first book with the given title, or Optional.empty(). */
    public static Optional<Book> findByTitle(List<Book> books, String title) {
        Objects.requireNonNull(title);
        return Objects.requireNonNull(books).stream()
                .filter(book -> book.getTitle().equals(title)).findFirst();
    }

    /** Star task: two-level grouping by genre and publication year. */
    public static Map<BookGenre, Map<Integer, Long>> countByGenreAndYear(List<Book> books) {
        return Objects.requireNonNull(books).stream().collect(Collectors.groupingBy(
                Book::getGenre, Collectors.groupingBy(Book::getPublicationYear,
                        TreeMap::new, Collectors.counting())));
    }
}
