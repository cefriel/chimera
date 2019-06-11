package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.util.Iterator;

public class Trip extends Entity {

    private static final long serialVersionUID = -4869384750974542712L;
    public String route_id;
    public String service_id;
    public String trip_id;
    public String trip_headsign;
    public String trip_short_name;
    public int    direction_id;
    public String block_id;
    public String shape_id;
    public int    bikes_allowed;
    public int    wheelchair_accessible;
    public String feed_id;

    @Override
    public String getId() {
        return trip_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public void setTrip_headsign(String trip_headsign) {
        this.trip_headsign = trip_headsign;
    }

    public String getTrip_short_name() {
        return trip_short_name;
    }

    public void setTrip_short_name(String trip_short_name) {
        this.trip_short_name = trip_short_name;
    }

    public int getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(int direction_id) {
        this.direction_id = direction_id;
    }

    public String getBlock_id() {
        return block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getShape_id() {
        return shape_id;
    }

    public void setShape_id(String shape_id) {
        this.shape_id = shape_id;
    }

    public int getBikes_allowed() {
        return bikes_allowed;
    }

    public void setBikes_allowed(int bikes_allowed) {
        this.bikes_allowed = bikes_allowed;
    }

    public int getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    public void setWheelchair_accessible(int wheelchair_accessible) {
        this.wheelchair_accessible = wheelchair_accessible;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public static class Loader extends Entity.Loader<Trip> {

        public Loader(GTFSFeed feed) {
            super(feed, "trips");
        }

        @Override
        protected boolean isRequired() {
            return true;
        }

        @Override
        public void loadOneRow() throws IOException {
            Trip t = new Trip();

            t.sourceFileLine  = row + 1; // offset line number by 1 to account for 0-based row index
            t.route_id        = getStringField("route_id", true);
            t.service_id      = getStringField("service_id", true);
            t.trip_id         = getStringField("trip_id", true);
            t.trip_headsign   = getStringField("trip_headsign", false);
            t.trip_short_name = getStringField("trip_short_name", false);
            t.direction_id    = getIntField("direction_id", false, 0, 1);
            t.block_id        = getStringField("block_id", false); // make a blocks multimap
            t.shape_id        = getStringField("shape_id", false);
            t.bikes_allowed   = getIntField("bikes_allowed", false, 0, 2);
            t.wheelchair_accessible = getIntField("wheelchair_accessible", false, 0, 2);
            t.feed = feed;
            t.feed_id = feed.feedId;
            feed.trips.put(t.trip_id, t);

            /*
              Check referential integrity without storing references. Trip cannot directly reference Services or
              Routes because they would be serialized into the MapDB.
             */
            // TODO confirm existence of shape ID
            getRefField("service_id", true, feed.services);
            getRefField("route_id", true, feed.routes);
        }

    }

    public static class Writer extends Entity.Writer<Trip> {
        public Writer (GTFSFeed feed) {
            super(feed, "trips");
        }

        @Override
        protected void writeHeaders() throws IOException {
            // TODO: export shapes
            writer.writeRecord(new String[] {"route_id", "trip_id", "trip_headsign", "trip_short_name", "direction_id", "block_id",
                    "shape_id", "bikes_allowed", "wheelchair_accessible", "service_id"});
        }

        @Override
        protected void writeOneRow(Trip t) throws IOException {
            writeStringField(t.route_id);
            writeStringField(t.trip_id);
            writeStringField(t.trip_headsign);
            writeStringField(t.trip_short_name);
            writeIntField(t.direction_id);
            writeStringField(t.block_id);
            writeStringField(t.shape_id);
            writeIntField(t.bikes_allowed);
            writeIntField(t.wheelchair_accessible);
            writeStringField(t.service_id);
            endRecord();
        }

        @Override
        protected Iterator<Trip> iterator() {
            return feed.trips.values().iterator();
        }


    }

}
