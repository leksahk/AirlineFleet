package com.airline.airlineweb.model;

public class CargoAirplane extends Airplane {
    private double payloadCapacity;

    // Обов'язковий порожній конструктор для Firebase
    public CargoAirplane() {}

    public CargoAirplane(String model, String manufacturer, int year, double maxSpeed,
                         double flightRange, double fuelConsumption, double payloadCapacity) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption);
        setPayloadCapacity(payloadCapacity);
    }

    public double getPayloadCapacity() { return payloadCapacity; }

    public void setPayloadCapacity(double capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Вантажопідйомність має бути > 0");
        this.payloadCapacity = capacity;
    }

    @Override
    public double calculateCapacity() {
        return payloadCapacity;
    }

    @Override
    public String toString() {
        return "Вантажний: " + super.toString() + " тонн";
    }
    @Override
    public String getPlaneType() { return "Вантажний (тонн)"; }
}