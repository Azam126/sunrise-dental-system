package lk.zaa.sunrise.api.exception;

/**
 * Thrown when a report depends on the optional SQL function/procedure in
 * db-extras.sql and that script has not been applied yet. See README.md.
 */
public class ReportUnavailableException extends RuntimeException {
    public ReportUnavailableException(String message) {
        super(message);
    }
}
