package ua.lpnu.kzp;

import java.util.List;
import java.util.Objects;

/** Immutable library CSV record for variant 1. */
public final class BookRecord {
    @CsvColumn("назва") private final String title;
    @CsvColumn("автор") private final String author;
    @CsvColumn("рік") private final int year;
    @CsvColumn("сторінки") private final int pages;
    @CsvColumn("жанр") private final BookGenre genre;

    /** Creates a validated library record. */
    public BookRecord(String title, String author, int year, int pages, BookGenre genre) {
        if (title == null || title.isBlank() || author == null || author.isBlank())
            throw new IllegalArgumentException("Title and author must not be blank");
        if (year < 1 || year > 2100 || pages <= 0)
            throw new IllegalArgumentException("Invalid year or pages");
        this.title = title;
        this.author = author;
        this.year = year;
        this.pages = pages;
        this.genre = Objects.requireNonNull(genre, "genre");
    }
    /** Returns the book title. */ public String title() { return title; }
    /** Returns the author. */ public String author() { return author; }
    /** Returns the publication year. */ public int year() { return year; }
    /** Returns the page count. */ public int pages() { return pages; }
    /** Returns the genre. */ public BookGenre genre() { return genre; }
    /** Returns the variant's composite round-trip key. */
    public String key() { return title + "|" + author; }
    /** Restores a record in the documented alphabetical Java-field order. */
    public static BookRecord fromCsv(List<String> fields) throws DataStorageException {
        if (fields == null) throw new DataStorageException("CSV fields must not be null", new IllegalArgumentException("null fields"));
        if (fields.size() != 5) throw new DataStorageException("Expected 5 CSV fields, got " + fields.size());
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i) == null) {
                throw new DataStorageException("Null CSV field at index " + i, new IllegalArgumentException("null field"));
            }
        }
        try {
            return new BookRecord(fields.get(3), fields.get(0), Integer.parseInt(fields.get(4)),
                    Integer.parseInt(fields.get(2)), BookGenre.valueOf(fields.get(1)));
        } catch (IllegalArgumentException ex) {
            throw new DataStorageException("Invalid BookRecord CSV value", ex);
        }
    }
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BookRecord other)) return false;
        return year == other.year && pages == other.pages && title.equals(other.title)
                && author.equals(other.author) && genre == other.genre;
    }
    @Override public int hashCode() { return Objects.hash(title, author, year, pages, genre); }
    @Override public String toString() { return "BookRecord[" + key() + "]"; }
}
