package com.airline.airlineweb.service;

import com.airline.airlineweb.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AirlineService {

    private final String COLLECTION_NAME = "airplanes";

    public List<Airplane> getAllAirplanes() {
        Firestore db = FirestoreClient.getFirestore();
        List<Airplane> fleet = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                fleet.add(mapToAirplane(doc));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return fleet;
    }

    public void addAirplane(Airplane plane) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> docData = new HashMap<>();

        docData.put("type", plane.getClass().getSimpleName());
        docData.put("model", plane.getModel());
        docData.put("manufacturer", plane.getManufacturer());
        docData.put("yearOfManufacture", plane.getYearOfManufacture());
        docData.put("maxSpeed", plane.getMaxSpeed());
        docData.put("flightRange", plane.getFlightRange());
        docData.put("fuelConsumption", plane.getFuelConsumption());

        if (plane instanceof PassengerAirplane) docData.put("capacity", ((PassengerAirplane) plane).getPassengerSeats());
        else if (plane instanceof CargoAirplane) docData.put("capacity", ((CargoAirplane) plane).getPayloadCapacity());
        else if (plane instanceof MilitaryAirplane) docData.put("capacity", ((MilitaryAirplane) plane).getWeaponLoad());
        else if (plane instanceof PrivateJet) {
            docData.put("capacity", ((PrivateJet) plane).getPassengers());
            docData.put("luxuryLevel", ((PrivateJet) plane).getLuxuryLevel());
        }

        try {
            db.collection(COLLECTION_NAME).document(plane.getModel()).set(docData).get();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteAirplane(String modelName) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.collection(COLLECTION_NAME).document(modelName).delete().get();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void updateAirplane(String oldModelName, String newModel, String newManufacturer,
                               int newYear, double newSpeed, double newRange, double newFuel) {
        Airplane plane = getAirplaneByModel(oldModelName);
        if (plane != null) {
            plane.setModel(newModel);
            plane.setManufacturer(newManufacturer);
            plane.setYearOfManufacture(newYear);
            plane.setMaxSpeed(newSpeed);
            plane.setFlightRange(newRange);
            plane.setFuelConsumption(newFuel);

            deleteAirplane(oldModelName);
            addAirplane(plane);
        }
    }

    public Airplane getAirplaneByModel(String model) {
        return getAllAirplanes().stream()
                .filter(plane -> plane.getModel().equals(model))
                .findFirst()
                .orElse(null);
    }

    private Airplane mapToAirplane(QueryDocumentSnapshot doc) {
        String type = doc.getString("type");
        String model = doc.getString("model");
        String manufacturer = doc.getString("manufacturer");
        int year = doc.getLong("yearOfManufacture") != null ? doc.getLong("yearOfManufacture").intValue() : 2024;
        double speed = doc.getDouble("maxSpeed") != null ? doc.getDouble("maxSpeed") : 0;
        double range = doc.getDouble("flightRange") != null ? doc.getDouble("flightRange") : 0;
        double fuel = doc.getDouble("fuelConsumption") != null ? doc.getDouble("fuelConsumption") : 0;
        double capacity = doc.getDouble("capacity") != null ? doc.getDouble("capacity") : 0;
        int lux = doc.getLong("luxuryLevel") != null ? doc.getLong("luxuryLevel").intValue() : 5;

        return switch (type != null ? type : "") {
            case "PassengerAirplane" -> new PassengerAirplane(model, manufacturer, year, speed, range, fuel, (int) capacity);
            case "CargoAirplane" -> new CargoAirplane(model, manufacturer, year, speed, range, fuel, capacity);
            case "MilitaryAirplane" -> new MilitaryAirplane(model, manufacturer, year, speed, range, fuel, capacity);
            case "PrivateJet" -> new PrivateJet(model, manufacturer, year, speed, range, fuel, (int) capacity, lux);
            default -> new PassengerAirplane(model, manufacturer, year, speed, range, fuel, (int) capacity);
        };
    }

    public int calculateTotalPassengerCapacity(List<Airplane> fleet) {
        return fleet.stream()
                .filter(p -> p instanceof PassengerAirplane || p instanceof PrivateJet)
                .mapToInt(p -> p instanceof PassengerAirplane ? ((PassengerAirplane) p).getPassengerSeats() : ((PrivateJet) p).getPassengers())
                .sum();
    }

    public double calculateTotalCargoCapacity(List<Airplane> fleet) {
        return fleet.stream()
                .filter(p -> p instanceof CargoAirplane || p instanceof MilitaryAirplane)
                .mapToDouble(p -> p instanceof CargoAirplane ? ((CargoAirplane) p).getPayloadCapacity() : ((MilitaryAirplane) p).getWeaponLoad())
                .sum();
    }

    public List<Airplane> findAirplanesByFuelRange(double min, double max) {
        return getAllAirplanes().stream()
                .filter(p -> p.getFuelConsumption() >= min && p.getFuelConsumption() <= max)
                .collect(Collectors.toList());
    }

    public List<Airplane> getSortedFleet(String sortBy, String direction) {
        Comparator<Airplane> comparator = switch (sortBy) {
            case "speed" -> Comparator.comparingDouble(Airplane::getMaxSpeed);
            case "fuel" -> Comparator.comparingDouble(Airplane::getFuelConsumption);
            default -> Comparator.comparingDouble(Airplane::getFlightRange);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        return getAllAirplanes().stream().sorted(comparator).collect(Collectors.toList());
    }
}