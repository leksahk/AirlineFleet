package com.airline.airlineweb;

import com.airline.airlineweb.model.*;
import com.airline.airlineweb.service.AirlineService;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AirlineBusinessLogicTest {

    @Test
    void megaCoverageTest() {
        // 1. Покриваємо AirlineService (Розрахунки)
        AirlineService service = new AirlineService();
        PassengerAirplane p1 = new PassengerAirplane("737", "Boeing", 2020, 850, 5000, 2.5, 180);
        CargoAirplane c1 = new CargoAirplane("An-124", "Antonov", 1990, 800, 4500, 10.0, 120.0);

        List<Airplane> fleet = Arrays.asList(p1, c1);
        assertEquals(180, service.calculateTotalPassengerCapacity(fleet));
        assertEquals(120.0, service.calculateTotalCargoCapacity(fleet));

        // 2. Покриваємо ВСІ моделі на 100% (гетери, сетери, toString)
        // Passenger
        p1.setPassengerSeats(200);
        assertEquals(200, p1.getPassengerSeats());
        assertNotNull(p1.toString());
        assertEquals("Пасажирський (чол)", p1.getPlaneType());

        // Cargo
        c1.setPayloadCapacity(150.0);
        assertEquals(150.0, c1.getPayloadCapacity());
        assertNotNull(c1.toString());

        // Military
        MilitaryAirplane mil = new MilitaryAirplane("F-16", "LM", 2010, 2100, 3000, 5.0, 2.0);
        mil.setWeaponLoad(3.0);
        assertEquals(3.0, mil.getWeaponLoad());
        assertNotNull(mil.toString());

        // PrivateJet
        PrivateJet jet = new PrivateJet("G650", "Gulfstream", 2021, 980, 13000, 1.5, 8, 5);
        jet.setLuxuryLevel(4);
        jet.setPassengers(10);
        assertEquals(4, jet.getLuxuryLevel());
        assertEquals(10, jet.getPassengers());
        assertNotNull(jet.toString());

        // Перевірка базових полів (Airplane)
        jet.setModel("New");
        jet.setManufacturer("Man");
        jet.setYearOfManufacture(2025);
        jet.setMaxSpeed(900);
        jet.setFlightRange(10000);
        jet.setFuelConsumption(1.0);
        assertEquals("New", jet.getModel());
    }
}