package com.crossover.trial.weather.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected
 * values
 *
 * @author Joao Gatto
 */
public class DataPoint {

	private double mean = 0.0;
	private double first = 0;
	private double median = 0;
	private double last = 0;
	private int count = 0;

	private DataPoint(final Builder builder) {
		this.first = builder.first;
		this.mean = builder.mean;
		this.median = builder.median;
		this.last = builder.last;
		this.count = builder.count;
	}

	/** the mean of the observations */
	public double getMean() {
		return mean;
	}

	public void setMean(final double mean) {
		this.mean = mean;
	}

	/** 1st quartile -- useful as a lower bound */
	public double getFirst() {
		return first;
	}

	public void setFirst(final double first) {
		this.first = first;
	}

	/** 2nd quartile -- median value */
	public double getMedian() {
		return median;
	}

	public void setMedian(final double median) {
		this.median = median;
	}

	/** 3rd quartile value -- less noisy upper value */
	public double getLast() {
		return last;
	}

	public void setLast(final double last) {
		this.last = last;
	}

	/** the total number of measurements */
	public int getCount() {
		return count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

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
		private double mean = 0.0;
		private double first = 0;
		private double median = 0;
		private double last = 0;
		private int count = 0;

		public Builder() {
		}

		public Builder withMean(final double mean) {
			this.mean = mean;
			return this;
		}

		public Builder withFirst(final double first) {
			this.first = first;
			return this;
		}

		public Builder withMedian(final double median) {
			this.median = median;
			return this;
		}

		public Builder withLast(final double last) {
			this.last = last;
			return this;
		}

		public Builder withCount(final int count) {
			this.count = count;
			return this;
		}

		public DataPoint build() {
			return new DataPoint(this);
		}
	}
}
