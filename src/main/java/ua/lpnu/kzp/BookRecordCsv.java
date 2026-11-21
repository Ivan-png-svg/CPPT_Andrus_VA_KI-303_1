package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Domain-specific importer paired with the generic exporter. */
public final class BookRecordCsv {
    private BookRecordCsv() { }
    /** Reads UTF-8 CSV and validates the annotated header and every record. */
    public static List<BookRecord> read(Path path) throws DataStorageException {
        final String csv;
        try { csv = Files.readString(path, StandardCharsets.UTF_8); }
        catch (IOException ex) { throw new DataStorageException("Cannot read CSV: " + path, ex); }
        List<List<String>> rows = CsvParser.parseDocument(csv);
        if (rows.isEmpty()) throw new DataStorageException("CSV has no header");
        List<String> expected = CsvExporter.annotatedFields(BookRecord.class).stream()
                .map(f -> f.getAnnotation(CsvColumn.class).value()).toList();
        if (!rows.get(0).equals(expected)) throw new DataStorageException("Invalid CSV header: " + rows.get(0));
        List<BookRecord> result = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            try { result.add(BookRecord.fromCsv(rows.get(i))); }
            catch (DataStorageException ex) { throw new DataStorageException("Invalid CSV record " + (i + 1), ex); }
        }
        return List.copyOf(result);
    }
}
