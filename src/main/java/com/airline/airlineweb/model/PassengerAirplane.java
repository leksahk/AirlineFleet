package com.airline.airlineweb.model;

public class PassengerAirplane extends Airplane {
    private int passengers;

    public PassengerAirplane() {}

    public PassengerAirplane(String model, String manufacturer, int year, double maxSpeed,
                             double flightRange, double fuelConsumption, int passengers) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption);
        setPassengers(passengers);
    }

    public int getPassengers() { return passengers; }

    public void setPassengers(int passengers) {
        if (passengers < 1 || passengers > 1000)
            throw new IllegalArgumentException("Кількість місць має бути від 1 до 1000");
        this.passengers = passengers;
    }

    @Override
    public double calculateCapacity() {
        return passengers;
    }

    @Override
    public String toString() {
        return "Пасажирський: " + super.toString() + " | місць: " + passengers;
    }

    @Override
    public String getPlaneType() { return "Пасажирський (чол)"; }
}