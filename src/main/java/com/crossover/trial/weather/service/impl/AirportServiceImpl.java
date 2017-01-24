package com.crossover.trial.weather.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.repository.DataRepository;
import com.crossover.trial.weather.repository.impl.DataRepositoryImpl;
import com.crossover.trial.weather.service.AirportService;

public class AirportServiceImpl implements AirportService {

	private DataRepository dataRepository = DataRepositoryImpl.getInstance();

	@Override
	public Airport addAirport(final Airport airport) {
		return dataRepository.getAirportData().put(airport.getIata(), airport);
	}

	@Override
	public Airport getAirport(final String iataCode) {
		return dataRepository.getAirportData().get(iataCode);
	}

	@Override
	public Collection<Airport> getAllAirports() {
		return dataRepository.getAirportData().values();
	}

	@Override
	public Collection<AtmosphericInformation> getAllAtmosphericInformation() {
		return Collections.unmodifiableCollection(dataRepository.getAtmosphericInformation().values());
	}

	@Override
	public Map<String, AtomicInteger> getRequestCounts() {
		return Collections.unmodifiableMap(dataRepository.getRequestFrequency());
	}

	@Override
	public Map<Double, AtomicInteger> getRadiusCounts() {
		return Collections.unmodifiableMap(dataRepository.getRadiusFrequency());
	}

	@Override
	public AtmosphericInformation getAtmosphericInformationByIataCode(final String iataCode) {
		return dataRepository.getAtmosphericInformation().getOrDefault(iataCode,
				new AtmosphericInformation.Builder().build());
	}

	@Override
	public AtomicInteger getRequestCount(final String iataCode) {
		return dataRepository.getRequestFrequency().getOrDefault(iataCode, new AtomicInteger(0));
	}

	@Override
	public void updateRequestCount(final String iataCode, final int count) {
		dataRepository.getRequestFrequency().put(iataCode, new AtomicInteger(count));
	}

	@Override
	public void updateRadiusCount(final Double radius, final int count) {
		dataRepository.getRadiusFrequency().put(radius, new AtomicInteger(count));
	}

	@Override
	public AtomicInteger getRadiusCount(final Double radius) {
		return dataRepository.getRadiusFrequency().getOrDefault(radius, new AtomicInteger(0));
	}

	@Override
	public boolean airportExists(final String iataCode) {
		return dataRepository.getAirportData().containsKey(iataCode);
	}

	@Override
	public void deleteAirport(final String iata) {
		dataRepository.getAirportData().remove(iata);
	}

}
