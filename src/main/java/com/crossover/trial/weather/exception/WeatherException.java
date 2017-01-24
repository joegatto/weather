package com.crossover.trial.weather.exception;

import com.crossover.trial.weather.model.DataPointType;

/**
 * An internal exception marker
 */
public class WeatherException extends Exception {

    private static final long serialVersionUID = -1087815705679741715L;

    private final DataPointType dataPointType;

    public WeatherException(final DataPointType dataPointType, final String message) {
        super(message);
        this.dataPointType = dataPointType;
    }

    public WeatherException(final DataPointType dataPointType, final String message, final Throwable cause) {
        super(message, cause);
        this.dataPointType = dataPointType;
    }

    public DataPointType getDataPointType() {
        return dataPointType;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " for data type :" + dataPointType.name();
    }
}
