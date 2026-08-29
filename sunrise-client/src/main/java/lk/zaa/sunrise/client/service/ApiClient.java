package lk.zaa.sunrise.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lk.zaa.sunrise.client.util.Session;
import lk.zaa.sunrise.common.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * The client's single point of contact with sunrise-api over HTTP/JSON — this
 * is what makes the system a distributed application (Task B(i)): the JavaFX
 * client never talks to MySQL directly, only ever to this REST boundary.
 */
public class ApiClient {

    // Change to the API host/port used at deployment (e.g. via a config file)
    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // ---- auth ---------------------------------------------------------

    public LoginResponse login(String username, String password) throws IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);
        HttpResponse<String> response = post("/auth/login", request, false);
        return parse(response, LoginResponse.class);
    }

    // ---- appointments ---------------------------------------------------

    public AppointmentResponse registerAppointment(AppointmentRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = post("/appointments", request, true);
        return parse(response, AppointmentResponse.class);
    }

    public AppointmentResponse searchAppointment(String appointmentNumber) throws IOException, InterruptedException {
        HttpResponse<String> response = get("/appointments/" + appointmentNumber);
        return parse(response, AppointmentResponse.class);
    }

    // ---- bills ------------------------------------------------------------

    public BillResponse generateBill(String appointmentNumber) throws IOException, InterruptedException {
        HttpResponse<String> response = get("/bills/" + appointmentNumber);
        return parse(response, BillResponse.class);
    }

    // ---- reference data -----------------------------------------------

    public List<DentistDto> listDentists() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/dentists");
        return parseList(response, DentistDto[].class);
    }

    public List<TreatmentTypeDto> listTreatments() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/treatments");
        return parseList(response, TreatmentTypeDto[].class);
    }

    // ---- admin: manage staff accounts ----------------------------------

    public UserDto createUser(NewUserRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = post("/admin/users", request, true);
        return parse(response, UserDto.class);
    }

    public List<UserDto> listUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/admin/users");
        return parseList(response, UserDto[].class);
    }

    // ---- plumbing -------------------------------------------------------

    private HttpRequest.Builder baseRequest(String path, boolean authenticated) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        if (authenticated && Session.getInstance().isLoggedIn()) {
            builder.header("Authorization", "Bearer " + Session.getInstance().getToken());
        }
        return builder;
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = baseRequest(path, true).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, Object body, boolean authenticated) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = baseRequest(path, authenticated)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T parse(HttpResponse<String> response, Class<T> type) throws IOException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readValue(response.body(), type);
        }
        throw toApiException(response);
    }

    private <T> List<T> parseList(HttpResponse<String> response, Class<T[]> arrayType) throws IOException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return List.of(mapper.readValue(response.body(), arrayType));
        }
        throw toApiException(response);
    }

    private ApiException toApiException(HttpResponse<String> response) {
        try {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            String details = error.getDetails() == null || error.getDetails().isEmpty()
                    ? "" : " (" + String.join("; ", error.getDetails()) + ")";
            return new ApiException(response.statusCode(), error.getMessage() + details);
        } catch (Exception parseFailure) {
            return new ApiException(response.statusCode(), "Request failed with status " + response.statusCode());
        }
    }
}
