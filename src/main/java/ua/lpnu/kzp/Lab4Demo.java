package ua.lpnu.kzp;

import java.util.List;
import java.util.Locale;

/** Reproducible laboratory 4 demonstration on a six-book polymorphic sample. */
public final class Lab4Demo {
    private Lab4Demo() { }

    /** Creates sample records with duplicates, filter boundary and sort ties. */
    public static List<Book> sample() {
        return List.of(
            new Book("Alpha", "Author A", 400, 120, 2020, BookGenre.SCIENCE),
            new Book("Beta", "Author B", 400, 140, 2021, BookGenre.FICTION),
            new Book("Gamma", "Author C", 550, 180, 2019, BookGenre.HISTORY),
            new Book("Delta", "Author D", 200, 90, 2024, BookGenre.SCIENCE),
            new Book("Alpha", "Author E", 150, 75, 2020, BookGenre.FICTION),
            new Book("Epsilon", "Author F", 100, 60, 2018, BookGenre.FANTASY));
    }

    /** Prints results of all queries including the star task. */
    public static void main(String[] args) {
        List<Book> books = sample();
        System.out.println("Version: " + VersionInfo.version());
        System.out.println("Since 2020: " + BookQueries.titles(BookQueries.since2020(books)));
        System.out.println("Titles: " + BookQueries.titles(books));
        System.out.println("By genre: " + BookQueries.countByGenre(books));
        var stats = BookQueries.pageStatistics(books);
        System.out.printf(Locale.ROOT, "Pages: count=%d sum=%d min=%d max=%d avg=%.2f%n",
                stats.getCount(), stats.getSum(), stats.getMin(), stats.getMax(), stats.getAverage());
        System.out.println("Top 5: " + BookQueries.titles(BookQueries.topN(books, 5)));
        System.out.println("Find Alpha: " + BookQueries.findByTitle(books, "Alpha").map(Book::getAuthor));
        System.out.println("Missing: " + BookQueries.findByTitle(books, "Missing"));
        System.out.println("Genre/year: " + BookQueries.countByGenreAndYear(books));
    }
}
