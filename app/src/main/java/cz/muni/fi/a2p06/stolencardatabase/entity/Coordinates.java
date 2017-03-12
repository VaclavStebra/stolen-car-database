package cz.muni.fi.a2p06.stolencardatabase.entity;

import com.google.firebase.database.PropertyName;

/**
 * Created by Vaclav Stebra
 */

public class Coordinates {

    private double lat;
    private double lon;

    public Coordinates() {
    }

    @PropertyName("lat")
    public double getLat() {
        return lat;
    }

    @PropertyName("lat")
    public void setLat(double lat) {
        this.lat = lat;
    }

    @PropertyName("lon")
    public double getLon() {
        return lon;
    }

    @PropertyName("lon")
    public void setLon(double lon) {
        this.lon = lon;
    }
}
