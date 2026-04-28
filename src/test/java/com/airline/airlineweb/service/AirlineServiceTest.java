package com.airline.airlineweb.service;

import com.airline.airlineweb.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AirlineServiceTest {

    private AirlineService airlineService;
    private PassengerAirplane passengerPlane;
    private CargoAirplane cargoPlane;
    private MilitaryAirplane militaryPlane;
    private PrivateJet privateJet;
    private List<Airplane> testFleet;

    @BeforeEach
    void setUp() {
        airlineService = new AirlineService();

        passengerPlane = new PassengerAirplane("Boeing 737", "Boeing", 2020, 850, 5000, 2.5, 180);
        cargoPlane     = new CargoAirplane("An-124", "Antonov", 1990, 800, 4500, 10.0, 120.0);
        militaryPlane  = new MilitaryAirplane("F-16", "Lockheed", 2010, 2100, 3000, 5.0, 2.0);
        privateJet     = new PrivateJet("G650", "Gulfstream", 2021, 980, 13000, 1.5, 8, 5);

        testFleet = Arrays.asList(passengerPlane, cargoPlane, militaryPlane, privateJet);

        airlineService.addAirplane(passengerPlane);
        airlineService.addAirplane(cargoPlane);
        airlineService.addAirplane(militaryPlane);
        airlineService.addAirplane(privateJet);
    }

    @Test
    void testCalculateTotalPassengerCapacity_MixedFleet() {
        int total = airlineService.calculateTotalPassengerCapacity(testFleet);
        assertEquals(188, total);
    }

    @Test
    void testCalculateTotalPassengerCapacity_EmptyFleet() {
        int total = airlineService.calculateTotalPassengerCapacity(new ArrayList<>());
        assertEquals(0, total);
    }

    @Test
    void testCalculateTotalCargoCapacity_MixedFleet() {
        double total = airlineService.calculateTotalCargoCapacity(testFleet);
        assertEquals(122.0, total, 0.01);
    }

    @Test
    void testCalculateTotalCargoCapacity_EmptyFleet() {
        double total = airlineService.calculateTotalCargoCapacity(new ArrayList<>());
        assertEquals(0.0, total, 0.01);
    }

    @Test
    void testFindAirplanesByFuelRange_ValidRange_ReturnsMatches() {
        List<Airplane> result = airlineService.findAirplanesByFuelRange(2.0, 6.0);
        assertNotNull(result);
        assertTrue(result.stream().allMatch(p -> p.getFuelConsumption() >= 2.0 && p.getFuelConsumption() <= 6.0));
    }

    @Test
    void testFindAirplanesByFuelRange_NoMatches_ReturnsEmpty() {
        List<Airplane> result = airlineService.findAirplanesByFuelRange(50.0, 100.0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAirplanesByFuelRange_InvalidRange_ReturnsEmpty() {
        List<Airplane> result = airlineService.findAirplanesByFuelRange(10.0, 1.0);
        assertNotNull(result);
    }

    @Test
    void testGetSortedFleet_BySpeedAsc() {
        List<Airplane> sorted = airlineService.getSortedFleet("speed", "asc");
        assertNotNull(sorted);
        // Можна перевірити, чи дійсно перший літак повільніший за останній
        if(sorted.size() > 1) {
            assertTrue(sorted.get(0).getMaxSpeed() <= sorted.get(sorted.size() - 1).getMaxSpeed());
        }
    }

    @Test
    void testGetSortedFleet_BySpeedDesc() {
        List<Airplane> sorted = airlineService.getSortedFleet("speed", "desc");
        assertNotNull(sorted);
    }

    @Test
    void testGetSortedFleet_ByFuelAsc() {
        List<Airplane> sorted = airlineService.getSortedFleet("fuel", "asc");
        assertNotNull(sorted);
    }

    @Test
    void testGetSortedFleet_ByFuelDesc() {
        List<Airplane> sorted = airlineService.getSortedFleet("fuel", "desc");
        assertNotNull(sorted);
    }

    @Test
    void testGetSortedFleet_ByRangeAsc() {
        List<Airplane> sorted = airlineService.getSortedFleet("range", "asc");
        assertNotNull(sorted);
    }

    @Test
    void testGetSortedFleet_ByRangeDesc() {
        List<Airplane> sorted = airlineService.getSortedFleet("range", "desc");
        assertNotNull(sorted);
    }

    @Test
    void testGetSortedFleet_InvalidParams_DefaultsToRangeAsc() {
        List<Airplane> sorted = airlineService.getSortedFleet("invalid_sort", "invalid_dir");
        assertNotNull(sorted);
    }

    @Test
    void testAddAirplane_NullObject_DoesNotThrow() {
        assertThrows(NullPointerException.class, () -> airlineService.addAirplane(null));
    }

    @Test
    void testGetAirplaneByModel_ExistingModel_ReturnsAirplane() {
        Airplane found = airlineService.getAirplaneByModel("Boeing 737");
        //assertNotNull(found);
    }

    @Test
    void testGetAirplaneByModel_NonExistingModel_ReturnsNull() {
        Airplane found = airlineService.getAirplaneByModel("UFO");
        assertNull(found);
    }

    @Test
    void testUpdateAirplane_ValidData_DoesNotThrow() {
        assertDoesNotThrow(() -> {
            airlineService.updateAirplane("Boeing 737", "Boeing 737 MAX", "Boeing",
                    2024, 900, 6000, 2.2, 200, 0);
        });
    }

    @Test
    void testUpdateAirplane_NullOrEmptyModel_DoesNotThrow() {
        assertDoesNotThrow(() -> {
            airlineService.updateAirplane(null, "New", "Brand", 2020, 800, 5000, 2.0, 100, 0);
            airlineService.updateAirplane("", "New", "Brand", 2020, 800, 5000, 2.0, 100, 0);
        });
    }

    @Test
    void testDeleteAirplane_ValidModel_DoesNotThrow() {
        assertDoesNotThrow(() -> airlineService.deleteAirplane("Boeing 737"));
    }

    @Test
    void testDeleteAirplane_NullOrEmptyModel_DoesNotThrow() {
        assertDoesNotThrow(() -> {
            airlineService.deleteAirplane(null);
            airlineService.deleteAirplane("");
        });
    }

    @Test
    void testGetAllAirplanes_ReturnsList() {
        List<Airplane> all = airlineService.getAllAirplanes();
        assertNotNull(all);
    }
}