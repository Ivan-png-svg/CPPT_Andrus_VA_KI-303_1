
package ua.lpnu.kzp;

import java.util.Objects;

/**
 * Електронна книжка в каталозі.
 *
 * <p>Має розмір файлу та власний спосіб
 * обчислення орієнтовного часу читання.</p>
 */
public final class ElectronicBook extends Book {

    private final double fileSizeMb;

    /**
     * Створює електронну книжку.
     *
     * @param title назва
     * @param author автор
     * @param pages кількість сторінок
     * @param price ціна
     * @param fileSizeMb розмір файлу в мегабайтах
     */
    public ElectronicBook(
            String title,
            String author,
            int pages,
            double price,
            double fileSizeMb
    ) {
        super(title, author, pages, price);

        if (!Double.isFinite(fileSizeMb) || fileSizeMb <= 0) {
            throw new IllegalArgumentException(
                    "розмір електронної книжки має бути додатним числом"
            );
        }

        this.fileSizeMb = fileSizeMb;
    }

    /**
     * @return розмір файлу в мегабайтах
     */
    public double getFileSizeMb() {
        return fileSizeMb;
    }

    /**
     * @return формат ELECTRONIC
     */
    @Override
    public BookFormat getFormat() {
        return BookFormat.ELECTRONIC;
    }

    /**
     * Розраховує час читання електронної книжки.
     * Умовна швидкість — 40 сторінок на годину.
     *
     * @return кількість годин
     */
    @Override
    public double calculateReadingHours() {
        return getPages() / 40.0;
    }

    /**
     * Порівнює електронні книжки за всіма полями.
     *
     * @param obj інший об'єкт
     * @return true, якщо книжки рівні
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        ElectronicBook other = (ElectronicBook) obj;
        return Double.compare(fileSizeMb, other.fileSizeMb) == 0;
    }

    /**
     * @return хеш-код електронної книжки
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileSizeMb);
    }
}
