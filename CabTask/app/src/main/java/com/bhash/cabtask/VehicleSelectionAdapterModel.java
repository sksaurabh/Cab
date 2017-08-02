package com.bhash.cabtask;

/**
 * Created by saurabh on 8/1/2017.
 */
public class VehicleSelectionAdapterModel {
    private String id;
    private String vehicleType;
    private String createdAt;
    private String updatedAt;
    private String link;
    private String vehicleName;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public int getVehicleImageId() {
        return vehicleImageId;
    }

    public void setVehicleImageId(int vehicleImageId) {
        this.vehicleImageId = vehicleImageId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private int vehicleImageId;
    private boolean isSelected;
    public VehicleSelectionAdapterModel() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

