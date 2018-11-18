package com.example.divided.nfcreader.model;

import com.example.divided.nfcreader.Utils;

public class Temp {
    private float Celsius;
    private float Kelvin;
    private float Fahrenheit;

    public Temp(float averageCelsius) {
        this.Celsius = averageCelsius;
        this.Kelvin = Utils.celsiusToKelvin(averageCelsius);
        this.Fahrenheit = Utils.celsiusToFahrenheit(averageCelsius);
    }

    public float getCelsius() {
        return Celsius;
    }

    public float getKelvin() {
        return Kelvin;
    }

    public float getFahrenheit() {
        return Fahrenheit;
    }
}