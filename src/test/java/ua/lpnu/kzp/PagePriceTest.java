
package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagePriceTest {

    @Test
    void testValidPagePrice() {
        PagePrice value = new PagePrice(320, 150.00);

        assertEquals(320, value.pages());
        assertEquals(150.00, value.price());
    }

    @Test
    void testZeroPrice() {
        PagePrice value = new PagePrice(100, 0.00);

        assertEquals(100, value.pages());
        assertEquals(0.00, value.price());
    }

    @Test
    void testInvalidPages() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PagePrice(0, 100.00)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new PagePrice(-10, 100.00)
        );
    }

    @Test
    void testNegativePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PagePrice(100, -1.00)
        );
    }

    @Test
    void testNonFinitePrices() {
        double[] invalidPrices = {
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double price : invalidPrices) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new PagePrice(100, price)
            );
        }
    }

    @Test
    void testEqualsAndHashCode() {
        PagePrice first = new PagePrice(320, 150.00);
        PagePrice second = new PagePrice(320, 150.00);
        PagePrice different = new PagePrice(400, 150.00);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void testToString() {
        PagePrice value = new PagePrice(320, 150.00);

        assertEquals(
                "PagePrice[pages=320, price=150.0]",
                value.toString()
        );
    }
}
