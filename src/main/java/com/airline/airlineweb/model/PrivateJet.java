package com.airline.airlineweb.model;

public class PrivateJet extends PassengerAirplane {
    private int luxuryLevel;

    public PrivateJet() {}

    public PrivateJet(String model, String manufacturer, int year, double maxSpeed,
                      double flightRange, double fuelConsumption, int passengers, int luxuryLevel) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption, passengers);

        if (passengers < 1 || passengers > 10) {
            throw new IllegalArgumentException("Кількість пасажирів у приватному джеті: 1-10");
        }

        setLuxuryLevel(luxuryLevel);
    }

    public int getLuxuryLevel() { return luxuryLevel; }

    public void setLuxuryLevel(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("Рівень розкоші: 1-5");
        this.luxuryLevel = level;
    }

    @Override
    public String getPlaneType() {
        return "Приватний джет";
    }

    @Override
    public String toString() {
        return String.format("%s | Рівень розкоші: %d/5", super.toString(), luxuryLevel);
    }
}