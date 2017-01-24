package com.crossover.trial.weather.model;

public enum DST {
    E("Europe"), A("US/Canada"), S("South America"), O("Australia"), Z("New Zeland"), N("None"), U("Unknown");

    private String region;

    DST(final String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

}
