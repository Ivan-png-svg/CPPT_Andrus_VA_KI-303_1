
package ua.lpnu.kzp;

import java.util.Objects;

/**
 * Друкована книжка в каталозі.
 *
 * <p>Має тип обкладинки та власну реалізацію
 * розрахунку часу читання.</p>
 */
public final class PrintedBook extends Book {

    private final boolean hardcover;

    /**
     * Створює друковану книжку.
     *
     * @param title назва
     * @param author автор
     * @param pages кількість сторінок
     * @param price ціна
     * @param hardcover true, якщо обкладинка тверда
     */
    public PrintedBook(
            String title,
            String author,
            int pages,
            double price,
            boolean hardcover
    ) {
        super(title, author, pages, price);
        this.hardcover = hardcover;
    }

    /**
     * @return true, якщо книжка має тверду обкладинку
     */
    public boolean isHardcover() {
        return hardcover;
    }

    /**
     * @return формат PRINTED
     */
    @Override
    public BookFormat getFormat() {
        return BookFormat.PRINTED;
    }

    /**
     * Розраховує час читання друкованої книжки.
     * Швидкість — 30 сторінок на годину.
     *
     * @return кількість годин
     */
    @Override
    public double calculateReadingHours() {
        return getPages() / 30.0;
    }

    /**
     * Порівнює друковані книжки за всіма полями.
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

        PrintedBook other = (PrintedBook) obj;
        return hardcover == other.hardcover;
    }

    /**
     * @return хеш-код друкованої книжки
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hardcover);
    }
}
