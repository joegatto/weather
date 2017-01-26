package com.crossover.trial.weather.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;

public interface AirportService {

    Airport addAirport(Airport airport);

    Airport getAirport(String iataCode);

    void deleteAirport(String iata);

    Collection<Airport> getAllAirports();

    Set<String> getAllAirportCodes();

    Collection<AtmosphericInformation> getAllAtmosphericInformation();

    void updateAtmosphericInformation(String iataCode, String pointType, DataPoint dp) throws WeatherException;

    Map<Airport, AtomicInteger> getRequestCounts();

    Map<Double, AtomicInteger> getRadiusCounts();

    AtmosphericInformation getAtmosphericInformationByIataCode(String iataCode);

    AtomicInteger getRequestCount(String iataCode);

    void updateRequestFrequency(String iata, Double radius);

}
