package com.conveyal.gtfs.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * This table does not exist in GTFS. It is a join of fare_attributes and fare_rules on fare_id.
 * There should only be one fare_attribute per fare_id, but there can be many fare_rules per fare_id.
 */
public class Fare implements Serializable {
    public static final long serialVersionUID = 1L;

    public String         fare_id;
    public FareAttribute  fare_attribute;
    public List<FareRule> fare_rules = Lists.newArrayList();

    public Fare(String fare_id) {
        this.fare_id = fare_id;
    }

    public String getFare_id() {
        return fare_id;
    }

    public void setFare_id(String fare_id) {
        this.fare_id = fare_id;
    }

    public FareAttribute getFare_attribute() {
        return fare_attribute;
    }

    public void setFare_attribute(FareAttribute fare_attribute) {
        this.fare_attribute = fare_attribute;
    }

    public List<FareRule> getFare_rules() {
        return fare_rules;
    }

    public void setFare_rules(List<FareRule> fare_rules) {
        this.fare_rules = fare_rules;
    }

    
}
