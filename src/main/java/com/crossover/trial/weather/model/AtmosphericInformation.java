package com.crossover.trial.weather.model;

import java.io.Serializable;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Encapsulates sensor information for a particular location
 */
public class AtmosphericInformation implements Serializable {

    private static final long serialVersionUID = 4884101137188765237L;

    /** temperature in degrees celsius */
    private DataPoint temperature;

    /** wind speed in km/h */
    private DataPoint wind;

    /** humidity in percent */
    private DataPoint humidity;

    /** precipitation in cm */
    private DataPoint precipitation;

    /** pressure in mmHg */
    private DataPoint pressure;

    /** cloud cover percent from 0 - 100 (integer) */
    private DataPoint cloudCover;

    /** the last time this data was updated, in milliseconds since UTC epoch */
    private long lastUpdateTime;

    private AtmosphericInformation(final Builder builder) {
        builder.temperature.ifPresent(value -> setTemperature(value));
        builder.wind.ifPresent(value -> setWind(value));
        builder.humidity.ifPresent(value -> setHumidity(value));
        builder.precipitation.ifPresent(value -> setPrecipitation(value));
        builder.pressure.ifPresent(value -> setPressure(value));
        builder.cloudCover.ifPresent(value -> setCloudCover(value));
    }

    public DataPoint getTemperature() {
        return temperature;
    }

    public void setTemperature(final DataPoint temperature) {
        this.temperature = temperature;
    }

    public DataPoint getWind() {
        return wind;
    }

    public void setWind(final DataPoint wind) {
        this.wind = wind;
    }

    public DataPoint getHumidity() {
        return humidity;
    }

    public void setHumidity(final DataPoint humidity) {
        this.humidity = humidity;
    }

    public DataPoint getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(final DataPoint precipitation) {
        this.precipitation = precipitation;
    }

    public DataPoint getPressure() {
        return pressure;
    }

    public void setPressure(final DataPoint pressure) {
        this.pressure = pressure;
    }

    public DataPoint getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(final DataPoint cloudCover) {
        this.cloudCover = cloudCover;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(final long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(final Object that) {
        return EqualsBuilder.reflectionEquals(this, that, false);
    }

    public static class Builder {

        private Optional<DataPoint> temperature = Optional.empty();
        private Optional<DataPoint> wind = Optional.empty();
        private Optional<DataPoint> humidity = Optional.empty();
        private Optional<DataPoint> precipitation = Optional.empty();
        private Optional<DataPoint> pressure = Optional.empty();
        private Optional<DataPoint> cloudCover = Optional.empty();

        public Builder withTemperature(final DataPoint temperature) {
            if (temperature != null) {
                this.temperature = Optional.of(temperature);
            }
            return this;
        }

        public Builder withWind(final DataPoint wind) {
            if (wind != null) {
                this.wind = Optional.of(wind);
            }
            return this;
        }

        public Builder withHumidity(final DataPoint humidity) {
            if (humidity != null) {
                this.humidity = Optional.of(humidity);
            }
            return this;
        }

        public Builder withPrecipitation(final DataPoint precipitation) {
            if (precipitation != null) {
                this.precipitation = Optional.of(precipitation);
            }
            return this;
        }

        public Builder withPressure(final DataPoint pressure) {
            if (pressure != null) {
                this.pressure = Optional.of(pressure);
            }
            return this;
        }

        public Builder withCloudCover(final DataPoint cloudCover) {
            if (cloudCover != null) {
                this.cloudCover = Optional.of(cloudCover);
            }
            return this;
        }

        public AtmosphericInformation build() {
            return new AtmosphericInformation(this);
        }
    }
}
