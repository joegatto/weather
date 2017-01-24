package com.crossover.trial.weather.endpoint.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.endpoint.WeatherCollectorEndpoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author Joao Gatto
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {

    public static final Logger LOGGER = Logger.getLogger(WeatherCollectorEndpointImpl.class.getName());

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();

    private AirportService airportService = new AirportServiceImpl();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") final String iataCode,
            @PathParam("pointType") final String pointType, final String datapointJson) {
        try {
            addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response getAirports() {
        Set<String> retval = new HashSet<>();
        for (Airport ad : airportService.getAllAirports()) {
            retval.add(ad.getIata());
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }

    @Override
    public Response getAirport(@PathParam("iata") final String iata) {
        Airport ad = airportService.getAirport(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }

    @Override
    public Response addAirport(@PathParam("iata") final String iata, @PathParam("lat") final String latString,
            @PathParam("long") final String longString) {
        addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response deleteAirport(@PathParam("iata") final String iata) {
        airportService.deleteAirport(iata);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode
     *            the 3 letter IATA code
     * @param pointType
     *            the point type {@link DataPointType}
     * @param dp
     *            a datapoint object holding pointType data
     *
     * @throws WeatherException
     *             if the update can not be completed
     */
    private void addDataPoint(final String iataCode, final String pointType, final DataPoint dp)
            throws WeatherException {
        AtmosphericInformation ai = airportService.getAtmosphericInformationByIataCode(iataCode);
        updateAtmosphericInformation(ai, pointType, dp);
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
    private void updateAtmosphericInformation(final AtmosphericInformation ai, final String pointType,
            final DataPoint dp) throws WeatherException {

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= DataPointType.WIND.getMin()) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= DataPointType.TEMPERATURE.getMin()
                    && dp.getMean() < DataPointType.TEMPERATURE.getMax()) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.HUMIDITY.name())) {
            if (dp.getMean() >= DataPointType.HUMIDITY.getMin() && dp.getMean() < DataPointType.HUMIDITY.getMax()) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= DataPointType.PRESSURE.getMin() && dp.getMean() < DataPointType.PRESSURE.getMax()) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= DataPointType.CLOUDCOVER.getMin() && dp.getMean() < DataPointType.CLOUDCOVER.getMax()) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >= DataPointType.PRECIPITATION.getMin()
                    && dp.getMean() < DataPointType.PRECIPITATION.getMax()) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        throw new IllegalStateException("couldn't update atmospheric data");
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode
     *            3 letter code
     * @param latitude
     *            in degrees
     * @param longitude
     *            in degrees
     *
     * @return the added airport
     */
    private Airport addAirport(final String iataCode, final double latitude, final double longitude) {
        Airport ad = new Airport.Builder().withIata(iataCode).withLatitude(latitude).withLongitude(longitude).build();
        AtmosphericInformation ai = new AtmosphericInformation.Builder().build();
        airportService.getAllAtmosphericInformation().add(ai);
        return airportService.addAirport(ad);
    }
}
