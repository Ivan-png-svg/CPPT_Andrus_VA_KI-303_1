package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;

/** Thin entry point for the library catalog application. */
public final class Main {
    private Main() { }

    /** Processes the command-line options and starts the application. */
    public static void main(String[] args) {
        final CommandLineOptions options;
        try { options = CommandLineOptions.parse(args); }
        catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.out.println(CommandLineOptions.usage());
            return;
        }
        if (options.help()) { System.out.println(CommandLineOptions.usage()); return; }
        if (options.version()) { System.out.println(VersionInfo.fullVersion()); return; }
        try { System.out.print(new CatalogApplication().run(options.input(), options.output())); }
        catch (IOException ex) { System.err.println("Помилка роботи з файлом: " + ex.getMessage()); }
    }

    /** Compatibility method retained for existing tests and clients. */
    static String processCatalog(Path input) throws IOException {
        return new CatalogApplication().processCatalog(input);
    }
}
