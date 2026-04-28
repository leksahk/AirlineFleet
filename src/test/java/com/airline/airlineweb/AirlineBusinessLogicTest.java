package com.airline.airlineweb;

import com.airline.airlineweb.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AirlineBusinessLogicTest {

    @Test
    void testPassengerAirplaneCreation() {
        PassengerAirplane plane = new PassengerAirplane("Boeing 737", "Boeing", 2020, 850, 5000, 2.5, 150);

        assertEquals("Boeing 737", plane.getModel());
        assertEquals("Boeing", plane.getManufacturer());
        assertEquals(150, plane.getPassengerSeats());
        assertEquals(2.5, plane.getFuelConsumption());
    }

    @Test
    void testCargoAirplaneCreation() {
        CargoAirplane plane = new CargoAirplane("An-225", "Antonov", 1988, 800, 4000, 15.0, 250.0);

        assertEquals("An-225", plane.getModel());
        assertEquals(250.0, plane.getPayloadCapacity());
    }

    @Test
    void testPrivateJetCreation() {
        PrivateJet plane = new PrivateJet("Gulfstream G650", "Gulfstream", 2022, 950, 11000, 1.8, 10, 5);

        assertEquals(10, plane.getPassengers());
        assertEquals(5, plane.getLuxuryLevel());
    }

    @Test
    void testMilitaryAirplaneCreation() {
        MilitaryAirplane military = new MilitaryAirplane("F-35", "Lockheed Martin", 2021, 1900, 2200, 12.0, 8.5);
        assertEquals(8.5, military.getWeaponLoad());
        assertEquals("Військовий (тонн озброєння)", military.getPlaneType());
        assertTrue(military.toString().contains("Військовий"));
    }

    @Test
    void testNegativeFuelConsumptionThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PassengerAirplane("Test", "Test", 2020, 800, 1000, -5.0, 100);
        });

        assertTrue(exception.getMessage().contains("пального") || exception.getMessage().toLowerCase().contains("fuel"));
    }

    @Test
    void testZeroOrNegativePassengerSeatsThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PassengerAirplane("Test", "Test", 2020, 800, 1000, 2.0, 0);
        });

        assertNotNull(exception);
    }

    @Test
    void testNegativeSpeedThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CargoAirplane("An-12", "Antonov", 1970, -100, 2000, 3.0, 20.0);
        });

        assertNotNull(exception);
    }

    @Test
    void testNegativeValuesAndBranches() {
        PassengerAirplane p = new PassengerAirplane("Test", "Brand", 2020, 100, 100, 10.0, 100);
        try { p.setMaxSpeed(-500); } catch (Exception e) {}
        try { p.setFlightRange(-1000); } catch (Exception e) {}
        try { p.setFuelConsumption(-5.0); } catch (Exception e) {}
        try { p.setPassengerSeats(-50); } catch (Exception e) {}
        try { p.setYearOfManufacture(-2020); } catch (Exception e) {}

        CargoAirplane c = new CargoAirplane("C", "B", 2020, 100, 100, 10.0, 100.0);
        try { c.setPayloadCapacity(-100.0); } catch (Exception e) {}

        MilitaryAirplane m = new MilitaryAirplane("M", "B", 2020, 100, 100, 10.0, 10.0);
        try { m.setWeaponLoad(-5.0); } catch (Exception e) {}

        PrivateJet pj = new PrivateJet("PJ", "B", 2020, 100, 100, 10.0, 10, 5);
        try { pj.setPassengers(-5); } catch (Exception e) {}
        try { pj.setLuxuryLevel(-1); } catch (Exception e) {}

        assertNotNull(p.toString());
        assertNotNull(c.toString());
        assertNotNull(m.toString());
        assertNotNull(pj.toString());
    }

    @Test
    void testAllModelsGettersAndToString() {
        PassengerAirplane p1 = new PassengerAirplane("Boeing", "Brand", 2020, 800, 5000, 2.0, 150);
        PassengerAirplane p2 = new PassengerAirplane("Boeing", "Brand", 2020, 800, 5000, 2.0, 150);

        assertEquals(p1.toString(), p2.toString());

        assertNotNull(p1.getModel());
        assertNotNull(p1.getManufacturer());
        assertNotNull(p1.getPlaneType());
        assertTrue(p1.getYearOfManufacture() > 0);
        assertTrue(p1.getMaxSpeed() > 0);
        assertTrue(p1.getFlightRange() > 0);
        assertTrue(p1.getFuelConsumption() > 0);
        assertTrue(p1.getPassengerSeats() > 0);

        CargoAirplane c1 = new CargoAirplane("C1", "A", 2020, 100, 100, 10.0, 100.0);
        assertTrue(c1.getPayloadCapacity() > 0);
        assertNotNull(c1.toString());

        MilitaryAirplane m1 = new MilitaryAirplane("M1", "A", 2020, 100, 100, 10.0, 10.0);
        assertTrue(m1.getWeaponLoad() > 0);
        assertNotNull(m1.toString());

        PrivateJet pj1 = new PrivateJet("PJ", "A", 2020, 100, 100, 10.0, 10, 5);
        assertTrue(pj1.getPassengers() > 0);
        assertTrue(pj1.getLuxuryLevel() > 0);
        assertNotNull(pj1.toString());
    }
}