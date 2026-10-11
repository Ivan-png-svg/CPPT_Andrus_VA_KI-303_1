
package ua.lpnu.kzp;

import java.util.Locale;

/**
 * Представляє книжку в каталозі.
 * Лабораторна робота №2, варіант 1.
 *
 * <p>Клас є незмінним: усі поля private final,
 * а відкриті сеттери відсутні.</p>
 */
public final class Book {

    private final String title;
    private final String author;
    private final int pages;
    private final double price;

    /**
     * Створює книжку з перевіркою коректності даних.
     *
     * @param title назва книжки
     * @param author автор книжки
     * @param pages кількість сторінок
     * @param price ціна книжки
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public Book(String title, String author, int pages, double price) {
        if (title == null || author == null
                || title.isBlank() || author.isBlank()) {
            throw new IllegalArgumentException(
                    "порожня назва книжки або автор"
            );
        }

        if (pages <= 0) {
            throw new IllegalArgumentException(
                    "кількість сторінок повинна бути більшою за 0"
            );
        }

        if (!Double.isFinite(price)) {
            throw new IllegalArgumentException(
                    "ціна повинна бути скінченним числом"
            );
        }

        if (price < 0) {
            throw new IllegalArgumentException(
                    "ціна не може бути від'ємною"
            );
        }

        this.title = title.trim();
        this.author = author.trim();
        this.pages = pages;
        this.price = price;
    }

    /**
     * Створює книжку з CSV-рядка.
     *
     * @param csvLine рядок формату title;author;pages;price
     * @return об'єкт Book
     * @throws IllegalArgumentException якщо запис некоректний
     */
    public static Book fromCsv(String csvLine) {
        if (csvLine == null || csvLine.isBlank()) {
            throw new IllegalArgumentException(
                    "порожній рядок"
            );
        }

        String[] fields = csvLine.split(";", -1);

        if (fields.length != 4) {
            throw new IllegalArgumentException(
                    "неправильна кількість полів"
            );
        }

        String title = fields[0].trim();
        String author = fields[1].trim();
        String pagesText = fields[2].trim();
        String priceText = fields[3].trim();

        if (title.isEmpty() || author.isEmpty()) {
            throw new IllegalArgumentException(
                    "порожня назва книжки або автор"
            );
        }

        try {
            int pages = Integer.parseInt(pagesText);
            double price = Double.parseDouble(priceText);

            return new Book(title, author, pages, price);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "неправильний числовий формат", e
            );
        }
    }

    /**
     * Повертає назву книжки.
     *
     * @return назва книжки
     */
    public String getTitle() {
        return title;
    }

    /**
     * Повертає автора книжки.
     *
     * @return автор книжки
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Повертає кількість сторінок.
     *
     * @return кількість сторінок
     */
    public int getPages() {
        return pages;
    }

    /**
     * Повертає ціну книжки.
     *
     * @return ціна книжки
     */
    public double getPrice() {
        return price;
    }

    /**
     * Повертає текстове представлення книжки.
     *
     * @return інформація про книжку
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "%s (%s): %d стор., %.2f грн",
                title,
                author,
                pages,
                price
        );
    }

    /**
     * Створює новий Builder для побудови книжки.
     *
     * @return новий Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder для покрокового створення незмінного Book.
     *
     * <p>Перевірка даних виконується конструктором Book
     * під час виклику build().</p>
     */
    public static final class Builder {

        private String title;
        private String author;
        private int pages;
        private double price;

        private Builder() {
            // Використовуйте Book.builder().
        }

        /**
         * Встановлює назву книжки.
         *
         * @param title назва книжки
         * @return поточний Builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Встановлює автора книжки.
         *
         * @param author автор книжки
         * @return поточний Builder
         */
        public Builder author(String author) {
            this.author = author;
            return this;
        }

        /**
         * Встановлює кількість сторінок.
         *
         * @param pages кількість сторінок
         * @return поточний Builder
         */
        public Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        /**
         * Встановлює ціну книжки.
         *
         * @param price ціна книжки
         * @return поточний Builder
         */
        public Builder price(double price) {
            this.price = price;
            return this;
        }

        /**
         * Створює незмінний об'єкт Book.
         *
         * @return нова книжка
         * @throws IllegalArgumentException якщо дані некоректні
         */
        public Book build() {
            return new Book(title, author, pages, price);
        }
    }
}
