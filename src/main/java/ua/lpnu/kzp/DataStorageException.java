package ua.lpnu.kzp;

/** Checked exception for CSV persistence and parsing failures. */
public class DataStorageException extends Exception {
    /** Creates an exception with a descriptive message. */
    public DataStorageException(String message) { super(message); }
    /** Creates an exception retaining its original cause. */
    public DataStorageException(String message, Throwable cause) { super(message, cause); }
}
