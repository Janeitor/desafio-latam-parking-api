package cl.desafiolatam.parking.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ParkingStayTest {

        @Test
        void shouldCloseActiveParkingStay() {
                LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

                LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 12, 45);

                ParkingStay parkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                entryTime,
                                null);

                parkingStay.close(exitTime);

                assertEquals(exitTime, parkingStay.getExitTime());
        }

        @Test
        void shouldRejectExitTimeBeforeEntryTime() {
                LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

                LocalDateTime invalidExitTime = LocalDateTime.of(2026, 9, 2, 10, 29);

                ParkingStay parkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                entryTime,
                                null);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> parkingStay.close(invalidExitTime));
        }

        @Test
        void shouldRejectClosingAlreadyClosedParkingStay() {
                LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

                LocalDateTime firstExitTime = LocalDateTime.of(2026, 9, 2, 12, 45);

                LocalDateTime secondExitTime = LocalDateTime.of(2026, 9, 2, 13, 30);

                ParkingStay parkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                entryTime,
                                firstExitTime);

                assertThrows(
                                IllegalStateException.class,
                                () -> parkingStay.close(secondExitTime));
        }

        @Test
        void shouldRejectNullExitTime() {
                LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

                ParkingStay parkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                entryTime,
                                null);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> parkingStay.close(null));
        }

}