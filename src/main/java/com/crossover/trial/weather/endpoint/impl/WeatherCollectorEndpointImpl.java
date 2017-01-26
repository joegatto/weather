package com.crossover.trial.weather.endpoint.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.endpoint.WeatherCollectorEndpoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.impl.AirportServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
		DataPoint dp;
		try {
			dp = gson.fromJson(datapointJson, DataPoint.class);
		} catch (JsonSyntaxException e) {
			LOGGER.log(Level.SEVERE, "Cannot read datapoint", e);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (iataCode == null || pointType == null || dp == null || airportService.getAirport(iataCode) == null) {
			LOGGER.log(Level.SEVERE,
					"Bad parameters: iataCode = " + iataCode + ", pointType = " + pointType + ", dp = " + dp);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		try {
			airportService.updateAtmosphericInformation(iataCode, pointType, dp);
		} catch (WeatherException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response getAirports() {
		Set<String> result = new HashSet<>();
		result.addAll(airportService.getAllAirportCodes());
		return Response.status(Response.Status.OK).entity(result).build();
	}

	@Override
	public Response getAirport(@PathParam("iata") final String iata) {
		Airport ad = airportService.getAirport(iata);
		return Response.status(Response.Status.OK).entity(ad).build();
	}

	@Override
	public Response addAirport(@PathParam("iata") final String iata, @PathParam("lat") final String latString,
			@PathParam("long") final String longString) {
		if (iata == null || iata.length() != 3 || latString == null || longString == null) {
			LOGGER.log(Level.SEVERE,
					"Bad parameters: iata = " + iata + ", latString = " + latString + ", longString = " + longString);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		Airport airport = null;
		try {
			airport = new Airport.Builder().withIata(iata).withLatitude(Double.valueOf(latString))
					.withLongitude(Double.valueOf(longString)).build();
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE,
					"Wrong airport coordinates latString = " + latString + ", longString = " + longString);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		airportService.addAirport(airport);
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response deleteAirport(@PathParam("iata") final String iata) {
		airportService.deleteAirport(iata);
		return Response.status(Response.Status.OK).build();
	}

	@Override
	public Response exit() {
		return Response.status(Response.Status.FORBIDDEN).build();
	}

}
