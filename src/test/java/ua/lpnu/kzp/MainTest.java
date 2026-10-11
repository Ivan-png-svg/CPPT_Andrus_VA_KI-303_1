
package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void testValidCatalog() throws IOException {
        Path input = Files.createTempFile("books", ".csv");

        String data = String.join(
                System.lineSeparator(),
                "Кобзар;Тарас Шевченко;720;450.00",
                "Лісова пісня;Леся Українка;180;320.00",
                "Місто;Валер'ян Підмогильний;300;280.00"
        );

        Files.writeString(input, data, StandardCharsets.UTF_8);

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 3"));
            assertTrue(report.contains(
                    "Середня кількість сторінок: 400.00"
            ));
            assertTrue(report.contains(
                    "Найдорожча книга: Кобзар "
                            + "(Тарас Шевченко) (450.00 грн)"
            ));
            assertTrue(report.contains(
                    "Сумарна вартість каталогу: 1050.00 грн"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    @Test
    void testInvalidRecords() throws IOException {
        Path input = Files.createTempFile("books-invalid", ".csv");

        String data = String.join(
                System.lineSeparator(),
                "Кобзар;Тарас Шевченко;720;450.00",
                ";Автор;200;150.00",
                "Чорна рада;Пантелеймон Куліш;abc;210.00",
                "Книга;Автор;100;-50.00",
                "Неправильна;кількість;полів"
        );

        Files.writeString(input, data, StandardCharsets.UTF_8);

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 1"));
            assertTrue(report.contains("Некоректних записів: 4"));
            assertTrue(report.contains("Рядок 2:"));
            assertTrue(report.contains("Рядок 3:"));
            assertTrue(report.contains("Рядок 4:"));
            assertTrue(report.contains("Рядок 5:"));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    @Test
    void testEmptyFile() throws IOException {
        Path input = Files.createTempFile("books-empty", ".csv");

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 0"));
            assertTrue(report.contains(
                    "Середня кількість сторінок: 0.00"
            ));
            assertTrue(report.contains("Найдорожча книга: немає"));
            assertTrue(report.contains(
                    "Сумарна вартість каталогу: 0.00 грн"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    @Test
    void testEmptyLine() throws IOException {
        Path input = Files.createTempFile("books-empty-line", ".csv");

        Files.writeString(
                input,
                System.lineSeparator(),
                StandardCharsets.UTF_8
        );

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains(
                    "Рядок 1: порожній рядок"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    @Test
    void testUkrainianCharacters() throws IOException {
        Path input = Files.createTempFile("books-ua", ".csv");

        Files.writeString(
                input,
                "Лісова пісня;Леся Українка;180;320.00",
                StandardCharsets.UTF_8
        );

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Лісова пісня"));
            assertTrue(report.contains("Леся Українка"));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    /**
     * Перевіряє виправлення першого зауваження:
     * NaN та нескінченні ціни мають відхилятися.
     */
    @Test
    void testNonFinitePrices() throws IOException {
        Path input = Files.createTempFile("books-nonfinite", ".csv");

        String data = String.join(
                System.lineSeparator(),
                "Книга A;Автор A;100;NaN",
                "Книга B;Автор B;200;Infinity",
                "Книга C;Автор C;300;-Infinity",
                "Книга D;Автор D;400;150.00"
        );

        Files.writeString(input, data, StandardCharsets.UTF_8);

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 1"));
            assertTrue(report.contains("Некоректних записів: 3"));
            assertTrue(report.contains(
                    "Середня кількість сторінок: 400.00"
            ));
            assertTrue(report.contains(
                    "Сумарна вартість каталогу: 150.00 грн"
            ));

            assertTrue(report.contains(
                    "Рядок 1: ціна повинна бути скінченним числом"
            ));
            assertTrue(report.contains(
                    "Рядок 2: ціна повинна бути скінченним числом"
            ));
            assertTrue(report.contains(
                    "Рядок 3: ціна повинна бути скінченним числом"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    /**
     * Перевіряє виправлення другого зауваження:
     * сума сторінок перевищує діапазон int.
     */
    @Test
    void testPagesOverflow() throws IOException {
        Path input = Files.createTempFile("books-overflow", ".csv");

        String data = String.join(
                System.lineSeparator(),
                "Книга A;Автор A;2000000000;100.00",
                "Книга B;Автор B;2000000000;200.00"
        );

        Files.writeString(input, data, StandardCharsets.UTF_8);

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 2"));
            assertTrue(report.contains("Некоректних записів: 0"));
            assertTrue(report.contains(
                    "Середня кількість сторінок: 2000000000.00"
            ));
            assertTrue(report.contains(
                    "Сумарна вартість каталогу: 300.00 грн"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }

    /**
     * Перевіряє, що ціна 0.00 є допустимою.
     */
    @Test
    void testZeroPrice() throws IOException {
        Path input = Files.createTempFile("books-zero-price", ".csv");

        Files.writeString(
                input,
                "Безкоштовна книга;Автор;120;0.00",
                StandardCharsets.UTF_8
        );

        try {
            String report = Main.processCatalog(input);

            assertTrue(report.contains("Коректних записів: 1"));
            assertTrue(report.contains("Некоректних записів: 0"));
            assertTrue(report.contains(
                    "Сумарна вартість каталогу: 0.00 грн"
            ));
        } finally {
            Files.deleteIfExists(input);
        }
    }
}
