package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Regression tests for the production refactoring and publication metadata. */
class RefactoringTest {
    @Test void commandLineParsing() {
        var opts = CommandLineOptions.parse(new String[]{"--input", "a.csv", "--output", "b.txt"});
        assertEquals(Path.of("a.csv"), opts.input());
        assertEquals(Path.of("b.txt"), opts.output());
        assertTrue(CommandLineOptions.parse(new String[]{"--help"}).help());
        assertThrows(IllegalArgumentException.class,
                () -> CommandLineOptions.parse(new String[]{"--input"}));
        assertThrows(IllegalArgumentException.class,
                () -> CommandLineOptions.parse(new String[]{"--bad"}));
    }
    @Test void subtypesAcceptPublicationMetadata() {
        Book printed = new PrintedBook("A", "B", 120, 10, true, 2020, BookGenre.HISTORY);
        Book electronic = new ElectronicBook("C", "D", 140, 20, 2.5, 2024, BookGenre.SCIENCE);
        assertEquals(2020, printed.getPublicationYear());
        assertEquals(BookGenre.HISTORY, printed.getGenre());
        assertEquals(2024, electronic.getPublicationYear());
        assertEquals(BookGenre.SCIENCE, electronic.getGenre());
        assertEquals(2, BookQueries.since2020(List.of(printed, electronic)).size());
    }
    @Test void productionQueriesUsePolymorphicBooks() {
        var books = List.<Book>of(
                new PrintedBook("Alpha", "X", 200, 15, false, 2020, BookGenre.FICTION),
                new ElectronicBook("Beta", "Y", 200, 12, 1.5, 2022, BookGenre.FICTION));
        var result = new CatalogApplication().query(books);
        assertEquals(List.of("Alpha", "Beta"), result.titles());
        assertEquals(2L, result.byGenre().get(BookGenre.FICTION));
        assertEquals(400L, result.pageStatistics().getSum());
        assertEquals("Alpha", result.topFive().get(0).getTitle());
        assertEquals("Alpha", result.found().orElseThrow().getTitle());
        assertTrue(new CatalogApplication().query(List.of()).found().isEmpty());
    }
    @Test void versionIncludesBuild() {
        assertFalse(VersionInfo.version().isBlank());
        assertFalse(VersionInfo.buildNumber().isBlank());
        assertTrue(VersionInfo.fullVersion().contains("build"));
    }
}
