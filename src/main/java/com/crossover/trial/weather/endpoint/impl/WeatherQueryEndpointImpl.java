package com.crossover.trial.weather.endpoint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.endpoint.WeatherQueryEndpoint;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check
 * health stats. Currently, all data is held in memory. The end point deploys to
 * a single container
 *
 * @author Joao Gatto
 */
@Path("/query")
public class WeatherQueryEndpointImpl implements WeatherQueryEndpoint {

    public static final Logger LOGGER = Logger.getLogger(WeatherQueryEndpointImpl.class.getName());

    /** earth radius in KM */
    public static final double R = 6372.8;

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();

    private AirportService airportService = new AirportServiceImpl();

    /**
     * Retrieve service health including total size of valid data points and
     * request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        for (AtmosphericInformation ai : airportService.getAllAtmosphericInformation()) {
            // we only count recent readings
            if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPressure() != null
                    || ai.getPrecipitation() != null || ai.getTemperature() != null || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (Airport data : airportService.getAllAirports()) {
            double frac = (double) airportService.getRequestCount(data.getIata()).get()
                    / airportService.getRequestCounts().size();
            freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);

        int m = airportService.getRadiusCounts().keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, AtomicInteger> e : airportService.getRadiusCounts().entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue().get();
        }
        retval.put("radius_freq", hist);

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the
     * requested airport information and return a list of matching atmosphere
     * information.
     *
     * @param iata
     *            the iataCode
     * @param radiusString
     *            the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(final String iata, final String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            retval.add(airportService.getAtmosphericInformationByIataCode(iata));
        } else {
            Airport airport = findAirport(iata);
            for (Airport a : airportService.getAllAirports()) {
                if (calculateDistance(airport, a) <= radius) {
                    AtmosphericInformation ai = airportService.getAtmosphericInformationByIataCode(a.getIata());
                    if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                            || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null) {
                        retval.add(ai);
                    }
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
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
        Airport airport = findAirport(iata);
        airportService.getRequestCounts().put(airport.getIata(),
                new AtomicInteger(airportService.getRequestCount(airport.getIata()).incrementAndGet()));
        airportService.getRadiusCounts().put(radius, airportService.getRadiusCount(radius));
    }

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode
     *            as a string
     * @return airport data or null if not found
     */
    private Airport findAirport(final String iataCode) {
        return airportService.getAllAirports().stream().filter(ap -> ap.getIata().equals(iataCode)).findFirst()
                .orElse(null);
    }

    /**
     * Haversine distance between two airports.
     *
     * @param airport1
     *            airport 1
     * @param airport2
     *            airport 2
     * @return the distance in KM
     */
    public double calculateDistance(final Airport airport1, final Airport airport2) {
        double deltaLat = Math.toRadians(airport2.getLatitude() - airport1.getLatitude());
        double deltaLon = Math.toRadians(airport2.getLongitude() - airport1.getLongitude());
        double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(airport1.getLatitude()) * Math.cos(airport2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    /**
     * A dummy init method that loads hard coded data
     */
    public void init() {
        airportService.addAirport(
                new Airport.Builder().withIata("BOS").withLatitude(42.364347).withLongitude(-71.005181).build());
        airportService.addAirport(
                new Airport.Builder().withIata("EWR").withLatitude(40.6925).withLongitude(-74.168667).build());
        airportService.addAirport(
                new Airport.Builder().withIata("JFK").withLatitude(40.639751).withLongitude(-73.778925).build());
        airportService.addAirport(
                new Airport.Builder().withIata("LGA").withLatitude(40.777245).withLongitude(-73.872608).build());
        airportService.addAirport(
                new Airport.Builder().withIata("MMU").withLatitude(40.79935).withLongitude(-74.4148747).build());
    }

}
