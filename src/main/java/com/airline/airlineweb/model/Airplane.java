package com.airline.airlineweb.model;

public abstract class Airplane {
    private String model;
    private String manufacturer;
    private int yearOfManufacture;
    private double maxSpeed;
    private double flightRange;
    private double fuelConsumption;

    // Обов'язковий порожній конструктор для Firebase
    public Airplane() {}

    public Airplane(String model, String manufacturer, int yearOfManufacture,
                    double maxSpeed, double flightRange, double fuelConsumption) {
        setModel(model);
        setManufacturer(manufacturer);
        setYearOfManufacture(yearOfManufacture);
        setMaxSpeed(maxSpeed);
        setFlightRange(flightRange);
        setFuelConsumption(fuelConsumption);
    }

    public String getModel() { return model; }
    public String getManufacturer() { return manufacturer; }
    public int getYearOfManufacture() { return yearOfManufacture; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getFlightRange() { return flightRange; }
    public double getFuelConsumption() { return fuelConsumption; }


    public void setModel(String model) {
        if (model == null || model.trim().isEmpty())
            throw new IllegalArgumentException("Модель не може бути порожньою");
        this.model = model;
    }

    public void setManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.trim().isEmpty())
            throw new IllegalArgumentException("Виробник не може бути порожнім");
        this.manufacturer = manufacturer;
    }

    public void setYearOfManufacture(int year) {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1903 || year > currentYear)
            throw new IllegalArgumentException("Рік випуску має бути від 1903 до " + currentYear);
        this.yearOfManufacture = year;
    }

    public void setMaxSpeed(double speed) {
        if (speed <= 0) throw new IllegalArgumentException("Максимальна швидкість має бути > 0");
        this.maxSpeed = speed;
    }

    public void setFlightRange(double range) {
        if (range <= 0) throw new IllegalArgumentException("Дальність польоту має бути > 0");
        this.flightRange = range;
    }

    public void setFuelConsumption(double consumption) {
        if (consumption <= 0) throw new IllegalArgumentException("Витрати пального мають бути > 0");
        this.fuelConsumption = consumption;
    }

    public abstract double calculateCapacity();

    @Override
    public String toString() {
        return String.format("%s [%s, %d] | %.0f км/год | %.0f км | %.2f л/км | Місткість: %.2f",
                model, manufacturer, yearOfManufacture, maxSpeed, flightRange, fuelConsumption, calculateCapacity());
    }
    public abstract String getPlaneType();
}