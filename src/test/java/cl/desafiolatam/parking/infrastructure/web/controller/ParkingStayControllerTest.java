package cl.desafiolatam.parking.infrastructure.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import cl.desafiolatam.parking.infrastructure.web.controller.ParkingStayController;

@WebMvcTest(ParkingStayController.class)
class ParkingStayControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        // Act
        ResultActions result = mockMvc.perform(get(endpoint));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Active parking stay not found for the given vehicle."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}