package cz.muni.fi.a2p06.stolencardatabase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;

/**
 * Created by Vaclav Stebra
 */

public class Car implements Parcelable {

    private String color;
    private String engine;
    private String manufacturer;
    private String photoUrl;
    private Integer productionYear;
    private String regno;
    private long stolenDate;
    private String model;
    private String vin;
    private Coordinates location;
    private String district;
    private String userUid;
    private HashMap<String, Coordinates> reportedLocation;

    public Car() {
    }

    @PropertyName("color")
    public String getColor() {
        return color;
    }

    @PropertyName("color")
    public void setColor(String color) {
        this.color = color;
    }


    @PropertyName("engine")
    public String getEngine() {
        return engine;
    }


    @PropertyName("engine")
    public void setEngine(String engine) {
        this.engine = engine;
    }


    @PropertyName("manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }


    @PropertyName("manufacturer")
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @PropertyName("photo_url")
    public String getPhotoUrl() {
        return photoUrl;
    }

    @PropertyName("photo_url")
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @PropertyName("production_year")
    public Integer getProductionYear() {
        return productionYear;
    }

    @PropertyName("production_year")
    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    @PropertyName("regno")
    public String getRegno() {
        return regno;
    }

    @PropertyName("regno")
    public void setRegno(String regno) {
        this.regno = regno;
    }

    @PropertyName("stolen_date")
    public long getStolenDate() {
        return stolenDate;
    }

    @PropertyName("stolen_date")
    public void setStolenDate(long stolenDate) {
        this.stolenDate = stolenDate;
    }

    @PropertyName("model")
    public String getModel() {
        return model;
    }

    @PropertyName("model")
    public void setModel(String model) {
        this.model = model;
    }

    @PropertyName("vin")
    public String getVin() {
        return vin;
    }

    @PropertyName("vin")
    public void setVin(String vin) {
        this.vin = vin;
    }

    @PropertyName("location")
    public Coordinates getLocation() {
        return location;
    }

    @PropertyName("location")
    public void setLocation(Coordinates location) {
        this.location = location;
    }

    @PropertyName("district")
    public String getDistrict() {
        return district;
    }

    @PropertyName("district")
    public void setDistrict(String district) {
        this.district = district;
    }

    @PropertyName("user_uid")
    public String getUserUid() {
        return userUid;
    }

    @PropertyName("user_uid")
    public void setUserUid(String uid) {
        this.userUid = uid;
    }

    @PropertyName("reported_location")
    public HashMap<String, Coordinates> getReportedLocation() {
        return reportedLocation;
    }

    @PropertyName("reported_location")
    public void setReportedLocation(HashMap<String, Coordinates> reportedLocation) {
        this.reportedLocation = reportedLocation;
    }

    @Override
    public String toString() {
        return "Car{" +
                "manufacturer='" + manufacturer + '\'' +
                ", productionYear=" + productionYear +
                ", regno='" + regno + '\'' +
                ", stolenDate=" + stolenDate +
                ", model='" + model + '\'' +
                ", vin='" + vin + '\'' +
                ", district='" + district + '\'' +
                ", userUID='" + userUid + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.color);
        dest.writeString(this.engine);
        dest.writeString(this.manufacturer);
        dest.writeString(this.photoUrl);
        dest.writeValue(this.productionYear);
        dest.writeString(this.regno);
        dest.writeLong(this.stolenDate);
        dest.writeString(this.model);
        dest.writeString(this.vin);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.district);
        dest.writeString(this.userUid);
        dest.writeSerializable(this.reportedLocation);
    }

    private Car(Parcel in) {
        this.color = in.readString();
        this.engine = in.readString();
        this.manufacturer = in.readString();
        this.photoUrl = in.readString();
        this.productionYear = (Integer) in.readValue(Integer.class.getClassLoader());
        this.regno = in.readString();
        this.stolenDate = in.readLong();
        this.model = in.readString();
        this.vin = in.readString();
        this.location = in.readParcelable(Coordinates.class.getClassLoader());
        this.district = in.readString();
        this.userUid = in.readString();
        this.reportedLocation = (HashMap<String, Coordinates>) in.readSerializable();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };
}
