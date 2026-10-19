
package ua.lpnu.kzp;

/**
 * Формати книжок у каталозі.
 * Лабораторна робота №3, варіант 1.
 */
public enum BookFormat {

    /** Друкована книжка. */
    PRINTED("Друкована"),

    /** Електронна книжка. */
    ELECTRONIC("Електронна");

    private final String label;

    /**
     * Створює формат книжки з текстовою назвою.
     *
     * @param label назва формату
     */
    BookFormat(String label) {
        this.label = label;
    }

    /**
     * Повертає українську назву формату.
     *
     * @return назва формату
     */
    public String label() {
        return label;
    }
}
