package com.mentor.simpleweatherapp.service;

import com.mentor.simpleweatherapp.model.WeatherCountriesDto;
import com.mentor.simpleweatherapp.model.WeatherCountryDto;
import com.mentor.simpleweatherapp.model.WeatherDetailDto;
import com.mentor.simpleweatherapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final double ZERO_KELVIN_DEG = 273.15;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public WeatherCountriesDto getWeather() {
        return getWeather("Kyiv", "London", "Paris", "Krakow");
    }

    public WeatherCountriesDto getWeather(String... cities) {
        Function<String, CompletableFuture<WeatherServiceTask>> stringCompletableFutureFunction = city
                -> CompletableFuture.supplyAsync(() -> new WeatherServiceTask(city), executorService)
                                    .orTimeout(5, TimeUnit.SECONDS)
                                    .exceptionally(e -> {
                                        System.out.println("Timeout expired");

                                        return new WeatherServiceTask();
                                    });

        List<CompletableFuture<WeatherServiceTask>> listCompletableFuture = Arrays.stream(cities)
                .map(stringCompletableFutureFunction)
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(listCompletableFuture.toArray(new CompletableFuture[0]));
        var allWeatherCountriesFuture = allFutures.thenApply(v -> listCompletableFuture.stream()
                    .map(CompletableFuture::join)
                    .toList());

        try {
            return allWeatherCountriesFuture.thenApply(this::initWeatherCountriesDtos).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherCountriesDto initWeatherCountriesDtos(List<WeatherServiceTask> tasks) {
        List<WeatherCountryDto> list = tasks.stream()
                .map(this::getCall)
                .filter(Optional::isPresent)
                .map(optional -> populateWeatherCountryDto(optional.get()))
                .toList();

        return new WeatherCountriesDto(list);
    }

    private Optional<WeatherDetailDto> getCall(WeatherServiceTask task) {
        try {
            return task.call();
        } catch (Exception e) {
            System.out.println("Exception: " + e);

            return Optional.empty();
        }
    }

    private WeatherCountryDto populateWeatherCountryDto(final WeatherDetailDto weather) {
        return new WeatherCountryDto(weather.sys().country(), weather.name(),
                Util.round(weather.main().temp() - ZERO_KELVIN_DEG));
    }
}
