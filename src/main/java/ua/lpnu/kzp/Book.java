
package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Базовий клас книжки в каталозі.
 * Лабораторна робота №3, варіант 1.
 *
 * <p>Містить спільні характеристики книжок.
 * Дозволяє спадкування лише визначеним підкласам.</p>
 */
public sealed class Book
        permits PrintedBook, ElectronicBook {

    private final String title;
    private final String author;
    private final int pages;
    private final double price;

    /**
     * Створює книжку.
     *
     * @param title назва книжки
     * @param author автор книжки
     * @param pages кількість сторінок
     * @param price ціна книжки
     */
    public Book(String title, String author, int pages, double price) {
        this(validate(title, author, pages, price));
    }

    /**
     * Приватний конструктор, який приймає перевірені дані.
     *
     * @param data перевірені характеристики книжки
     */
    private Book(ValidatedBookData data) {
        this.title = data.title();
        this.author = data.author();
        this.pages = data.pages();
        this.price = data.price();
    }

    /**
     * Перевіряє дані до створення об'єкта.
     *
     * @param title назва
     * @param author автор
     * @param pages сторінки
     * @param price ціна
     * @return перевірені дані
     */
    private static ValidatedBookData validate(
            String title,
            String author,
            int pages,
            double price
    ) {
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

        return new ValidatedBookData(
                title.trim(),
                author.trim(),
                pages,
                price
        );
    }

    /**
     * Допоміжний тип для зберігання перевірених даних.
     */
    private record ValidatedBookData(
            String title,
            String author,
            int pages,
            double price
    ) {
    }

    /**
     * Створює книжку з CSV-рядка.
     *
     * @param csvLine рядок CSV
     * @return книжка
     */
    public static Book fromCsv(String csvLine) {
        if (csvLine == null || csvLine.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
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
     * @return назва
     */
    public String getTitle() {
        return title;
    }

    /**
     * Повертає автора книжки.
     *
     * @return автор
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Повертає кількість сторінок.
     *
     * @return сторінки
     */
    public int getPages() {
        return pages;
    }

    /**
     * Повертає ціну книжки.
     *
     * @return ціна
     */
    public double getPrice() {
        return price;
    }

    /**
     * Повертає формат книжки.
     *
     * @return формат
     */
    public BookFormat getFormat() {
        return BookFormat.PRINTED;
    }

    /**
     * Обчислює час читання.
     *
     * @return час читання в годинах
     */
    public double calculateReadingHours() {
        return pages / 30.0;
    }

    /**
     * Повертає текстове представлення книжки.
     *
     * @return опис книжки
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
     * Порівнює книжки.
     *
     * @param obj інший об'єкт
     * @return true, якщо книжки рівні
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Book other = (Book) obj;

        return pages == other.pages
                && Double.compare(price, other.price) == 0
                && Objects.equals(title, other.title)
                && Objects.equals(author, other.author);
    }

    /**
     * Обчислює хеш-код книжки.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author, pages, price);
    }

    /**
     * Створює Builder.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder для створення книжки.
     */
    public static final class Builder {

        private String title;
        private String author;
        private int pages;
        private Double price;

        private Builder() {
        }

        /**
         * Встановлює назву.
         *
         * @param title назва
         * @return Builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Встановлює автора.
         *
         * @param author автор
         * @return Builder
         */
        public Builder author(String author) {
            this.author = author;
            return this;
        }

        /**
         * Встановлює кількість сторінок.
         *
         * @param pages сторінки
         * @return Builder
         */
        public Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        /**
         * Встановлює ціну.
         *
         * @param price ціна
         * @return Builder
         */
        public Builder price(double price) {
            this.price = price;
            return this;
        }

        /**
         * Створює книжку.
         *
         * @return нова книжка
         */
        public Book build() {
            if (price == null) {
                throw new IllegalArgumentException(
                        "потрібно вказати ціну книжки"
                );
            }

            return new Book(title, author, pages, price);
        }
    }
}
