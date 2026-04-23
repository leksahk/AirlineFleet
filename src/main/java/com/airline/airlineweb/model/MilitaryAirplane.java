package com.airline.airlineweb.model;

public class MilitaryAirplane extends Airplane {
    private double weaponLoad;

    public MilitaryAirplane() {}

    public MilitaryAirplane(String model, String manufacturer, int year, double maxSpeed,
                            double flightRange, double fuelConsumption, double weaponLoad) {
        super(model, manufacturer, year, maxSpeed, flightRange, fuelConsumption);
        setWeaponLoad(weaponLoad);
    }

    public double getWeaponLoad() { return weaponLoad; }

    public void setWeaponLoad(double load) {
        if (load < 0) throw new IllegalArgumentException("Вантаж боєприпасів >= 0");
        this.weaponLoad = load;
    }

    @Override
    public double calculateCapacity() {
        return weaponLoad;
    }

    @Override
    public String toString() {
        return "Військовий: " + super.toString() + " тонн озброєння";
    }

    @Override
    public String getPlaneType() { return "Військовий (тонн озброєння)"; }
}