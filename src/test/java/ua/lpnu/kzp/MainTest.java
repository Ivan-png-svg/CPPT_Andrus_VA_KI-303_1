package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testHelpOption() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(new String[]{"--help"});
        assertTrue(outContent.toString().contains("Використання: java -jar lab01.jar"));
    }

    @Test
    void testVersionOption() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(new String[]{"--version"});
        assertTrue(outContent.toString().contains("LAB01 - Варіант 1"));
    }

    @Test
    void testProcessCatalogValidAndInvalidData(@TempDir Path tempDir) throws Exception {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("report.txt");

        String csvContent = """
            Кобзар;Тарас Шевченко;350;450.00
            Тіні забутих предків;Михайло Коцюбинський;180;220.50
            ;Невідомий;100;100.00
            Помилка;Автор;abc;150.00
            Від'ємна;Автор;200;-50.00
            """;
        Files.writeString(input, csvContent, StandardCharsets.UTF_8);

        Main.processCatalog(input, output);

        assertTrue(Files.exists(output));
        String report = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(report.contains("Правильних записів: 2"));
        assertTrue(report.contains("Неправильних записів: 3"));
        assertTrue(report.contains("Середня кількість сторінок: 265.00"));
        assertTrue(report.contains("Найдорожча книга: Кобзар (Тарас Шевченко) (450.00 грн)"));
        assertTrue(report.contains("Сумарна вартість каталогу: 670.50 грн"));
    }
}