package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Produces the unchanged legacy report using Stream API calculations. */
public final class CatalogReport {
    /** Formats a report for parsed books and validation errors. */
    public String format(CatalogReader.Result result) {
        List<Book> books = result.books();
        long totalPages = books.stream().mapToLong(Book::getPages).sum();
        double totalValue = books.stream().mapToDouble(Book::getPrice).sum();
        Optional<Book> mostExpensive = books.stream()
                .max(Comparator.comparingDouble(Book::getPrice));
        double average = books.isEmpty() ? 0 : (double) totalPages / books.size();
        StringBuilder report = new StringBuilder("===== ЗВІТ КАТАЛОГУ КНИЖОК =====")
                .append(System.lineSeparator());
        report.append(String.format(Locale.ROOT, "Коректних записів: %d%n", books.size()));
        report.append(String.format(Locale.ROOT, "Некоректних записів: %d%n", result.errors().size()));
        report.append(String.format(Locale.ROOT, "Середня кількість сторінок: %.2f%n", average));
        report.append(mostExpensive.map(book -> String.format(Locale.ROOT,
                "Найдорожча книга: %s (%s) (%.2f грн)%n",
                book.getTitle(), book.getAuthor(), book.getPrice()))
                .orElse("Найдорожча книга: немає" + System.lineSeparator()));
        report.append(String.format(Locale.ROOT, "Сумарна вартість каталогу: %.2f грн%n", totalValue));
        if (!result.errors().isEmpty()) {
            report.append(System.lineSeparator()).append("Помилки:").append(System.lineSeparator());
            report.append(result.errors().stream().map(error -> " - " + error + System.lineSeparator())
                    .collect(Collectors.joining()));
        }
        return report.toString();
    }
}
