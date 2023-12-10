package vn.daoanhvu.assignmenttwo.model;

import java.io.Serializable;

public class Site implements Serializable {
    private String id;
    private String address;
    private String date;
    private String imageUrl;
    private String latlng;
    private String name;
    private String time;
    private String ownerId;
    public Site() {
    }

    public Site(String id, String address, String date, String imageUrl, String latlng, String name, String time, String ownerId) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.imageUrl = imageUrl;
        this.latlng = latlng;
        this.name = name;
        this.time = time;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Double getLatitude() {
        if (latlng != null && !latlng.isEmpty()) {
            String[] latLngArray = latlng.split(",");
            if (latLngArray.length == 2) {
                try {
                    return Double.parseDouble(latLngArray[0]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public Double getLongitude() {
        if (latlng != null && !latlng.isEmpty()) {
            String[] latLngArray = latlng.split(",");
            if (latLngArray.length == 2) {
                try {
                    return Double.parseDouble(latLngArray[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
