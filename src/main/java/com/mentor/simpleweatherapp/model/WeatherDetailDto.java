package com.mentor.simpleweatherapp.model;

import java.util.List;

public record WeatherDetailDto(CoordinateDto coord,
                               List<WeatherDto> weather,
                               String base,
                               TemperatureDto main,
                               Integer visibility,
                               WindDto wind,
                               CloudDto clouds,
                               Long dt,
                               SysDto sys,
                               Integer timezone,
                               Long id,
                               String name,
                               Integer cod) {
}
