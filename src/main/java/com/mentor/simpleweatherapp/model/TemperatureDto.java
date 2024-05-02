package com.mentor.simpleweatherapp.model;

public record TemperatureDto(Double temp, Double feels_like, Double temp_min, Double temp_max, Double pressure, Double humidity) {
}
