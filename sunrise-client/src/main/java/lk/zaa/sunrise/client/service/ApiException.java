package lk.zaa.sunrise.client.service;

/** Thrown by ApiClient when sunrise-api returns a non-2xx response; carries its message through to the UI. */
public class ApiException extends RuntimeException {
    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
