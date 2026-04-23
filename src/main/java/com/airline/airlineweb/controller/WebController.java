package com.airline.airlineweb.controller;

import com.airline.airlineweb.model.*;
import com.airline.airlineweb.service.AirlineService;
import com.airline.airlineweb.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    private final AirlineService airlineService;
    private final EmailService emailService;

    public WebController(AirlineService airlineService, EmailService emailService) {
        this.airlineService = airlineService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.info("Користувач відкрив головну сторінку (Dashboard)");
        List<Airplane> currentFleet = airlineService.getAllAirplanes();
        model.addAttribute("fleet", currentFleet);
        model.addAttribute("totalPassenger", airlineService.calculateTotalPassengerCapacity(currentFleet));
        model.addAttribute("totalCargo", airlineService.calculateTotalCargoCapacity(currentFleet));
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm() {
        return "add-plane";
    }

    @PostMapping("/add")
    public String addAirplane(@RequestParam String type,
                              @RequestParam String model,
                              @RequestParam String manufacturer,
                              @RequestParam int year,
                              @RequestParam double maxSpeed,
                              @RequestParam double flightRange,
                              @RequestParam double fuelConsumption,
                              @RequestParam double specialValue,
                              @RequestParam(defaultValue = "5") int luxuryLevel,
                              RedirectAttributes redirectAttributes) {
        try {
            Airplane newPlane = switch (type) {
                case "Passenger" -> new PassengerAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, (int) specialValue);
                case "Cargo" -> new CargoAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, specialValue);
                case "Military" -> new MilitaryAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, specialValue);
                case "Private" -> new PrivateJet(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, (int) specialValue, luxuryLevel);
                default -> throw new IllegalArgumentException("Невідомий тип літака: " + type);
            };

            //if (model.equals("fail")) throw new RuntimeException("Емуляція втрати зв'язку з базою!");

            airlineService.addAirplane(newPlane);
            logger.info("Літак {} успішно додано.", model);

            return "redirect:/";

        } catch (Exception e) {
            String errorType = e.getClass().getSimpleName();
            String errorMessage = e.getMessage();
            String severity = (e instanceof RuntimeException) ? "CRITICAL" : "WARNING";

            logger.error("🚨 Помилка [{}]: {}", errorType, errorMessage);

            if (emailService != null) {
                emailService.sendErrorAlert(errorType, errorMessage, severity);
            }

            redirectAttributes.addFlashAttribute("errorMessage", "Помилка: " + errorMessage);
            return "redirect:/add";
        }
    }

    @GetMapping("/delete")
    public String deleteAirplane(@RequestParam String model) {
        logger.warn("Видалення літака: {}", model);
        airlineService.deleteAirplane(model);
        return "redirect:/";
    }

    @GetMapping("/sort")
    public String sortAirplanes(@RequestParam(defaultValue = "range") String by,
                                @RequestParam(defaultValue = "asc") String dir,
                                Model model) {
        List<Airplane> sortedFleet = airlineService.getSortedFleet(by, dir);
        model.addAttribute("fleet", sortedFleet);
        model.addAttribute("totalPassenger", airlineService.calculateTotalPassengerCapacity(sortedFleet));
        model.addAttribute("totalCargo", airlineService.calculateTotalCargoCapacity(sortedFleet));
        return "index";
    }

    @GetMapping("/search")
    public String searchAirplanes(@RequestParam double minFuel, @RequestParam double maxFuel, Model model) {
        List<Airplane> foundFleet = airlineService.findAirplanesByFuelRange(minFuel, maxFuel);
        model.addAttribute("fleet", foundFleet);
        model.addAttribute("totalPassenger", airlineService.calculateTotalPassengerCapacity(foundFleet));
        model.addAttribute("totalCargo", airlineService.calculateTotalCargoCapacity(foundFleet));
        return "index";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam String model, Model springModel) {
        Airplane plane = airlineService.getAirplaneByModel(model);
        if (plane == null) return "redirect:/";
        springModel.addAttribute("plane", plane);
        return "edit-plane";
    }

    @PostMapping("/edit")
    public String updateAirplane(@RequestParam String oldModelName,
                                 @RequestParam String model,
                                 @RequestParam String manufacturer,
                                 @RequestParam int yearOfManufacture,
                                 @RequestParam double maxSpeed,
                                 @RequestParam double flightRange,
                                 @RequestParam double fuelConsumption) {
        airlineService.updateAirplane(oldModelName, model, manufacturer, yearOfManufacture, maxSpeed, flightRange, fuelConsumption);
        return "redirect:/";
    }
}