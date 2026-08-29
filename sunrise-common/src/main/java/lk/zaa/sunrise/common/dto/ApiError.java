package lk.zaa.sunrise.common.dto;

import java.time.Instant;
import java.util.List;

/** Uniform error payload returned by the GlobalExceptionHandler. */
public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String message;
    private List<String> details;

    public ApiError() {
    }

    public ApiError(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
