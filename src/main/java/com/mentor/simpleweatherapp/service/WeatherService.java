package com.mentor.simpleweatherapp.service;

import com.mentor.simpleweatherapp.model.WeatherCountriesDto;
import com.mentor.simpleweatherapp.model.WeatherCountryDto;
import com.mentor.simpleweatherapp.model.WeatherDetailDto;
import com.mentor.simpleweatherapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final double ZERO_KELVIN_DEG = 273.15;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public WeatherCountriesDto getWeather() {
        try {
            Future<WeatherDetailDto> t1 = executorService.submit(new WeatherServiceTask("Kyiv"));
            Future<WeatherDetailDto> t2 = executorService.submit(new WeatherServiceTask("London"));
            Future<WeatherDetailDto> t3 = executorService.submit(new WeatherServiceTask("Paris"));
            Future<WeatherDetailDto> t4 = executorService.submit(new WeatherServiceTask("Krakow"));

            WeatherDetailDto weatherKyiv = t1.get();
            WeatherDetailDto weatherLondon = t2.get();
            WeatherDetailDto weatherParis = t3.get();
            WeatherDetailDto weatherKrakow = t4.get();

            List<WeatherCountryDto> list = new CopyOnWriteArrayList<>();
            list.add(populateWeatherCountryDto(weatherKyiv));
            list.add(populateWeatherCountryDto(weatherLondon));
            list.add(populateWeatherCountryDto(weatherParis));
            list.add(populateWeatherCountryDto(weatherKrakow));

            return new WeatherCountriesDto(list);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherCountryDto populateWeatherCountryDto(final WeatherDetailDto weather) {
        return new WeatherCountryDto(weather.sys().country(), weather.name(),
                Util.round(weather.main().temp() - ZERO_KELVIN_DEG));
    }
}
