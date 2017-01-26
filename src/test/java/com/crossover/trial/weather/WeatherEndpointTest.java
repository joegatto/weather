package com.crossover.trial.weather;

import com.crossover.trial.weather.endpoint.WeatherCollectorEndpoint;
import com.crossover.trial.weather.endpoint.WeatherQueryEndpoint;
import com.crossover.trial.weather.endpoint.impl.WeatherCollectorEndpointImpl;
import com.crossover.trial.weather.endpoint.impl.WeatherQueryEndpointImpl;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.repository.DataRepository;
import com.crossover.trial.weather.repository.impl.DataRepositoryImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint query = new WeatherQueryEndpointImpl();

    private WeatherCollectorEndpoint update = new WeatherCollectorEndpointImpl();

    private DataRepository repository = DataRepositoryImpl.getInstance();

    private Gson gson = new Gson();

    private DataPoint dataPoint;

    @Before
    public void setUp() throws Exception {
        repository.getAirportData().clear();
        repository.getAtmosphericInformation().clear();
        repository.getRadiusFrequency().clear();
        repository.getRequestFrequency().clear();
        update.addAirport("BOS", "42.364347", "-71.005181");
        update.addAirport("EWR", "40.6925", "-74.168667");
        update.addAirport("JFK", "40.639751", "-73.778925");
        update.addAirport("LGA", "40.777245", "-73.872608");
        update.addAirport("MMU", "40.79935", "-74.4148747");
        dataPoint = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        update.updateWeather("BOS", "wind", gson.toJson(dataPoint));
        query.weather("BOS", "0").getEntity();
    }

    @Test
    public void testPing() throws Exception {
        String ping = query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), dataPoint);
    }

    @Test
    public void testGetNearby() throws Exception {
        update.updateWeather("JFK", "wind", gson.toJson(dataPoint));
        dataPoint.setMean(40);
        update.updateWeather("EWR", "wind", gson.toJson(dataPoint));
        dataPoint.setMean(30);
        update.updateWeather("LGA", "wind", gson.toJson(dataPoint));
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
    }

    @Test
    public void testUpdate() throws Exception {
        DataPoint windDp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        update.updateWeather("BOS", "wind", gson.toJson(windDp));
        query.weather("BOS", "0").getEntity();
        String ping = query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        DataPoint cloudCoverDp = new DataPoint.Builder().withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        update.updateWeather("BOS", "cloudcover", gson.toJson(cloudCoverDp));
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

}
