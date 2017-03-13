package cz.muni.fi.a2p06.stolencardatabase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

/**
 * Created by Vaclav Stebra
 */

public class Coordinates implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }

    private Coordinates(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
}
