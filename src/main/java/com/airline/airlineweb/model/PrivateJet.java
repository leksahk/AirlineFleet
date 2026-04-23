package com.airline.airlineweb.model;

public class PrivateJet extends Airplane {
    private int passengers;
    private int luxuryLevel;

    public PrivateJet() {}

    public PrivateJet(String model, String manufacturer, int year, double maxSpeed,
                      double flightRange, double fuelConsumption, int passengers, int luxuryLevel) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption);
        setPassengers(passengers);
        setLuxuryLevel(luxuryLevel);
    }

    public int getPassengers() { return passengers; }

    public void setPassengers(int p) {
        if (p < 1 || p > 10) throw new IllegalArgumentException("Кількість пасажирів: 1-10");
        this.passengers = p;
    }

    public int getLuxuryLevel() { return luxuryLevel; }

    public void setLuxuryLevel(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("Рівень розкоші: 1-5");
        this.luxuryLevel = level;
    }

    @Override
    public double calculateCapacity() {
        return passengers;
    }

    @Override
    public String toString() {
        return String.format("Приватний джет: %s | %d пасажирів | Розкіш: %d", super.toString(), passengers, luxuryLevel);
    }

    @Override
    public String getPlaneType() { return "Приватний джет (чол)"; }
}