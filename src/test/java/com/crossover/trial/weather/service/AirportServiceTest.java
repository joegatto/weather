package com.crossover.trial.weather.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.repository.DataRepository;
import com.crossover.trial.weather.repository.impl.DataRepositoryImpl;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;

/**
 * Base class for service testing
 * 
 * @author Joao Gatto
 */
public class AirportServiceTest {

    @Resource
    private AirportService airportService = new AirportServiceImpl();

    private DataRepository repository = DataRepositoryImpl.getInstance();

    @Before
    public void setUp() throws Exception {
        repository.getAirportData().clear();
        repository.getAtmosphericInformation().clear();
        repository.getRadiusFrequency().clear();
        repository.getRequestFrequency().clear();
    }

    @Test
    public void testFindAirport() {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(2.0).build();
        airportService.addAirport(ad);
        ad = airportService.getAirport("AAA");
        assertNotNull(ad);
        assertEquals(ad.getIata(), "AAA");
        assertTrue(ad.getCoordinate().getLatitude() == 1);
        assertTrue(ad.getCoordinate().getLongitude() == 2);
    }

    @Test
    public void testFindAirportNull() {
        Airport ad = airportService.getAirport(null);
        assertNull(ad);
    }

    @Test
    public void testFindAirportUnknown() {
        Airport ad = airportService.getAirport("");
        assertNull(ad);
    }

    @Test
    public void testGetAllAirportCodes() {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(1.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("AAA"));
        ad = new Airport.Builder().withIata("BBB").withLatitude(2.0).withLongitude(2.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("BBB"));
        ad = new Airport.Builder().withIata("CCC").withLatitude(3.0).withLongitude(3.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("CCC"));
        Set<String> result = airportService.getAllAirportCodes();
        assertTrue(result.size() == 3);
    }

    @Test
    public void testFindAtmosphericInformationNull() {
        AtmosphericInformation result = airportService.getAtmosphericInformationByIataCode(null);
        assertNull(result);
    }

    @Test
    public void testFindAtmosphericInformationUnknown() {
        AtmosphericInformation result = airportService.getAtmosphericInformationByIataCode("");
        assertEquals(new AtmosphericInformation.Builder().build(), result);
    }

    @Test
    public void testUpdateAtmosphericInformation() throws WeatherException {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(1.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("AAA"));
        airportService.updateAtmosphericInformation("AAA", DataPointType.WIND.name(),
                new DataPoint.Builder().withFirst(10).withMedian(20).withMean(30).withLast(40).withCount(50).build());
        assertNotNull(airportService.getAtmosphericInformationByIataCode("AAA"));
        AtmosphericInformation ai = airportService.getAtmosphericInformationByIataCode("AAA");
        assertTrue(ai.getWind().getFirst() == 10);
        assertTrue(ai.getWind().getMedian() == 20);
        assertTrue(ai.getWind().getMean() == 30);
        assertTrue(ai.getWind().getLast() == 40);
        assertTrue(ai.getWind().getCount() == 50);
        airportService.updateAtmosphericInformation("AAA", "wind",
                new DataPoint.Builder().withFirst(50).withMedian(40).withMean(30).withLast(20).withCount(10).build());
        ai = airportService.getAtmosphericInformationByIataCode("AAA");
        assertTrue(ai.getWind().getFirst() == 50);
        assertTrue(ai.getWind().getMedian() == 40);
        assertTrue(ai.getWind().getMean() == 30);
        assertTrue(ai.getWind().getLast() == 20);
        assertTrue(ai.getWind().getCount() == 10);
    }

    @Test
    public void testUpdateAtmosphericInformationNull() throws WeatherException {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(1.0).build();
        airportService.addAirport(ad);
        IllegalArgumentException exception = null;
        try {
            airportService.updateAtmosphericInformation(null, DataPointType.WIND.name(), new DataPoint.Builder()
                    .withCount(50).withFirst(40).withLast(30).withMean(20).withMedian(10).build());
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
        exception = null;
        try {
            airportService.updateAtmosphericInformation("AAA", null, new DataPoint.Builder().withCount(50).withFirst(40)
                    .withLast(30).withMean(20).withMedian(10).build());
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
        exception = null;
        try {
            airportService.updateAtmosphericInformation("AAA", DataPointType.WIND.name(), null);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void testUpdateAtmosphericInformationUnknown() throws WeatherException {
        IllegalArgumentException exception = null;
        try {
            airportService.updateAtmosphericInformation("", DataPointType.WIND.name(), new DataPoint.Builder()
                    .withCount(50).withFirst(40).withLast(30).withMean(20).withMedian(10).build());
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void testSaveAirport() {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(1.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("AAA"));
    }

    public void testSaveAirportNull() {
        airportService.addAirport(null);
        airportService.addAirport(new Airport.Builder().withIata(null).withLatitude(1.0).withLongitude(1.0).build());
    }

    @Test
    public void testDeleteAirport() throws WeatherException {
        Airport ad = new Airport.Builder().withIata("AAA").withLatitude(1.0).withLongitude(1.0).build();
        airportService.addAirport(ad);
        assertNotNull(airportService.getAirport("AAA"));
        airportService.updateAtmosphericInformation("AAA", DataPointType.WIND.name(),
                new DataPoint.Builder().withCount(50).withFirst(40).withLast(30).withMean(20).withMedian(10).build());
        assertNotNull(airportService.getAtmosphericInformationByIataCode("AAA"));
        airportService.deleteAirport("AAA");
        assertNull(airportService.getAirport("AAA"));
    }

    @Test
    public void testDeleteAirportNull() {
        airportService.deleteAirport(null);
    }

}