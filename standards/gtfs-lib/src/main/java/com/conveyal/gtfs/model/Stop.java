package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class Stop extends Entity {

    private static final long serialVersionUID = 464065335273514677L;
    public String stop_id;
    public String stop_code;
    public String stop_name;
    public String stop_desc;
    public double stop_lat;
    public double stop_lon;
    public String zone_id;
    public URL    stop_url;
    public int    location_type;
    public String parent_station;
    public String stop_timezone;
    // TODO should be int
    public String wheelchair_boarding;
    public String feed_id;

    @Override
    public String getId () {
        return stop_id;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_code() {
        return stop_code;
    }

    public void setStop_code(String stop_code) {
        this.stop_code = stop_code;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getStop_desc() {
        return stop_desc;
    }

    public void setStop_desc(String stop_desc) {
        this.stop_desc = stop_desc;
    }

    public double getStop_lat() {
        return stop_lat;
    }

    public void setStop_lat(double stop_lat) {
        this.stop_lat = stop_lat;
    }

    public double getStop_lon() {
        return stop_lon;
    }

    public void setStop_lon(double stop_lon) {
        this.stop_lon = stop_lon;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public URL getStop_url() {
        return stop_url;
    }

    public void setStop_url(URL stop_url) {
        this.stop_url = stop_url;
    }

    public int getLocation_type() {
        return location_type;
    }

    public void setLocation_type(int location_type) {
        this.location_type = location_type;
    }

    public String getParent_station() {
        return parent_station;
    }

    public void setParent_station(String parent_station) {
        this.parent_station = parent_station;
    }

    public String getStop_timezone() {
        return stop_timezone;
    }

    public void setStop_timezone(String stop_timezone) {
        this.stop_timezone = stop_timezone;
    }

    public String getWheelchair_boarding() {
        return wheelchair_boarding;
    }

    public void setWheelchair_boarding(String wheelchair_boarding) {
        this.wheelchair_boarding = wheelchair_boarding;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public static class Loader extends Entity.Loader<Stop> {

        public Loader(GTFSFeed feed) {
            super(feed, "stops");
        }

        @Override
        protected boolean isRequired() {
            return true;
        }

        @Override
        public void loadOneRow() throws IOException {
            Stop s = new Stop();
            s.sourceFileLine = row + 1; // offset line number by 1 to account for 0-based row index
            s.stop_id   = getStringField("stop_id", true);
            s.stop_code = getStringField("stop_code", false);
            s.stop_name = getStringField("stop_name", true);
            s.stop_desc = getStringField("stop_desc", false);
            s.stop_lat  = getDoubleField("stop_lat", true, -90D, 90D);
            s.stop_lon  = getDoubleField("stop_lon", true, -180D, 180D);
            s.zone_id   = getStringField("zone_id", false);
            s.stop_url  = getUrlField("stop_url", false);
            s.location_type  = getIntField("location_type", false, 0, 1);
            s.parent_station = getStringField("parent_station", false);
            s.stop_timezone  = getStringField("stop_timezone", false);
            s.wheelchair_boarding = getStringField("wheelchair_boarding", false);
            s.feed = feed;
            s.feed_id = feed.feedId;
            /* TODO check ref integrity later, this table self-references via parent_station */

            feed.stops.put(s.stop_id, s);
        }

    }

    public static class Writer extends Entity.Writer<Stop> {
        public Writer (GTFSFeed feed) {
            super(feed, "stops");
        }

        @Override
        public void writeHeaders() throws IOException {
            writer.writeRecord(new String[] {"stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "zone_id",					
                    "stop_url", "location_type", "parent_station", "stop_timezone", "wheelchair_boarding"});
        }

        @Override
        public void writeOneRow(Stop s) throws IOException {
            writeStringField(s.stop_id);
            writeStringField(s.stop_code);
            writeStringField(s.stop_name);
            writeStringField(s.stop_desc);
            writeDoubleField(s.stop_lat);
            writeDoubleField(s.stop_lon);
            writeStringField(s.zone_id);
            writeUrlField(s.stop_url);
            writeIntField(s.location_type);
            writeStringField(s.parent_station);
            writeStringField(s.stop_timezone);
            writeStringField(s.wheelchair_boarding);
            endRecord();
        }

        @Override
        public Iterator<Stop> iterator() {
            return feed.stops.values().iterator();
        }   	
    }
}
