package cl.desafiolatam.parking.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParkingStay {

    private final UUID id;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

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