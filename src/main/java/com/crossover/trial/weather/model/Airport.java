package com.crossover.trial.weather.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author Joao Gatto
 */
public class Airport implements Serializable {

	private static final long serialVersionUID = 2134416010544747160L;

	/** Main city served by airport. May be spelled differently from name */
    private String city;

    /** Country or territory where airport is located */
    private String country;

    /** The three letter IATA code */
    private String iata = "";

    /** The four letter ICAO code */
    private String icao = "";

    /** latitude/longitude values in degrees */
    private Coordinate coordinate = null;

    /** altitude value in feet */
    private double altitude;

    /**
     * Hours offset from UTC. Fractional hours are expressed as decimals.
     */
    private int timezone;

    private DST dst;

    private Airport(final Builder builder) {
        this.city = builder.city;
        this.country = builder.country;
        this.iata = builder.iata;
        this.icao = builder.icao;
		this.coordinate = new Coordinate(builder.latitude, builder.longitude);
        this.altitude = builder.altitude;
        this.timezone = builder.timezone;
        this.dst = builder.dst;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(final String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(final String icao) {
        this.icao = icao;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(final Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(final double altitude) {
        this.altitude = altitude;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(final int timezone) {
        this.timezone = timezone;
    }

    public DST getDst() {
        return dst;
    }

    public void setDst(final DST dst) {
        this.dst = dst;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public static class Builder {

        private String iata = "";
        private String city = "";
        private String country = "";
        private String icao = "";
        private double latitude = 0.0;
        private double longitude = 0.0;
        private double altitude = 0.0;
        private int timezone = 0;
        private DST dst = DST.U;

        public Builder() {
        }

        public Builder withIata(final String iata) {
            this.iata = iata;
            return this;
        }

        public Builder withCity(final String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(final String country) {
            this.country = country;
            return this;
        }

        public Builder withIcao(final String icao) {
            this.icao = icao;
            return this;
        }

        public Builder withLatitude(final double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder withLongitude(final double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder withAltitude(final double altitude) {
            this.altitude = altitude;
            return this;
        }

        public Builder withTimezone(final int timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder withDst(final DST dst) {
            this.dst = dst;
            return this;
        }

        public Airport build() {
            return new Airport(this);
        }
    }

}
