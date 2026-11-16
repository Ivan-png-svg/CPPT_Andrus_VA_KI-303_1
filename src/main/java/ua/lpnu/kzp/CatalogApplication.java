package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.IntSummaryStatistics;
import java.util.Optional;

/** Coordinates the production catalog workflow and its Stream API queries. */
public final class CatalogApplication {
    private final CatalogReader reader = new CatalogReader();
    private final CatalogReport report = new CatalogReport();

    /** Reads the catalog and returns the original report format. */
    public String processCatalog(Path input) throws IOException {
        return report.format(reader.read(input));
    }

    /** Immutable results of the six required Stream API queries. */
    public record QueryResults(List<Book> recent, List<String> titles,
            Map<BookGenre, Long> byGenre, IntSummaryStatistics pageStatistics,
            List<Book> topFive, Optional<Book> found) {
        /** Copies mutable inputs to prevent exposure of internal state. */
        public QueryResults {
            recent = List.copyOf(recent);
            titles = List.copyOf(titles);
            byGenre = Map.copyOf(byGenre);
            pageStatistics = copyStatistics(pageStatistics);
            topFive = List.copyOf(topFive);
            java.util.Objects.requireNonNull(found, "found");
        }

        /** Returns an independent copy of mutable page statistics. */
        @Override
        public IntSummaryStatistics pageStatistics() {
            return copyStatistics(pageStatistics);
        }

        private static IntSummaryStatistics copyStatistics(IntSummaryStatistics source) {
            java.util.Objects.requireNonNull(source, "pageStatistics");
            if (source.getCount() == 0) return new IntSummaryStatistics();
            return new IntSummaryStatistics(source.getCount(), source.getMin(),
                    source.getMax(), source.getSum());
        }
    }

    /** Runs all six queries against the actual polymorphic catalog. */
    public QueryResults query(List<Book> books) {
        List<String> titles = BookQueries.titles(books);
        return new QueryResults(BookQueries.since2020(books), titles,
                BookQueries.countByGenre(books), BookQueries.pageStatistics(books),
                BookQueries.topN(books, 5), titles.isEmpty() ? Optional.empty()
                        : BookQueries.findByTitle(books, titles.get(0)));
    }

    /** Runs the real catalog pipeline and writes its report. */
    public String run(Path input, Path output) throws IOException {
        CatalogReader.Result data = reader.read(input);
        query(data.books());
        String outputText = report.format(data);
        Path parent = output.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(output, outputText, StandardCharsets.UTF_8);
        return outputText;
    }
}
