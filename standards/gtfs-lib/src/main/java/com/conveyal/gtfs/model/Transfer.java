package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.util.Iterator;

public class Transfer extends Entity {

    private static final long serialVersionUID = -4944512120812641063L;
    public String from_stop_id;
    public String to_stop_id;
    public int  transfer_type;
    public int  min_transfer_time;
    public String from_route_id;
    public String to_route_id;
    public String from_trip_id;
    public String to_trip_id;

    // TODO: Add id method for Transfer.
//    @Override
//    public String getId() {
////        return trip_id;
//    }

    
    
    public String getFrom_stop_id() {
        return from_stop_id;
    }

    public void setFrom_stop_id(String from_stop_id) {
        this.from_stop_id = from_stop_id;
    }

    public String getTo_stop_id() {
        return to_stop_id;
    }

    public void setTo_stop_id(String to_stop_id) {
        this.to_stop_id = to_stop_id;
    }

    public int getTransfer_type() {
        return transfer_type;
    }

    public void setTransfer_type(int transfer_type) {
        this.transfer_type = transfer_type;
    }

    public int getMin_transfer_time() {
        return min_transfer_time;
    }

    public void setMin_transfer_time(int min_transfer_time) {
        this.min_transfer_time = min_transfer_time;
    }

    public String getFrom_route_id() {
        return from_route_id;
    }

    public void setFrom_route_id(String from_route_id) {
        this.from_route_id = from_route_id;
    }

    public String getTo_route_id() {
        return to_route_id;
    }

    public void setTo_route_id(String to_route_id) {
        this.to_route_id = to_route_id;
    }

    public String getFrom_trip_id() {
        return from_trip_id;
    }

    public void setFrom_trip_id(String from_trip_id) {
        this.from_trip_id = from_trip_id;
    }

    public String getTo_trip_id() {
        return to_trip_id;
    }

    public void setTo_trip_id(String to_trip_id) {
        this.to_trip_id = to_trip_id;
    }

    public static class Loader extends Entity.Loader<Transfer> {

        public Loader(GTFSFeed feed) {
            super(feed, "transfers");
        }

        @Override
        protected boolean isRequired() {
            return false;
        }

        @Override
        public void loadOneRow() throws IOException {
            Transfer tr = new Transfer();
            tr.sourceFileLine    = row + 1; // offset line number by 1 to account for 0-based row index
            tr.from_stop_id      = getStringField("from_stop_id", true);
            tr.to_stop_id        = getStringField("to_stop_id", true);
            tr.transfer_type     = getIntField("transfer_type", true, 0, 3);
            tr.min_transfer_time = getIntField("min_transfer_time", false, 0, Integer.MAX_VALUE);
            tr.from_route_id     = getStringField("from_route_id", false);
            tr.to_route_id       = getStringField("to_route_id", false);
            tr.from_trip_id      = getStringField("from_trip_id", false);
            tr.to_trip_id        = getStringField("to_trip_id", false);

            getRefField("from_stop_id", true, feed.stops);
            getRefField("to_stop_id", true, feed.stops);
            getRefField("from_route_id", false, feed.routes);
            getRefField("to_route_id", false, feed.routes);
            getRefField("from_trip_id", false, feed.trips);
            getRefField("to_trip_id", false, feed.trips);

            tr.feed = feed;
            feed.transfers.put(Long.toString(row), tr);
        }

    }

    public static class Writer extends Entity.Writer<Transfer> {
        public Writer (GTFSFeed feed) {
            super(feed, "transfers");
        }

        @Override
        protected void writeHeaders() throws IOException {
            writer.writeRecord(new String[] {"from_stop_id", "to_stop_id", "transfer_type", "min_transfer_time"});
        }

        @Override
        protected void writeOneRow(Transfer t) throws IOException {
            writeStringField(t.from_stop_id);
            writeStringField(t.to_stop_id);
            writeIntField(t.transfer_type);
            writeIntField(t.min_transfer_time);
            endRecord();
        }

        @Override
        protected Iterator<Transfer> iterator() {
            return feed.transfers.values().iterator();
        }


    }
}
