package ver1.SpringDemoBot.model;
import jdk.jfr.Event;

import java.time.LocalDateTime;
import java.util.List;

public class ShippingInfo {
    private boolean isContainerSearch;
    private String origin;
    private String destination;
    private List<Container> containers;

    public boolean isContainerSearch() {
        return isContainerSearch;
    }

    public void setContainerSearch(boolean containerSearch) {
        isContainerSearch = containerSearch;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public static class Location {
        private String terminal;
        private String geo_site;
        private String city;
        private String state;
        private String country;
        private String country_code;
        private String geoid_city;
        private String site_type;
        private List<Event> events;

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

        public String getGeo_site() {
            return geo_site;
        }

        public void setGeo_site(String geo_site) {
            this.geo_site = geo_site;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getGeoid_city() {
            return geoid_city;
        }

        public void setGeoid_city(String geoid_city) {
            this.geoid_city = geoid_city;
        }

        public String getSite_type() {
            return site_type;
        }

        public void setSite_type(String site_type) {
            this.site_type = site_type;
        }

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }
    }

    public static class Container {
        private String container_num;
        private String container_size;
        private String container_type;
        private String iso_code;
        private String operator;
        private List<Location> locations;
        private LocalDateTime eta_final_delivery;
        private Event latest;

        public String getContainer_num() {
            return container_num;
        }

        public void setContainer_num(String container_num) {
            this.container_num = container_num;
        }

        public String getContainer_size() {
            return container_size;
        }

        public void setContainer_size(String container_size) {
            this.container_size = container_size;
        }

        public String getContainer_type() {
            return container_type;
        }

        public void setContainer_type(String container_type) {
            this.container_type = container_type;
        }

        public String getIso_code() {
            return iso_code;
        }

        public void setIso_code(String iso_code) {
            this.iso_code = iso_code;
        }
    }
}