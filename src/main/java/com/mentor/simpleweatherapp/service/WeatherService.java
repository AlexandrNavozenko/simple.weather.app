package com.mentor.simpleweatherapp.service;

import com.mentor.simpleweatherapp.model.WeatherCountriesDto;
import com.mentor.simpleweatherapp.model.WeatherCountryDto;
import com.mentor.simpleweatherapp.model.WeatherDetailDto;
import com.mentor.simpleweatherapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            Future<WeatherDetailDto> t4 = executorService.submit(new WeatherServiceTask("Krakow", 100));

            Optional<WeatherDetailDto> weatherKyiv = getFuture(t1);
            Optional<WeatherDetailDto> weatherLondon = getFuture(t2);
            Optional<WeatherDetailDto> weatherParis = getFuture(t3, 5);
            Optional<WeatherDetailDto> weatherKrakow = getFuture(t4, 20);

            List<WeatherCountryDto> list = new ArrayList<>();
            weatherKyiv.ifPresent(weatherDetailDto -> list.add(populateWeatherCountryDto(weatherDetailDto)));
            weatherLondon.ifPresent(weatherDetailDto -> list.add(populateWeatherCountryDto(weatherDetailDto)));
            weatherParis.ifPresent(weatherDetailDto -> list.add(populateWeatherCountryDto(weatherDetailDto)));
            weatherKrakow.ifPresent(weatherDetailDto -> list.add(populateWeatherCountryDto(weatherDetailDto)));

            return new WeatherCountriesDto(list);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<WeatherDetailDto> getFuture(Future<WeatherDetailDto> call)
            throws ExecutionException, InterruptedException {
        return Optional.ofNullable(call.get());
    }

    private Optional<WeatherDetailDto> getFuture(Future<WeatherDetailDto> call, long timeOut)
            throws ExecutionException, InterruptedException {
        try {
            return Optional.ofNullable(call.get(timeOut, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            System.out.println("Timeout expired");
        }

        return Optional.empty();
    }

    private WeatherCountryDto populateWeatherCountryDto(final WeatherDetailDto weather) {
        return new WeatherCountryDto(weather.sys().country(), weather.name(),
                Util.round(weather.main().temp() - ZERO_KELVIN_DEG));
    }
}
