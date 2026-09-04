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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayAlreadyExistsException;
import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.domain.port.ParkingStayRepository;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
import cl.desafiolatam.parking.domain.exception.ParkingStayNotFoundException;

@ExtendWith(MockitoExtension.class)
class ParkingStayServiceTest {

        @Mock
        private ParkingStayRepository repository;

        @InjectMocks
        private ParkingStayService service;

        @Test
        void shouldRegisterEntryWhenLicensePlateIsNotActive() {
                LocalDateTime entryTime = LocalDateTime.of(2026, 8, 26, 18, 0);

                when(repository.findActiveByLicensePlate("ABCD12"))
                                .thenReturn(Optional.empty());

                when(repository.save(any(ParkingStay.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                ParkingStay result = service.registerEntry(
                                " abcd12 ",
                                entryTime);

                ArgumentCaptor<ParkingStay> captor = ArgumentCaptor.forClass(ParkingStay.class);

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
                LocalDateTime entryTime = LocalDateTime.of(2026, 8, 26, 18, 0);

                ParkingStay activeParkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                entryTime.minusHours(1),
                                null);

                when(repository.findActiveByLicensePlate("ABCD12"))
                                .thenReturn(Optional.of(activeParkingStay));

                assertThatThrownBy(() -> service.registerEntry("abcd12", entryTime))
                                .isInstanceOf(
                                                ActiveParkingStayAlreadyExistsException.class)
                                .hasMessageContaining("ABCD12");

                verify(repository)
                                .findActiveByLicensePlate("ABCD12");
                verify(repository, never())
                                .save(any(ParkingStay.class));
        }

        @Test
        void shouldCloseAndSaveActiveParkingStay() {
                UUID id = UUID.randomUUID();

                LocalDateTime entryTime = LocalDateTime.of(2026, 9, 2, 10, 30);

                LocalDateTime exitTime = LocalDateTime.of(2026, 9, 2, 12, 45);

                ParkingStay activeParkingStay = new ParkingStay(
                                id,
                                "ABCD12",
                                entryTime,
                                null);

                when(repository.findById(id))
                                .thenReturn(Optional.of(activeParkingStay));

                when(repository.save(any(ParkingStay.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                ParkingStay result = service.checkout(
                                id,
                                exitTime);

                verify(repository).findById(id);
                verify(repository).save(activeParkingStay);

                assertThat(result.getId()).isEqualTo(id);
                assertThat(result.getExitTime()).isEqualTo(exitTime);
        }

        @Test
        void shouldReturnAllParkingStays() {
                ParkingStay firstParkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                LocalDateTime.of(2026, 9, 3, 10, 0),
                                null);

                ParkingStay secondParkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "EFGH34",
                                LocalDateTime.of(2026, 9, 3, 11, 0),
                                null);

                when(repository.findAll())
                                .thenReturn(List.of(
                                                firstParkingStay,
                                                secondParkingStay));

                List<ParkingStay> result = service.findAll();

                assertThat(result)
                                .containsExactly(
                                                firstParkingStay,
                                                secondParkingStay);

                verify(repository).findAll();
        }

        @Test
        void shouldFindActiveParkingStayByNormalizedLicensePlate() {
                ParkingStay activeParkingStay = new ParkingStay(
                                UUID.randomUUID(),
                                "ABCD12",
                                LocalDateTime.of(2026, 9, 3, 10, 0),
                                null);

                when(repository.findActiveByLicensePlate("ABCD12"))
                                .thenReturn(Optional.of(activeParkingStay));

                ParkingStay result = service.findActiveByLicensePlate(" abcd12 ");

                assertThat(result).isSameAs(activeParkingStay);

                verify(repository)
                                .findActiveByLicensePlate("ABCD12");
        }

        @Test
        void shouldRejectWhenActiveParkingStayDoesNotExist() {
                when(repository.findActiveByLicensePlate("ABCD12"))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> service.findActiveByLicensePlate(" abcd12 "))
                                .isInstanceOf(
                                                ActiveParkingStayNotFoundException.class);

                verify(repository)
                                .findActiveByLicensePlate("ABCD12");
        }

        @Test
        void shouldFindParkingStayById() {
                UUID id = UUID.randomUUID();

                ParkingStay parkingStay = new ParkingStay(
                                id,
                                "ABCD12",
                                LocalDateTime.of(2026, 9, 3, 10, 0),
                                null);

                when(repository.findById(id))
                                .thenReturn(Optional.of(parkingStay));

                ParkingStay result = service.findById(id);

                assertThat(result).isSameAs(parkingStay);

                verify(repository).findById(id);
        }

        @Test
        void shouldRejectWhenParkingStayIdDoesNotExist() {
                UUID id = UUID.randomUUID();

                when(repository.findById(id))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> service.findById(id))
                                .isInstanceOf(
                                                ParkingStayNotFoundException.class)
                                .hasMessageContaining(id.toString());

                verify(repository).findById(id);
        }
}