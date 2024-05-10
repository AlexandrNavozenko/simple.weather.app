package com.mentor.simpleweatherapp.service;

import com.mentor.simpleweatherapp.model.WeatherDetailDto;
import com.mentor.simpleweatherapp.util.Util;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Callable;

public class WeatherServiceTask implements Callable<Optional<WeatherDetailDto>> {

    private static final String OPEN_WEATHER_URI = "https://api.openweathermap.org/data/2.5/weather";

    private static final String OPEN_WEATHER_API_KEY = "APPID";

    private static final String OPEN_WEATHER_API_ID = "188c193b356ef74dc13a886f1a841434";

    private static final String OPEN_WEATHER_COUNTRY_KEY = "q";

    private final RestClient restClient = RestClient.create();

    private final String weatherCountry;

    private final Integer sleepTime;

    public WeatherServiceTask() {
        this.weatherCountry = "";
        this.sleepTime = 1;
    }

    public WeatherServiceTask(String weatherCountry) {
        this.weatherCountry = weatherCountry;
        this.sleepTime = 10;
    }

    @Override
    public Optional<WeatherDetailDto> call() throws Exception {
        if (weatherCountry.isEmpty()) {
            return Optional.empty();
        }

        Util.sleepSecond(sleepTime);
        WeatherDetailDto body = restClient.get()
                .uri(getUri())
                .retrieve()
                .toEntity(WeatherDetailDto.class)
                .getBody();

        System.out.println(body.sys().country());

        return Optional.ofNullable(body);
    }

    private URI getUri() {
        return UriComponentsBuilder.fromHttpUrl(OPEN_WEATHER_URI)
                .queryParam(OPEN_WEATHER_COUNTRY_KEY, weatherCountry)
                .queryParam(OPEN_WEATHER_API_KEY, OPEN_WEATHER_API_ID)
                .build()
                .toUri();
    }
}
