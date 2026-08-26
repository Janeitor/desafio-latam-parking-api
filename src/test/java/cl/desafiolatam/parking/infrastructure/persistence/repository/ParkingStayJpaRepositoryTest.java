package cl.desafiolatam.parking.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.desafiolatam.parking.infrastructure.persistence.entity.ParkingStayEntity;

@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE)
class ParkingStayJpaRepositoryTest {

    @Autowired
    private ParkingStayJpaRepository repository;

    @Test
    void shouldSaveAndFindActiveParkingStayByLicensePlate() {
        UUID id = UUID.randomUUID();
        LocalDateTime entryTime =
                LocalDateTime.of(2026, 8, 26, 17, 0);

        ParkingStayEntity parkingStay = new ParkingStayEntity(
                id,
                "ABCD12",
                entryTime,
                null);

        repository.save(parkingStay);

        Optional<ParkingStayEntity> result =
                repository
                        .findFirstByLicensePlateAndExitTimeIsNull(
                                "ABCD12");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getLicensePlate())
                .isEqualTo("ABCD12");
        assertThat(result.get().getEntryTime())
                .isEqualTo(entryTime);
        assertThat(result.get().getExitTime()).isNull();
    }
}