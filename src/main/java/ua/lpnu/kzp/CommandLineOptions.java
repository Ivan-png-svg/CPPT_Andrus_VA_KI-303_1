package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.Objects;

/** Parses and validates command-line options independently of the application. */
public record CommandLineOptions(Path input, Path output, boolean help, boolean version) {
    /** Parses CLI arguments, preserving the original default paths and flags. */
    public static CommandLineOptions parse(String[] args) {
        Objects.requireNonNull(args, "args");
        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> { return new CommandLineOptions(input, output, true, false); }
                case "--version" -> { return new CommandLineOptions(input, output, false, true); }
                case "--input", "--output" -> {
                    String flag = args[i];
                    if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                        throw new IllegalArgumentException("після " + flag + " потрібно вказати шлях.");
                    }
                    Path path = Path.of(args[++i]);
                    if (flag.equals("--input")) input = path;
                    else output = path;
                }
                default -> throw new IllegalArgumentException("Невідомий аргумент: " + args[i]);
            }
        }
        return new CommandLineOptions(input, output, false, false);
    }

    /** Returns command-line usage. */
    public static String usage() {
        return "Використання: java -jar lab01.jar [--help] [--version] "
                + "[--input <файл>] [--output <файл>]";
    }
}
