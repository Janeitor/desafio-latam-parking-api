package cl.desafiolatam.parking.infrastructure.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import cl.desafiolatam.parking.application.service.ParkingStayService;
import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.domain.exception.ParkingStayNotFoundException;
import cl.desafiolatam.parking.domain.exception.InvalidParkingStayExitTimeException;
import cl.desafiolatam.parking.domain.exception.ParkingStayAlreadyClosedException;
import org.springframework.http.HttpHeaders;

@WebMvcTest(ParkingStayController.class)
class ParkingStayControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ParkingStayService service;

  @Test
  void shouldReturnEmptyParkingStayCollection() throws Exception {
    // Arrange
    String endpoint = "/api/v1/parking-stays";

    // Act
    ResultActions result = mockMvc.perform(get(endpoint));

    // Assert
    result.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  void shouldReturnNotFoundWhenActiveParkingStayNotExists() throws Exception {
    // Arrange
    String endpoint = "/api/v1/parking-stays/active/ABCD12";

    when(service.findActiveByLicensePlate("ABCD12"))
        .thenThrow(
            new ActiveParkingStayNotFoundException());
    // Act
    ResultActions result = mockMvc.perform(get(endpoint));

    // Assert
    result.andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message")
            .value("Active parking stay not found for the given vehicle."))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldRegisterParkingEntryAndReturnCreated() throws Exception {
    UUID id = UUID.fromString(
        "2ae869c1-f6dd-45f0-a22d-74468c63ecb6");

    LocalDateTime entryTime = LocalDateTime.of(2026, 8, 26, 18, 30);

    ParkingStay parkingStay = new ParkingStay(
        id,
        "ABCD12",
        entryTime,
        null);

    when(service.registerEntry("ABCD12", entryTime))
        .thenReturn(parkingStay);

    mockMvc.perform(post("/api/v1/parking-stays")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "licensePlate": "ABCD12",
              "entryTime": "2026-08-26T18:30:00"
            }
            """))
        .andExpect(status().isCreated())
        .andExpect(header().string(
            "Location",
            "http://localhost/api/v1/parking-stays/"
                + id))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.licensePlate")
            .value("ABCD12"))
        .andExpect(jsonPath("$.entryTime")
            .value("2026-08-26T18:30:00"))
        .andExpect(jsonPath("$.exitTime").isEmpty());
  }

  @Test
  void shouldReturnBadRequestWhenRequestFieldsAreInvalid()
      throws Exception {
    mockMvc.perform(post("/api/v1/parking-stays")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "licensePlate": "",
              "entryTime": null
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnBadRequestWhenJsonIsMalformed()
      throws Exception {
    mockMvc.perform(post("/api/v1/parking-stays")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "licensePlate": "ABCD12",
              "entryTime":
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message")
            .value("Malformed JSON request"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnParkingStayById() throws Exception {
    UUID id = UUID.fromString(
        "03227f66-76dc-4e71-be12-78ca6e869afd");

    LocalDateTime entryTime = LocalDateTime.of(2026, 8, 26, 22, 50);

    ParkingStay parkingStay = new ParkingStay(
        id,
        "ABCD12",
        entryTime,
        null);

    when(service.findById(id))
        .thenReturn(parkingStay);

    mockMvc.perform(get(
        "/api/v1/parking-stays/{id}",
        id))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id")
            .value(id.toString()))
        .andExpect(jsonPath("$.licensePlate")
            .value("ABCD12"))
        .andExpect(jsonPath("$.entryTime")
            .value("2026-08-26T22:50:00"))
        .andExpect(jsonPath("$.exitTime").isEmpty());
  }

  @Test
  void shouldReturnNotFoundWhenParkingStayIdDoesNotExist()
      throws Exception {
    UUID id = UUID.fromString(
        "11111111-1111-1111-1111-111111111111");

    when(service.findById(id))
        .thenThrow(
            new ParkingStayNotFoundException(id));

    mockMvc.perform(get(
        "/api/v1/parking-stays/{id}",
        id))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message")
            .value(
                "Parking stay not found with id: "
                    + id))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldCheckoutParkingStayAndReturnOk()
      throws Exception {
    UUID id = UUID.fromString(
        "03227f66-76dc-4e71-be12-78ca6e869afd");

    LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

    LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 12, 45);

    ParkingStay closedParkingStay = new ParkingStay(
        id,
        "ABCD12",
        entryTime,
        exitTime);

    when(service.checkout(id, exitTime))
        .thenReturn(closedParkingStay);

    mockMvc.perform(patch(
        "/api/v1/parking-stays/{id}/checkout",
        id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "exitTime": "2026-09-02T12:45:00"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id")
            .value(id.toString()))
        .andExpect(jsonPath("$.licensePlate")
            .value("ABCD12"))
        .andExpect(jsonPath("$.entryTime")
            .value("2026-09-02T10:30:00"))
        .andExpect(jsonPath("$.exitTime")
            .value("2026-09-02T12:45:00"));
  }

  @Test
  void shouldReturnBadRequestWhenCheckoutExitTimeIsNull()
      throws Exception {
    UUID id = UUID.fromString(
        "03227f66-76dc-4e71-be12-78ca6e869afd");

    mockMvc.perform(patch(
        "/api/v1/parking-stays/{id}/checkout",
        id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "exitTime": null
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message")
            .value("Exit time is required"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnNotFoundWhenCheckoutParkingStayDoesNotExist()
      throws Exception {
    UUID id = UUID.fromString(
        "11111111-1111-1111-1111-111111111111");

    LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 12, 45);

    when(service.checkout(id, exitTime))
        .thenThrow(new ParkingStayNotFoundException(id));

    mockMvc.perform(patch(
        "/api/v1/parking-stays/{id}/checkout",
        id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "exitTime": "2026-09-02T12:45:00"
            }
            """))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message")
            .value("Parking stay not found with id: " + id))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnUnprocessableEntityWhenExitTimeIsBeforeEntryTime()
      throws Exception {
    UUID id = UUID.fromString(
        "03227f66-76dc-4e71-be12-78ca6e869afd");

    LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 9, 30);

    when(service.checkout(id, exitTime))
        .thenThrow(
            new InvalidParkingStayExitTimeException(
                "Exit time cannot be before entry time"));

    mockMvc.perform(patch(
        "/api/v1/parking-stays/{id}/checkout",
        id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "exitTime": "2026-09-02T09:30:00"
            }
            """))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(422))
        .andExpect(jsonPath("$.message")
            .value("Exit time cannot be before entry time"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnConflictWhenParkingStayIsAlreadyClosed()
      throws Exception {
    UUID id = UUID.fromString(
        "03227f66-76dc-4e71-be12-78ca6e869afd");

    LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 13, 30);

    when(service.checkout(id, exitTime))
        .thenThrow(
            new ParkingStayAlreadyClosedException());

    mockMvc.perform(patch(
        "/api/v1/parking-stays/{id}/checkout",
        id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "exitTime": "2026-09-02T13:30:00"
            }
            """))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(409))
        .andExpect(jsonPath("$.message")
            .value("Parking stay is already closed"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldAllowRequestsFromViteDevelopmentOrigin()
      throws Exception {
    mockMvc.perform(get("/api/v1/parking-stays")
        .header(
            HttpHeaders.ORIGIN,
            "http://localhost:5173"))
        .andExpect(status().isOk())
        .andExpect(header().string(
            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            "http://localhost:5173"));
  }

  @Test
  void shouldAllowPostPreflightFromViteDevelopmentOrigin()
      throws Exception {
    mockMvc.perform(options("/api/v1/parking-stays")
        .header(
            HttpHeaders.ORIGIN,
            "http://localhost:5173")
        .header(
            HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
            "POST"))
        .andExpect(status().isOk())
        .andExpect(header().string(
            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            "http://localhost:5173"))
        .andExpect(header().string(
            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
            containsString("POST")));
  }
}