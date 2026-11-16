package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads legacy CSV records and retains line-numbered validation errors. */
public final class CatalogReader {
    /** Result of reading and validating the catalog. */
    public record Result(List<Book> books, List<String> errors) {
        /** Copies the input collections to protect the parsed result. */
        public Result { books = List.copyOf(books); errors = List.copyOf(errors); }
    }

    /** Reads a UTF-8 CSV file without changing the legacy four-column format. */
    public Result read(Path input) throws IOException {
        List<Book> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            try { books.add(Book.fromCsv(lines.get(i))); }
            catch (IllegalArgumentException ex) {
                errors.add("Рядок " + (i + 1) + ": " + ex.getMessage());
            }
        }
        return new Result(books, errors);
    }
}
