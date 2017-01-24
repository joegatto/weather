package com.crossover.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

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

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        query = client.target("http://localhost:8080/query");
        collect = client.target("http://localhost:8080/collect");
    }

    public void upload(final InputStream airportDataStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        List<Airport> airports = reader.lines().skip(1).map(mapToAirport).collect(Collectors.toList());
    }

    private static Function<String, Airport> mapToAirport = (line) -> {
        String[] p = line.split(COMMA);
        return new Airport.Builder().withCity(p[0]).withCountry(p[1]).withIata(p[2]).withIcao(p[3])
                .withLatitude(new Double(p[4]).doubleValue()).withLongitude(new Double(p[5]).doubleValue())
                .withAltitude(new Double(p[6]).doubleValue()).withTimezone(new Integer(p[7]).intValue())
                .withDst(DST.valueOf(p[8])).build();
    };

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
