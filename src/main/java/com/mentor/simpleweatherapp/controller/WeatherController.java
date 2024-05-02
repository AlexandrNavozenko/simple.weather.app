package com.mentor.simpleweatherapp.controller;

import com.mentor.simpleweatherapp.model.WeatherCountriesDto;
import com.mentor.simpleweatherapp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherCountriesDto> getWeather() {
        return ResponseEntity.ok(weatherService.getWeather());
    }
}
