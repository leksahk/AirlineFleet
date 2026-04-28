package com.airline.airlineweb.controller;

import com.airline.airlineweb.model.*;
import com.airline.airlineweb.service.AirlineService;
import com.airline.airlineweb.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AirlineService airlineService;

    @MockitoBean
    private EmailService emailService;

    private PassengerAirplane passengerPlane;
    private CargoAirplane cargoPlane;
    private MilitaryAirplane militaryPlane;
    private PrivateJet privateJet;

    @BeforeEach
    void setUp() {
        passengerPlane = new PassengerAirplane("Boeing 737", "Boeing", 2020, 850, 5000, 2.5, 180);
        cargoPlane     = new CargoAirplane("An-124", "Antonov", 1990, 800, 4500, 10.0, 120.0);
        militaryPlane  = new MilitaryAirplane("F-16", "Lockheed", 2010, 2100, 3000, 5.0, 2.0);
        privateJet     = new PrivateJet("G650", "Gulfstream", 2021, 980, 13000, 1.5, 8, 5);
    }

    @Test
    void testIndexPage_EmptyFleet() throws Exception {
        when(airlineService.getAllAirplanes()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("fleet"))
                .andExpect(model().attributeExists("totalPassenger"))
                .andExpect(model().attributeExists("totalCargo"));
    }

    @Test
    void testIndexPage_WithFleet() throws Exception {
        List<Airplane> fleet = Arrays.asList(passengerPlane, cargoPlane);
        when(airlineService.getAllAirplanes()).thenReturn(fleet);
        when(airlineService.calculateTotalPassengerCapacity(fleet)).thenReturn(180);
        when(airlineService.calculateTotalCargoCapacity(fleet)).thenReturn(120.0);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("totalPassenger", 180))
                .andExpect(model().attribute("totalCargo", 120.0));
    }

    @Test
    void testShowAddForm_ReturnsAddPlaneView() throws Exception {
        mockMvc.perform(get("/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-plane"));
    }

    @Test
    void testAddPassengerAirplane_RedirectsToHome() throws Exception {
        mockMvc.perform(post("/add")
                        .param("type", "Passenger")
                        .param("model", "Boeing 737")
                        .param("manufacturer", "Boeing")
                        .param("year", "2020")
                        .param("maxSpeed", "850")
                        .param("flightRange", "5000")
                        .param("fuelConsumption", "2.5")
                        .param("specialValue", "180"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).addAirplane(any(PassengerAirplane.class));
    }

    @Test
    void testAddCargoAirplane_RedirectsToHome() throws Exception {
        mockMvc.perform(post("/add")
                        .param("type", "Cargo")
                        .param("model", "An-124")
                        .param("manufacturer", "Antonov")
                        .param("year", "1990")
                        .param("maxSpeed", "800")
                        .param("flightRange", "4500")
                        .param("fuelConsumption", "10.0")
                        .param("specialValue", "120.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).addAirplane(any(CargoAirplane.class));
    }

    @Test
    void testAddMilitaryAirplane_RedirectsToHome() throws Exception {
        mockMvc.perform(post("/add")
                        .param("type", "Military")
                        .param("model", "F-16")
                        .param("manufacturer", "Lockheed")
                        .param("year", "2010")
                        .param("maxSpeed", "2100")
                        .param("flightRange", "3000")
                        .param("fuelConsumption", "5.0")
                        .param("specialValue", "2.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).addAirplane(any(MilitaryAirplane.class));
    }

    @Test
    void testAddPrivateJet_RedirectsToHome() throws Exception {
        mockMvc.perform(post("/add")
                        .param("type", "Private")
                        .param("model", "G650")
                        .param("manufacturer", "Gulfstream")
                        .param("year", "2021")
                        .param("maxSpeed", "980")
                        .param("flightRange", "13000")
                        .param("fuelConsumption", "1.5")
                        .param("specialValue", "8")
                        .param("luxuryLevel", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).addAirplane(any(PrivateJet.class));
    }

    @Test
    void testAddAirplane_UnknownType_RedirectsToAddWithError() throws Exception {
        mockMvc.perform(post("/add")
                        .param("type", "Unknown")
                        .param("model", "X1")
                        .param("manufacturer", "Brand")
                        .param("year", "2020")
                        .param("maxSpeed", "800")
                        .param("flightRange", "5000")
                        .param("fuelConsumption", "2.5")
                        .param("specialValue", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add"));

        verify(airlineService, never()).addAirplane(any());
        verify(emailService, times(1)).sendErrorAlert(anyString(), anyString(), anyString());
    }

    @Test
    void testAddAirplane_InvalidPassengerSeats_RedirectsToAddWithError() throws Exception {
        // passengerSeats = 0 — невалідне значення (< 1)
        mockMvc.perform(post("/add")
                        .param("type", "Passenger")
                        .param("model", "BadPlane")
                        .param("manufacturer", "Brand")
                        .param("year", "2020")
                        .param("maxSpeed", "850")
                        .param("flightRange", "5000")
                        .param("fuelConsumption", "2.5")
                        .param("specialValue", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add"));

        verify(airlineService, never()).addAirplane(any());
    }

    @Test
    void testAddAirplane_ServiceThrowsException_SendsEmailAlert() throws Exception {
        doThrow(new RuntimeException("Firebase down")).when(airlineService).addAirplane(any());

        mockMvc.perform(post("/add")
                        .param("type", "Passenger")
                        .param("model", "Boeing 737")
                        .param("manufacturer", "Boeing")
                        .param("year", "2020")
                        .param("maxSpeed", "850")
                        .param("flightRange", "5000")
                        .param("fuelConsumption", "2.5")
                        .param("specialValue", "180"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add"));

        verify(emailService, times(1)).sendErrorAlert(
                eq("RuntimeException"),
                eq("Firebase down"),
                eq("CRITICAL")
        );
    }

    @Test
    void testDeleteAirplane_RedirectsToHome() throws Exception {
        mockMvc.perform(get("/delete").param("model", "Boeing 737"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).deleteAirplane("Boeing 737");
    }

    @Test
    void testDeleteAirplane_NonExistentModel_StillRedirects() throws Exception {
        doNothing().when(airlineService).deleteAirplane("NonExistent");

        mockMvc.perform(get("/delete").param("model", "NonExistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testSort_ByRange_Asc() throws Exception {
        List<Airplane> sorted = Arrays.asList(passengerPlane, cargoPlane);
        when(airlineService.getSortedFleet("range", "asc")).thenReturn(sorted);

        mockMvc.perform(get("/sort").param("by", "range").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("fleet"));
    }

    @Test
    void testSort_BySpeed_Desc() throws Exception {
        when(airlineService.getSortedFleet("speed", "desc")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/sort").param("by", "speed").param("dir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testSort_ByFuel_Asc() throws Exception {
        when(airlineService.getSortedFleet("fuel", "asc")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/sort").param("by", "fuel").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testSort_DefaultParams() throws Exception {
        when(airlineService.getSortedFleet("range", "asc")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/sort")) // без параметрів — дефолти "range" і "asc"
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testSearch_ValidRange_ReturnsResults() throws Exception {
        List<Airplane> found = Arrays.asList(passengerPlane);
        when(airlineService.findAirplanesByFuelRange(1.0, 5.0)).thenReturn(found);
        when(airlineService.calculateTotalPassengerCapacity(found)).thenReturn(180);
        when(airlineService.calculateTotalCargoCapacity(found)).thenReturn(0.0);

        mockMvc.perform(get("/search").param("minFuel", "1.0").param("maxFuel", "5.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("fleet"))
                .andExpect(model().attribute("totalPassenger", 180));
    }

    @Test
    void testSearch_NoResults_ReturnsEmptyFleet() throws Exception {
        when(airlineService.findAirplanesByFuelRange(99.0, 100.0)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/search").param("minFuel", "99.0").param("maxFuel", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testSearch_MissingParams_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/search")) // без minFuel і maxFuel
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowEditForm_PlaneFound_ReturnsEditView() throws Exception {
        when(airlineService.getAirplaneByModel("Boeing 737")).thenReturn(passengerPlane);

        mockMvc.perform(get("/edit").param("model", "Boeing 737"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-plane"))
                .andExpect(model().attribute("plane", passengerPlane));
    }

    @Test
    void testShowEditForm_PlaneNotFound_RedirectsToHome() throws Exception {
        when(airlineService.getAirplaneByModel("NonExistent")).thenReturn(null);

        mockMvc.perform(get("/edit").param("model", "NonExistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testUpdateAirplane_Success_RedirectsToHome() throws Exception {
        mockMvc.perform(post("/edit")
                        .param("oldModelName", "OldModel")
                        .param("model", "NewModel")
                        .param("manufacturer", "Boeing")
                        .param("yearOfManufacture", "2022")
                        .param("maxSpeed", "900")
                        .param("flightRange", "6000")
                        .param("fuelConsumption", "3.0")
                        .param("specialValue", "200")
                        .param("luxuryLevel", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(airlineService, times(1)).updateAirplane(
                eq("OldModel"), eq("NewModel"), eq("Boeing"),
                eq(2022), eq(900.0), eq(6000.0), eq(3.0),
                eq(200.0), eq(5)
        );
    }

    @Test
    void testUpdateAirplane_ServiceThrowsException_RedirectsToEdit() throws Exception {
        doThrow(new IllegalArgumentException("Невалідна швидкість"))
                .when(airlineService).updateAirplane(anyString(), anyString(), anyString(),
                        anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt());

        mockMvc.perform(post("/edit")
                        .param("oldModelName", "BadModel")
                        .param("model", "BadModel")
                        .param("manufacturer", "Brand")
                        .param("yearOfManufacture", "2020")
                        .param("maxSpeed", "-100")
                        .param("flightRange", "5000")
                        .param("fuelConsumption", "2.5")
                        .param("specialValue", "150")
                        .param("luxuryLevel", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/edit?model=*"));
    }
}