
package ua.lpnu.kzp;

/**
 * Незмінна структура для зберігання кількості
 * сторінок книжки та її ціни.
 *
 * <p>Лабораторна робота №2, варіант 1.</p>
 *
 * @param pages кількість сторінок книжки
 * @param price ціна книжки у гривнях
 */
public record PagePrice(int pages, double price) {

    /**
     * Компактний конструктор із перевіркою даних.
     *
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public PagePrice {
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
    }
}
