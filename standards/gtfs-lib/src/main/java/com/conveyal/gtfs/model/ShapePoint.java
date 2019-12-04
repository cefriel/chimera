package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.util.Iterator;

import org.mapdb.Fun.Tuple2;

public class ShapePoint extends Entity {

    private static final long serialVersionUID = 6751814959971086070L;
    public String shape_id;
    public double shape_pt_lat;
    public double shape_pt_lon;
    public int    shape_pt_sequence;
    public double shape_dist_traveled;

    @Override
    public String getId () {
        return shape_id;
    }

    @Override
    public Integer getSequenceNumber() {
        return shape_pt_sequence;
    }

    public ShapePoint () { }

    
    // Similar to stoptime, we have to have a constructor, because fields are final so as to be immutable for storage in MapDB.
    public ShapePoint(String shape_id, double shape_pt_lat, double shape_pt_lon, int shape_pt_sequence, double shape_dist_traveled) {
        this.shape_id = shape_id;
        this.shape_pt_lat = shape_pt_lat;
        this.shape_pt_lon = shape_pt_lon;
        this.shape_pt_sequence = shape_pt_sequence;
        this.shape_dist_traveled = shape_dist_traveled;
    }


    
    public String getShape_id() {
        return shape_id;
    }

    public void setShape_id(String shape_id) {
        this.shape_id = shape_id;
    }

    public double getShape_pt_lat() {
        return shape_pt_lat;
    }

    public void setShape_pt_lat(double shape_pt_lat) {
        this.shape_pt_lat = shape_pt_lat;
    }

    public double getShape_pt_lon() {
        return shape_pt_lon;
    }

    public void setShape_pt_lon(double shape_pt_lon) {
        this.shape_pt_lon = shape_pt_lon;
    }

    public int getShape_pt_sequence() {
        return shape_pt_sequence;
    }

    public void setShape_pt_sequence(int shape_pt_sequence) {
        this.shape_pt_sequence = shape_pt_sequence;
    }

    public double getShape_dist_traveled() {
        return shape_dist_traveled;
    }

    public void setShape_dist_traveled(double shape_dist_traveled) {
        this.shape_dist_traveled = shape_dist_traveled;
    }



    public static class Loader extends Entity.Loader<ShapePoint> {

        public Loader(GTFSFeed feed) {
            super(feed, "shapes");
        }

        @Override
        protected boolean isRequired() {
            return false;
        }

        @Override
        public void loadOneRow() throws IOException {
            String shape_id = getStringField("shape_id", true);
            double shape_pt_lat = getDoubleField("shape_pt_lat", true, -90D, 90D);
            double shape_pt_lon = getDoubleField("shape_pt_lon", true, -180D, 180D);
            int shape_pt_sequence = getIntField("shape_pt_sequence", true, 0, Integer.MAX_VALUE);
            double shape_dist_traveled = getDoubleField("shape_dist_traveled", false, 0D, Double.MAX_VALUE);

            ShapePoint s = new ShapePoint(shape_id, shape_pt_lat, shape_pt_lon, shape_pt_sequence, shape_dist_traveled);
            s.sourceFileLine = row + 1; // offset line number by 1 to account for 0-based row index
            s.feed = null; // since we're putting this into MapDB, we don't want circular serialization
            feed.shape_points.put(new Tuple2<String, Integer>(s.shape_id, s.shape_pt_sequence), s);
        }
    }

    public static class Writer extends Entity.Writer<ShapePoint> {
        public Writer (GTFSFeed feed) {
            super(feed, "shapes");
        }

        @Override
        protected void writeHeaders() throws IOException {
            writer.writeRecord(new String[] {"shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled"});
        }

        @Override
        protected void writeOneRow(ShapePoint s) throws IOException {
            writeStringField(s.shape_id);
            writeDoubleField(s.shape_pt_lat);
            writeDoubleField(s.shape_pt_lon);
            writeIntField(s.shape_pt_sequence);
            writeDoubleField(s.shape_dist_traveled);
            endRecord();
        }

        @Override
        protected Iterator<ShapePoint> iterator() {
            return feed.shape_points.values().iterator();
        }
    }
}
