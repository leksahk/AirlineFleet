package com.airline.airlineweb.service;

import com.airline.airlineweb.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AirlineService {

    private static  Logger logger = LoggerFactory.getLogger(AirlineService.class);
    private  String COLLECTION_NAME = "airplanes";

    public List<Airplane> getAllAirplanes() {
        Firestore db = FirestoreClient.getFirestore();
        List<Airplane> fleet = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                fleet.add(mapToAirplane(doc));
            }
            logger.info("Успішно завантажено {} літаків з бази даних.", fleet.size());
        } catch (Exception e) {
            //якщо Firebase впаде або ключ неправильний - логер запише це і відправить на пошту
            logger.error("КРИТИЧНА ПОМИЛКА: Не вдалося завантажити флот з Firebase - {}", e.getMessage(), e);
        }
        return fleet;
    }

    public void addAirplane(@NonNull Airplane plane) {
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
            logger.info("Успішно додано/оновлено літак: {}", plane.getModel());
        } catch (Exception e) {
            logger.error("КРИТИЧНА ПОМИЛКА: Не вдалося зберегти літак {} - {}", plane.getModel(), e.getMessage(), e);
        }
    }

    public void deleteAirplane(String modelName) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.collection(COLLECTION_NAME).document(modelName).delete().get();
            logger.info("Успішно видалено літак: {}", modelName);
        } catch (Exception e) {
            logger.error("КРИТИЧНА ПОМИЛКА: Не вдалося видалити літак {} - {}", modelName, e.getMessage(), e);
        }
    }

    public void updateAirplane(String oldModelName, String newModel, String newManufacturer,
                               int newYear, double newSpeed, double newRange, double newFuel) {
        logger.info("Початок оновлення літака: {} -> {}", oldModelName, newModel);
        Airplane plane = getAirplaneByModel(oldModelName);
        if (plane != null) {
            plane.setModel(newModel);
            plane.setManufacturer(newManufacturer);
            plane.setYearOfManufacture(newYear);
            plane.setMaxSpeed(newSpeed);
            plane.setFlightRange(newRange);
            plane.setFuelConsumption(newFuel);

            if (!oldModelName.equals(newModel)) {
                deleteAirplane(oldModelName);
            }
            addAirplane(plane);
        } else {
            logger.warn("Спроба оновити неіснуючий літак: {}", oldModelName);
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
        String model = doc.getString("model") != null ? doc.getString("model") : doc.getId();
        String manufacturer = doc.getString("manufacturer");

        int year = doc.get("yearOfManufacture") != null ? ((Number) doc.get("yearOfManufacture")).intValue() : 2024;
        double speed = doc.get("maxSpeed") != null ? ((Number) doc.get("maxSpeed")).doubleValue() : 0;
        double range = doc.get("flightRange") != null ? ((Number) doc.get("flightRange")).doubleValue() : 0;
        double fuel = doc.get("fuelConsumption") != null ? ((Number) doc.get("fuelConsumption")).doubleValue() : 0;
        double capacity = doc.get("capacity") != null ? ((Number) doc.get("capacity")).doubleValue() : 0;
        int lux = doc.get("luxuryLevel") != null ? ((Number) doc.get("luxuryLevel")).intValue() : 5;

        return switch (type != null ? type : "") {
            case "PassengerAirplane" -> new PassengerAirplane(model, manufacturer, year, speed, range, fuel, (int) capacity);
            case "CargoAirplane" -> new CargoAirplane(model, manufacturer, year, speed, range, fuel, capacity);
            case "MilitaryAirplane" -> new MilitaryAirplane(model, manufacturer, year, speed, range, fuel, capacity);
            case "PrivateJet" -> new PrivateJet(model, manufacturer, year, speed, range, fuel, (int) capacity, lux);
            default -> {
                logger.warn("У базі знайдено літак з невідомим типом: {}. Використано тип за замовчуванням.", type);
                yield new PassengerAirplane(model, manufacturer, year, speed, range, fuel, (int) capacity);
            }
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
        logger.info("Виконано пошук літаків за пальним: від {} до {}", min, max);
        return getAllAirplanes().stream()
                .filter(p -> p.getFuelConsumption() >= min && p.getFuelConsumption() <= max)
                .collect(Collectors.toList());
    }

    public List<Airplane> getSortedFleet(String sortBy, String direction) {
        logger.info("Користувач відсортував флот за параметром: {}, напрямок: {}", sortBy, direction);
        Comparator<Airplane> comparator = switch (sortBy) {
            case "speed" -> Comparator.comparingDouble(Airplane::getMaxSpeed);
            case "fuel" -> Comparator.comparingDouble(Airplane::getFuelConsumption);
            default -> Comparator.comparingDouble(Airplane::getFlightRange);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        return getAllAirplanes().stream().sorted(comparator).collect(Collectors.toList());
    }
}