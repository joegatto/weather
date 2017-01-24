package com.crossover.trial.weather.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;

public interface AirportService {

	Airport addAirport(Airport airport);

	Airport getAirport(String iataCode);

	boolean airportExists(String iataCode);

	void deleteAirport(String iata);

	Collection<Airport> getAirports();

	Collection<AtmosphericInformation> getAllAtmosphericInformation();

	Map<String, AtomicInteger> getRequestCounts();

	Map<Double, AtomicInteger> getRadiusCounts();

	AtmosphericInformation getAtmosphericInformationByIataCode(String iataCode);

	void updateRequestCount(String iataCode, int count);

	AtomicInteger getRequestCount(String iataCode);

	void updateRadiusCount(Double radius, int count);

	AtomicInteger getRadiusCount(Double radius);

}
