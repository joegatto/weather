package com.crossover.trial.weather.repository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;

public interface DataRepository {

    Map<Airport, AtomicInteger> getRequestFrequency();

    Map<Double, AtomicInteger> getRadiusFrequency();

    Map<String, AtmosphericInformation> getAtmosphericInformation();

    Map<String, Airport> getAirportData();

}
