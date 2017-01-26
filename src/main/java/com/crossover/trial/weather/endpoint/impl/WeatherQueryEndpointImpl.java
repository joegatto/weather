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
import com.crossover.trial.weather.utils.CoordinateHelper;
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
            if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPressure() != null || ai.getPrecipitation() != null || ai
                    .getTemperature() != null || ai.getWind() != null) {
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
            double frac = (double) airportService.getRequestCount(data.getIata()).get() / airportService.getRequestCounts().size();
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
        if (radius < 0) {
            radius = 0;
        }
        airportService.updateRequestFrequency(iata, radius);
        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            retval.add(airportService.getAtmosphericInformationByIataCode(iata));
        } else {
            Airport airport = airportService.getAirport(iata);
            for (Airport airport2 : airportService.getAllAirports()) {
                if (CoordinateHelper.calculateDistance(airport.getCoordinate(), airport2.getCoordinate()) <= radius) {
                    AtmosphericInformation ai = airportService.getAtmosphericInformationByIataCode(airport2.getIata());
                    if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null || ai.getPressure() != null || ai
                            .getTemperature() != null || ai.getWind() != null) {
                        retval.add(ai);
                    }
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }

}
