package com.crossover.trial.weather.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.repository.DataRepository;
import com.crossover.trial.weather.repository.impl.DataRepositoryImpl;
import com.crossover.trial.weather.service.AirportService;

public class AirportServiceImpl implements AirportService {

    private static final Logger LOGGER = Logger.getLogger(AirportServiceImpl.class.getName());

    private DataRepository dataRepository = DataRepositoryImpl.getInstance();

    @Override
    public void addAirport(final Airport airport) {
        if (airport == null || airport.getIata() == null) {
            LOGGER.severe("Cannot save airport");
            return;
        }
        dataRepository.getAirportData().put(airport.getIata(), airport);
    }

    @Override
    public Airport getAirport(final String iataCode) {
        if (iataCode == null) {
            LOGGER.severe("iataCode is null");
            return null;
        }
        return dataRepository.getAirportData().get(iataCode);
    }

    @Override
    public Collection<Airport> getAllAirports() {
        return dataRepository.getAirportData().values();
    }

    public Set<String> getAllAirportCodes() {
        return dataRepository.getAirportData().keySet();
    }

    @Override
    public Collection<AtmosphericInformation> getAllAtmosphericInformation() {
        return Collections.unmodifiableCollection(dataRepository.getAtmosphericInformation().values());
    }

    @Override
    public Map<Airport, AtomicInteger> getRequestCounts() {
        return Collections.unmodifiableMap(dataRepository.getRequestFrequency());
    }

    @Override
    public Map<Double, AtomicInteger> getRadiusCounts() {
        return Collections.unmodifiableMap(dataRepository.getRadiusFrequency());
    }

    @Override
    public AtmosphericInformation getAtmosphericInformationByIataCode(final String iataCode) {
        return dataRepository.getAtmosphericInformation().getOrDefault(iataCode, new AtmosphericInformation.Builder().build());
    }

    @Override
    public AtomicInteger getRequestCount(final String iataCode) {
        if (iataCode == null) {
            LOGGER.severe("iataCode is null");
            return null;
        }
        return dataRepository.getRequestFrequency().getOrDefault(iataCode, new AtomicInteger(0));
    }

    @Override
    public void deleteAirport(final String iata) {
        if (iata == null) {
            LOGGER.severe("Cannot delete atmospheric information");
            return;
        }
        dataRepository.getAirportData().remove(iata);
    }

    public void updateAtmosphericInformation(final String iataCode, final String pointType, final DataPoint dp) throws WeatherException {
        if (iataCode == null) {
            throw new IllegalArgumentException("IATA code is null");
        }
        if (dataRepository.getAirportData().get(iataCode) == null) {
            throw new IllegalArgumentException("Unknown IATA code");
        }
        if (pointType == null) {
            throw new IllegalArgumentException("pointType is null");
        }
        if (dp == null) {
            throw new IllegalArgumentException("DataPoint is null");
        }

        AtmosphericInformation oldValue = getAtmosphericInformationByIataCode(iataCode);
        dataRepository.getAtmosphericInformation().putIfAbsent(iataCode, oldValue);

        AtmosphericInformation newValue = new AtmosphericInformation.Builder().build();
        updateAtmosphericInformation(newValue, pointType, dp);
        dataRepository.getAtmosphericInformation().replace(iataCode, oldValue, newValue);
    }

    /**
     * update atmospheric information with the given data point for the given
     * point type
     *
     * @param ai
     *            the atmospheric information object to update
     * @param pointType
     *            the data point type as a string
     * @param dp
     *            the actual data point
     */
    private void updateAtmosphericInformation(final AtmosphericInformation ai, final String pointType, final DataPoint dp) throws WeatherException {
        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (DataPointType.WIND.validate(dp)) {
                ai.setWind(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (DataPointType.TEMPERATURE.validate(dp)) {
                ai.setTemperature(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.HUMIDITY.name())) {
            if (DataPointType.HUMIDITY.validate(dp)) {
                ai.setHumidity(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (DataPointType.PRESSURE.validate(dp)) {
                ai.setPressure(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (DataPointType.CLOUDCOVER.validate(dp)) {
                ai.setCloudCover(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        } else if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (DataPointType.PRECIPITATION.validate(dp)) {
                ai.setPrecipitation(dp);
            } else {
                throw new IllegalArgumentException("Wrong parameter " + pointType + " = " + dp.getMean());
            }
        }
        ai.setLastUpdateTime(System.currentTimeMillis());
    }

    /**
     * Records information about how often requests are made
     *
     * @param iata
     *            an iata code
     * @param radius
     *            query radius
     */
    public void updateRequestFrequency(final String iata, final Double radius) {
        Airport airport = dataRepository.getAirportData().get(iata);
        double r = radius.doubleValue();

        if (airport != null) {
            AtomicInteger i = dataRepository.getRequestFrequency().get(airport);
            if (i == null) {
                dataRepository.getRequestFrequency().putIfAbsent(airport, new AtomicInteger(0));
                i = dataRepository.getRequestFrequency().get(airport);

            }
            i.incrementAndGet();

            if (radius < 0) {
                r = 0d;
            }
            if (radius > 1000) {
                r = 1000d;
            }

            i = dataRepository.getRadiusFrequency().get(r);
            if (i == null) {
                dataRepository.getRadiusFrequency().putIfAbsent(r, new AtomicInteger(0));
                i = dataRepository.getRadiusFrequency().get(r);
            }
            i.incrementAndGet();
        }
    }

}
