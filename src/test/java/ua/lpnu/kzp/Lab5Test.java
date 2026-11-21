package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Integration and edge-case tests for variant 1 CSV persistence. */
class Lab5Test {
    @TempDir Path dir;
    private BookRecord sample() { return new BookRecord("Book, \"One\"\nTwo", "Автор", 2020, 100, BookGenre.FICTION); }
    @Test void repositorySnapshotAndFind() {
        Repository<BookRecord> repo = new Repository<>(); repo.add(sample());
        List<BookRecord> snapshot = repo.all();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());
        repo.add(new BookRecord("Second", "Author", 2021, 150, BookGenre.SCIENCE));
        assertEquals(1, snapshot.size()); assertEquals(1, repo.find(b -> b.genre() == BookGenre.SCIENCE).size());
        assertThrows(NullPointerException.class, () -> repo.add(null));
    }
    @Test void annotationsAndHeader() throws Exception {
        assertEquals(List.of("автор", "жанр", "сторінки", "назва", "рік"),
            CsvExporter.annotatedFields(BookRecord.class).stream().map(f -> f.getAnnotation(CsvColumn.class).value()).toList());
        Path path = dir.resolve("header.csv"); CsvExporter.write(path, BookRecord.class, List.of());
        assertEquals("автор,жанр,сторінки,назва,рік\n", Files.readString(path, StandardCharsets.UTF_8));
    }
    @Test void multilineRoundTrip() throws Exception {
        Path path = dir.resolve("books.csv"); List<BookRecord> original = List.of(sample());
        CsvExporter.write(path, BookRecord.class, original);
        assertEquals(original, BookRecordCsv.read(path));
        assertTrue(Files.readString(path, StandardCharsets.UTF_8).contains("\"Book, \"\"One\"\"\nTwo\""));
    }
    @Test void emptyDatasetRoundTrip() throws Exception {
        Path path = dir.resolve("empty.csv"); CsvExporter.write(path, BookRecord.class, List.of());
        assertEquals(List.of(), BookRecordCsv.read(path));
    }
    @Test void malformedCsvAndCause() throws Exception {
        Path path = dir.resolve("bad.csv");
        Files.writeString(path, "автор,жанр,сторінки,назва,рік\nA,FICTION,100,B\n");
        assertNotNull(assertThrows(DataStorageException.class, () -> BookRecordCsv.read(path)).getCause());
        Files.writeString(path, "автор,жанр,сторінки,назва,рік\nA,FICTION,abc,B,2020\n");
        assertNotNull(assertThrows(DataStorageException.class, () -> BookRecordCsv.read(path)).getCause());
        Files.writeString(path, "автор,жанр,сторінки,назва,рік\nA,UNKNOWN,100,B,2020\n");
        assertNotNull(assertThrows(DataStorageException.class, () -> BookRecordCsv.read(path)).getCause());
        assertThrows(DataStorageException.class, () -> CsvParser.parseDocument("\"unclosed"));
    }
    @Test void fileFailurePreservesIOException() throws Exception {
        Path missing = dir.resolve("missing.csv");
        assertInstanceOf(IOException.class, assertThrows(DataStorageException.class, () -> BookRecordCsv.read(missing)).getCause());
        Path directory = dir.resolve("directory"); Files.createDirectories(directory);
        assertInstanceOf(IOException.class, assertThrows(DataStorageException.class,
                () -> CsvExporter.write(directory, BookRecord.class, List.of(sample()))).getCause());
    }
    @Test void genericExporterWorksForOtherClass() throws Exception {
        Path path = dir.resolve("other.csv");
        CsvExporter.write(path, Other.class, List.of(new Other("ok")));
        assertEquals("текст\nok\n", Files.readString(path));
        assertThrows(DataStorageException.class, () -> CsvExporter.write(dir.resolve("none.csv"), String.class, List.of("x")));
    }
    private static final class Other {
        @CsvColumn("текст") private final String value;
        private Other(String value) { this.value = value; }
    }
}
