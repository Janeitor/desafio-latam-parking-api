package cl.desafiolatam.parking.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayAlreadyExistsException;
import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.domain.port.ParkingStayRepository;

@ExtendWith(MockitoExtension.class)
class ParkingStayServiceTest {

    @Mock
    private ParkingStayRepository repository;

    @InjectMocks
    private ParkingStayService service;

    @Test
    void shouldRegisterEntryWhenLicensePlateIsNotActive() {
        LocalDateTime entryTime =
                LocalDateTime.of(2026, 8, 26, 18, 0);

        when(repository.findActiveByLicensePlate("ABCD12"))
                .thenReturn(Optional.empty());

        when(repository.save(any(ParkingStay.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        ParkingStay result = service.registerEntry(
                " abcd12 ",
                entryTime);

        ArgumentCaptor<ParkingStay> captor =
                ArgumentCaptor.forClass(ParkingStay.class);

        verify(repository)
                .findActiveByLicensePlate("ABCD12");
        verify(repository).save(captor.capture());

        assertThat(result.getLicensePlate())
                .isEqualTo("ABCD12");
        assertThat(result.getEntryTime())
                .isEqualTo(entryTime);
        assertThat(result.getExitTime()).isNull();
        assertThat(result.getId()).isNotNull();

        assertThat(captor.getValue().getLicensePlate())
                .isEqualTo("ABCD12");
    }

    @Test
    void shouldRejectEntryWhenLicensePlateIsAlreadyActive() {
        LocalDateTime entryTime =
                LocalDateTime.of(2026, 8, 26, 18, 0);

        ParkingStay activeParkingStay = new ParkingStay(
                UUID.randomUUID(),
                "ABCD12",
                entryTime.minusHours(1),
                null);

        when(repository.findActiveByLicensePlate("ABCD12"))
                .thenReturn(Optional.of(activeParkingStay));

        assertThatThrownBy(() ->
                service.registerEntry("abcd12", entryTime))
                .isInstanceOf(
                        ActiveParkingStayAlreadyExistsException.class)
                .hasMessageContaining("ABCD12");

        verify(repository)
                .findActiveByLicensePlate("ABCD12");
        verify(repository, never())
                .save(any(ParkingStay.class));
    }
}