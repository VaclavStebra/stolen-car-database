package cz.muni.fi.a2p06.stolencardatabase.entity;

import com.google.firebase.database.PropertyName;

/**
 * Created by Vaclav Stebra
 */

public class Car {

    private String color;
    private String engine;
    private String manufacturer;
    private String photoUrl;
    private int productionYear;
    private String regno;
    private long stolenDate;
    private String model;
    private String vin;
    private Coordinates location;
    private String district;

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
    public int getProductionYear() {
        return productionYear;
    }

    @PropertyName("production_year")
    public void setProductionYear(int productionYear) {
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
}
