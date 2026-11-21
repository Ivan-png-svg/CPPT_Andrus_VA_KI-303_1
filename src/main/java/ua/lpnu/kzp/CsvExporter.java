package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Generic reflection-based CSV exporter independent of domain record types. */
public final class CsvExporter {
    private CsvExporter() { }
    /** Returns annotated fields sorted by their Java field names. */
    public static List<Field> annotatedFields(Class<?> type) throws DataStorageException {
        Objects.requireNonNull(type);
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(CsvColumn.class) && !Modifier.isStatic(field.getModifiers())) {
                    try {
                        if (!field.trySetAccessible()) throw new DataStorageException("Inaccessible field: " + field.getName());
                    } catch (SecurityException ex) { throw new DataStorageException("Cannot access field: " + field.getName(), ex); }
                    fields.add(field);
                }
            }
        }
        fields.sort(Comparator.comparing(Field::getName));
        if (fields.isEmpty()) throw new DataStorageException("No @CsvColumn fields in " + type.getName());
        return List.copyOf(fields);
    }
    /** Escapes one CSV cell. */
    public static String escape(Object value) {
        String s = Objects.toString(value, "");
        return s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\r') >= 0 || s.indexOf('\n') >= 0
                ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
    /** Writes the annotated fields of arbitrary records as UTF-8 CSV. */
    public static <T> void write(Path path, Class<T> type, List<? extends T> records) throws DataStorageException {
        Objects.requireNonNull(path); Objects.requireNonNull(records);
        List<Field> fields = annotatedFields(type);
        StringBuilder csv = new StringBuilder();
        csv.append(fields.stream().map(f -> escape(f.getAnnotation(CsvColumn.class).value()))
                .collect(Collectors.joining(","))).append('\n');
        for (T record : records) {
            if (record == null) throw new DataStorageException("Null CSV record");
            if (!type.isInstance(record)) throw new DataStorageException("Unexpected record type");
            List<String> values = new ArrayList<>();
            for (Field field : fields) {
                try { values.add(escape(field.get(record))); }
                catch (IllegalAccessException | IllegalArgumentException ex) {
                    throw new DataStorageException("Cannot read field " + field.getName(), ex);
                }
            }
            csv.append(String.join(",", values)).append('\n');
        }
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, csv, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ex) { throw new DataStorageException("Cannot write CSV: " + path, ex); }
    }
}
