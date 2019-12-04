package com.conveyal.gtfs.model;

import com.conveyal.gtfs.GTFSFeed;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class Agency extends Entity {

    private static final long serialVersionUID = -2825890165823575940L;
    public String agency_id;
    public String agency_name;
    public URL    agency_url;
    public String agency_timezone;
    public String agency_lang;
    public String agency_email;
    public String agency_phone;
    public URL    agency_fare_url;
    public URL    agency_branding_url;
    public String feed_id;

    @Override
    public String getId () {
        return agency_id;
    }

    public static class Loader extends Entity.Loader<Agency> {

        public Loader(GTFSFeed feed) {
            super(feed, "agency");
        }

        @Override
        protected boolean isRequired() {
            return true;
        }

        @Override
        public void loadOneRow() throws IOException {
            Agency a = new Agency();
            a.sourceFileLine = row + 1; // offset line number by 1 to account for 0-based row index
            a.agency_id    = getStringField("agency_id", false); // can only be absent if there is a single agency -- requires a special validator.
            a.agency_name  = getStringField("agency_name", true);
            a.agency_url   = getUrlField("agency_url", true);
            a.agency_lang  = getStringField("agency_lang", false);
            a.agency_email = getStringField("agency_email", false);
            a.agency_phone = getStringField("agency_phone", false);
            a.agency_timezone = getStringField("agency_timezone", true);
            a.agency_fare_url = getUrlField("agency_fare_url", false);
            a.agency_branding_url = getUrlField("agency_branding_url", false);
            a.feed = feed;
            a.feed_id = feed.feedId;

            // TODO clooge due to not being able to have null keys in mapdb
            if (a.agency_id == null) a.agency_id = "NONE";

            feed.agency.put(a.agency_id, a);
        }

    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    public URL getAgency_url() {
        return agency_url;
    }

    public void setAgency_url(URL agency_url) {
        this.agency_url = agency_url;
    }

    public String getAgency_timezone() {
        return agency_timezone;
    }

    public void setAgency_timezone(String agency_timezone) {
        this.agency_timezone = agency_timezone;
    }

    public String getAgency_lang() {
        return agency_lang;
    }

    public void setAgency_lang(String agency_lang) {
        this.agency_lang = agency_lang;
    }

    public String getAgency_email() {
        return agency_email;
    }

    public void setAgency_email(String agency_email) {
        this.agency_email = agency_email;
    }

    public String getAgency_phone() {
        return agency_phone;
    }

    public void setAgency_phone(String agency_phone) {
        this.agency_phone = agency_phone;
    }

    public URL getAgency_fare_url() {
        return agency_fare_url;
    }

    public void setAgency_fare_url(URL agency_fare_url) {
        this.agency_fare_url = agency_fare_url;
    }

    public URL getAgency_branding_url() {
        return agency_branding_url;
    }

    public void setAgency_branding_url(URL agency_branding_url) {
        this.agency_branding_url = agency_branding_url;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    
    public static class Writer extends Entity.Writer<Agency> {
        public Writer(GTFSFeed feed) {
            super(feed, "agency");
        }

        @Override
        public void writeHeaders() throws IOException {
            writer.writeRecord(new String[] {"agency_id", "agency_name", "agency_url", "agency_lang",
                    "agency_phone", "agency_email", "agency_timezone", "agency_fare_url", "agency_branding_url"});
        }

        @Override
        public void writeOneRow(Agency a) throws IOException {
            writeStringField(a.agency_id);
            writeStringField(a.agency_name);
            writeUrlField(a.agency_url);
            writeStringField(a.agency_lang);
            writeStringField(a.agency_phone);
            writeStringField(a.agency_email);
            writeStringField(a.agency_timezone);
            writeUrlField(a.agency_fare_url);
            writeUrlField(a.agency_branding_url);
            endRecord();
        }

        @Override
        public Iterator<Agency> iterator() {
            return this.feed.agency.values().iterator();
        }
    }


}
