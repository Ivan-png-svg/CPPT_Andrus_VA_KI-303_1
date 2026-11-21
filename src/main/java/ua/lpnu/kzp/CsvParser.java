package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;

/** Stateful RFC-style parser supporting quoted commas, quotes and multiline values. */
public final class CsvParser {
    private CsvParser() { }
    /** Parses an entire CSV document; empty input produces no rows. */
    public static List<List<String>> parseDocument(String csv) throws DataStorageException {
        if (csv == null) throw new DataStorageException("CSV input is null");
        if (csv.isEmpty()) return List.of();
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false, closed = false, started = false;
        for (int i = 0; i < csv.length(); i++) {
            char c = csv.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < csv.length() && csv.charAt(i + 1) == '"') { cell.append('"'); i++; }
                    else { quoted = false; closed = true; }
                } else cell.append(c);
            } else if (c == '"') {
                if (closed || started || cell.length() != 0) throw new DataStorageException("Unexpected CSV quote");
                quoted = true; started = true;
            } else if (c == ',') {
                row.add(cell.toString()); cell.setLength(0); closed = false; started = false;
            } else if (c == '\r' || c == '\n') {
                row.add(cell.toString()); rows.add(List.copyOf(row)); row = new ArrayList<>();
                cell.setLength(0); closed = false; started = false;
                if (c == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') i++;
            } else {
                if (closed) throw new DataStorageException("Characters after closing quote");
                cell.append(c); started = true;
            }
        }
        if (quoted) throw new DataStorageException("Unclosed CSV quote");
        if (started || closed || !row.isEmpty()) {
            row.add(cell.toString()); rows.add(List.copyOf(row));
        }
        return List.copyOf(rows);
    }
}
