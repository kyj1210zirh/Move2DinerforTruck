package com.mjc.yhs.move2diner.DTO;

import java.io.Serializable;

public class SalesInfoListItem{
    private String locationlat,locationlon,salesdate,endtime,starttime,truckName,truckUid,addressLine,thumbnail; //영업 데이터
    private Boolean onBusiness;
    public SalesInfoListItem(){}

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSalesdate() {
        return salesdate;
    }

    public void setSalesdate(String salesdate) {
        this.salesdate = salesdate;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getLocationlat() {
        return locationlat;
    }

    public void setLocationlat(String locationlat) {
        this.locationlat = locationlat;
    }

    public String getLocationlon() {
        return locationlon;
    }

    public void setLocationlon(String locationlon) {
        this.locationlon = locationlon;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getTruckUid() {
        return truckUid;
    }

    public void setTruckUid(String truckUid) {
        this.truckUid = truckUid;
    }

    public Boolean getOnBusiness() {
        return onBusiness;
    }

    public void setOnBusiness(Boolean onBusiness) {
        this.onBusiness = onBusiness;
    }
}
