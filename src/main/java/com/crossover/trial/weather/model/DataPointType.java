package com.crossover.trial.weather.model;

/**
 * The various types of data points we can collect.
 *
 * @author Joao Gatto
 */
public enum DataPointType {
	WIND(0, 1000), TEMPERATURE(-50, 100), HUMIDITY(0, 100), PRESSURE(650, 800), CLOUDCOVER(0, 100), PRECIPITATION(0, 100);

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
}
