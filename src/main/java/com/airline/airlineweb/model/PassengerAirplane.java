package com.airline.airlineweb.model;

public class PassengerAirplane extends Airplane {
    private int passengerSeats;

    // Обов'язковий порожній конструктор для Firebase
    public PassengerAirplane() {}

    public PassengerAirplane(String model, String manufacturer, int year, double maxSpeed,
                             double flightRange, double fuelConsumption, int passengerSeats) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption);
        setPassengerSeats(passengerSeats);
    }

    public int getPassengerSeats() { return passengerSeats; }

    public void setPassengerSeats(int seats) {
        if (seats < 1 || seats > 1000)
            throw new IllegalArgumentException("Кількість місць має бути від 1 до 1000");
        this.passengerSeats = seats;
    }

    @Override
    public double calculateCapacity() {
        return passengerSeats;
    }

    @Override
    public String toString() {
        return "Пасажирський: " + super.toString() + " місць";
    }
    @Override
    public String getPlaneType() { return "Пасажирський (чол)"; }
}