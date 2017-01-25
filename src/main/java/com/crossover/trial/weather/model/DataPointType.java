package com.crossover.trial.weather.model;

import com.crossover.trial.weather.exception.WeatherException;

/**
 * The various types of data points we can collect.
 *
 * @author Joao Gatto
 */
public enum DataPointType {
    WIND(0, 1000), TEMPERATURE(-50, 100), HUMIDITY(0, 100), PRESSURE(650, 800), CLOUDCOVER(0, 100), PRECIPITATION(0,
            100);

    private double min;
    private double max;

    DataPointType(final double min, final double max) {
        this.min = min;
        this.max = max;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public boolean validate(final DataPoint dataPoint) throws WeatherException {
        boolean valid = false;

        switch (this) {
        case WIND:
            valid = dataPoint.getMean() >= this.getMin();
            return valid;
        case TEMPERATURE:
            valid = dataPoint.getMean() >= this.getMin() && dataPoint.getMean() < this.getMax();
            return valid;
        case HUMIDITY:
            valid = dataPoint.getMean() >= this.getMin() && dataPoint.getMean() < this.getMax();
            return valid;
        case PRESSURE:
            valid = dataPoint.getMean() >= this.getMin() && dataPoint.getMean() < this.getMax();
            return valid;
        case CLOUDCOVER:
            valid = dataPoint.getMean() >= this.getMin() && dataPoint.getMean() < this.getMax();
            return valid;
        case PRECIPITATION:
            valid = dataPoint.getMean() >= this.getMin() && dataPoint.getMean() < this.getMax();
            return valid;
        default:
            if (!valid) {
                throw new WeatherException(this, "Invalid atmospheric data parameter");
            }
            return valid;
        }
    }
}
