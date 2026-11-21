package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.List;

/** Standalone Lab 5 demonstration; leaves the previous Main entry point untouched. */
public final class Lab5Demo {
    private Lab5Demo() { }
    /** Exports and imports five library records and checks complete equality. */
    public static void main(String[] args) throws DataStorageException {
        Repository<BookRecord> repository = new Repository<>();
        List<BookRecord> books = List.of(
            new BookRecord("Java, 21", "O. Author", 2023, 350, BookGenre.SCIENCE),
            new BookRecord("The \"Library\"", "A. Writer", 2010, 220, BookGenre.FICTION),
            new BookRecord("History\nof Books", "M. Scholar", 1999, 410, BookGenre.HISTORY),
            new BookRecord("Magic", "N. Story", 2005, 300, BookGenre.FANTASY),
            new BookRecord("Algorithms", "D. Knuth", 1973, 650, BookGenre.SCIENCE));
        books.forEach(repository::add);
        Path path = Path.of("target", "data", "books-lab5.csv");
        CsvExporter.write(path, BookRecord.class, repository.all());
        List<BookRecord> restored = BookRecordCsv.read(path);
        if (!repository.all().equals(restored)) throw new DataStorageException("Round-trip mismatch");
        System.out.println("CSV: " + path);
        System.out.println("Records: " + restored.size());
        System.out.println("Round-trip: PASS");
        System.out.println("Science records: " + repository.find(b -> b.genre() == BookGenre.SCIENCE).size());
    }
}
