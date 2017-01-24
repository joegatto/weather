package com.crossover.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.model.Airport;
import com.crossover.trial.weather.model.DST;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice
 *
 * @author Joao Gatto
 */
public class AirportLoader {

	private static final String COMMA = ",";

	/** end point to supply updates */
	private WebTarget collect;

	public AirportLoader() {
		Client client = ClientBuilder.newClient();
		collect = client.target("http://localhost:8080/collect");
	}

	public void upload(final InputStream airportDataStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
		List<Airport> airports = reader.lines().skip(1).map(mapToAirport).collect(Collectors.toList());
		airports.stream().filter(Objects::nonNull).forEach((airport) -> {
			Response post = collect
					.path("/airport/" + airport.getIata() + "/" + airport.getLatitude() + "/" + airport.getLongitude())
					.request().post(Entity.text(""));
			processResultStatus(airport.getIata(), post);
		});
	}

	private static Function<String, Airport> mapToAirport = (line) -> {
		String[] p = line.split(COMMA);
		return new Airport.Builder().withCity(p[1]).withCountry(p[2]).withIata(p[3]).withIcao(p[4])
				.withLatitude(Double.valueOf(p[5])).withLongitude(Double.valueOf(p[6]))
				.withAltitude(Double.valueOf(p[7])).withTimezone(Integer.valueOf(p[8])).withDst(DST.valueOf(p[9]))
				.build();
	};

	private void processResultStatus(String iataCode, Response post) {
		switch (post.getStatus()) {
		case 200:
			break;

		case 403:
			System.out.println("Warning: airport entry '" + iataCode + "' already exists");
			break;

		default:
			System.out.println(
					"ERROR when adding airport '" + iataCode + "': " + post.getStatus() + " " + post.getStatusInfo());
		}
	}

	public static void main(final String[] args) throws IOException {
		File airportDataFile = new File(args[0]);
		if (!airportDataFile.exists() || airportDataFile.length() == 0) {
			System.err.println(airportDataFile + " is not a valid input");
			System.exit(1);
		}

		AirportLoader al = new AirportLoader();
		al.upload(new FileInputStream(airportDataFile));
		System.exit(0);
	}
}
