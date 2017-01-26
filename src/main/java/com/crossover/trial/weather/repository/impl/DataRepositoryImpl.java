package com.crossover.trial.weather.repository.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.repository.DataRepository;

public class DataRepositoryImpl implements DataRepository {

    private final Map<String, AtmosphericInformation> atmosphericInformation;

    private final Map<Airport, AtomicInteger> requestFrequency;

    private final Map<Double, AtomicInteger> radiusFrequency;

    private final Map<String, Airport> airportData;

    private DataRepositoryImpl() {
        atmosphericInformation = new ConcurrentHashMap<>(1000);
        requestFrequency = new ConcurrentHashMap<>();
        radiusFrequency = new ConcurrentHashMap<>();
        airportData = new ConcurrentHashMap<>();
    }

    private static final DataRepository INSTANCE = new DataRepositoryImpl();

    public static DataRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Map<Airport, AtomicInteger> getRequestFrequency() {
        return requestFrequency;
    }

    @Override
    public Map<Double, AtomicInteger> getRadiusFrequency() {
        return radiusFrequency;
    }

    @Override
    public Map<String, AtmosphericInformation> getAtmosphericInformation() {
        return atmosphericInformation;
    }

    @Override
    public Map<String, Airport> getAirportData() {
        return airportData;
    }

}
