package cl.desafiolatam.parking.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.desafiolatam.parking.domain.exception.ActiveParkingStayAlreadyExistsException;
import cl.desafiolatam.parking.domain.exception.ActiveParkingStayNotFoundException;
import cl.desafiolatam.parking.domain.model.ParkingStay;
import cl.desafiolatam.parking.domain.port.ParkingStayRepository;
import java.util.UUID;

import cl.desafiolatam.parking.domain.exception.ParkingStayNotFoundException;

@Service
@Transactional
public class ParkingStayService {

        private final ParkingStayRepository repository;

        public ParkingStayService(
                        ParkingStayRepository repository) {
                this.repository = repository;
        }

        public ParkingStay registerEntry(
                        String licensePlate,
                        LocalDateTime entryTime) {
                String normalizedLicensePlate = licensePlate.trim().toUpperCase(Locale.ROOT);

                boolean alreadyParked = repository
                                .findActiveByLicensePlate(
                                                normalizedLicensePlate)
                                .isPresent();

                if (alreadyParked) {
                        throw new ActiveParkingStayAlreadyExistsException(
                                        normalizedLicensePlate);
                }

                ParkingStay parkingStay = ParkingStay.register(
                                normalizedLicensePlate,
                                entryTime);

                return repository.save(parkingStay);
        }

        public ParkingStay checkout(
                        UUID id,
                        LocalDateTime exitTime) {
                ParkingStay parkingStay = findById(id);

                parkingStay.close(exitTime);

                return repository.save(parkingStay);
        }

        @Transactional(readOnly = true)
        public List<ParkingStay> findAll() {
                return repository.findAll();
        }

        @Transactional(readOnly = true)
        public ParkingStay findActiveByLicensePlate(
                        String licensePlate) {
                String normalizedLicensePlate = licensePlate.trim().toUpperCase(Locale.ROOT);

                return repository
                                .findActiveByLicensePlate(
                                                normalizedLicensePlate)
                                .orElseThrow(
                                                ActiveParkingStayNotFoundException::new);
        }

        @Transactional(readOnly = true)
        public ParkingStay findById(UUID id) {
                return repository
                                .findById(id)
                                .orElseThrow(
                                                () -> new ParkingStayNotFoundException(id));
        }
}