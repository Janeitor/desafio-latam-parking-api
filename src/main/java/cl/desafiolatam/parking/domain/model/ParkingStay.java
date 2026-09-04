package cl.desafiolatam.parking.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import cl.desafiolatam.parking.domain.exception.InvalidParkingStayExitTimeException;
import cl.desafiolatam.parking.domain.exception.ParkingStayAlreadyClosedException;

public class ParkingStay {

    private final UUID id;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingStay(
            UUID id,
            String licensePlate,
            LocalDateTime entryTime,
            LocalDateTime exitTime) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    public static ParkingStay register(
            String licensePlate,
            LocalDateTime entryTime) {
        return new ParkingStay(
                UUID.randomUUID(),
                licensePlate,
                entryTime,
                null);
    }

    public void close(LocalDateTime exitTime) {
        if (this.exitTime != null) {
            throw new ParkingStayAlreadyClosedException();
        }

        if (exitTime == null) {
            throw new InvalidParkingStayExitTimeException(
                    "Exit time is required");
        }

        if (exitTime.isBefore(entryTime)) {
            throw new InvalidParkingStayExitTimeException(
                    "Exit time cannot be before entry time");
        }

        this.exitTime = exitTime;
    }

    public UUID getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }
}