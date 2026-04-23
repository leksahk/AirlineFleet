package com.airline.airlineweb.controller;

import com.airline.airlineweb.model.*;
import com.airline.airlineweb.service.AirlineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    private final AirlineService airlineService;

    public WebController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Airplane> currentFleet = airlineService.getAllAirplanes();
        model.addAttribute("fleet", currentFleet);
        model.addAttribute("totalPassenger", airlineService.calculateTotalPassengerCapacity(currentFleet));
        model.addAttribute("totalCargo", airlineService.calculateTotalCargoCapacity(currentFleet));
        return "index";
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

    @GetMapping("/delete")
    public String deleteAirplane(@RequestParam String model) {
        airlineService.deleteAirplane(model);
        return "redirect:/";
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
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Airplane newPlane = switch (type) {
                case "Passenger" -> {
                    if (specialValue % 1 != 0) {
                        throw new IllegalArgumentException("Кількість пасажирів має бути цілим числом!");
                    }
                    yield new PassengerAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, (int) specialValue);
                }
                case "Cargo" -> new CargoAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, specialValue);
                case "Military" -> new MilitaryAirplane(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, specialValue);
                case "Private" -> {
                    if (specialValue % 1 != 0) {
                        throw new IllegalArgumentException("Кількість VIP-пасажирів має бути цілим числом!");
                    }
                    yield new PrivateJet(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, (int) specialValue, luxuryLevel);
                }
                default -> throw new IllegalArgumentException("Невідомий тип літака");
            };

            airlineService.addAirplane(newPlane);
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            redirectAttributes.addFlashAttribute("typedType", type);
            redirectAttributes.addFlashAttribute("typedModel", model);
            redirectAttributes.addFlashAttribute("typedManufacturer", manufacturer);
            redirectAttributes.addFlashAttribute("typedYear", year);
            redirectAttributes.addFlashAttribute("typedSpeed", maxSpeed);
            redirectAttributes.addFlashAttribute("typedRange", flightRange);
            redirectAttributes.addFlashAttribute("typedFuel", fuelConsumption);
            redirectAttributes.addFlashAttribute("typedSpecial", specialValue);
            redirectAttributes.addFlashAttribute("typedLuxury", luxuryLevel);

            return "redirect:/add";
        }
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