package com.mjc.yhs.move2diner.DTO;

import java.util.ArrayList;

public class TruckInfoItem {
    private String truckName,truckDes,thumbnail;
    private Boolean payCard;
    private ArrayList<String> tags=new ArrayList<>();


    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Boolean getPayCard() {
        return payCard;
    }

    public void setPayCard(Boolean payCard) {
        this.payCard = payCard;
    }

    public TruckInfoItem(){}
    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getTruckDes() {
        return truckDes;
    }

    public void setTruckDes(String truckDes) {
        this.truckDes = truckDes;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    public TruckInfoItem(String truckName, String truckDes, String thumbnail) {
        this.truckName = truckName;
        this.truckDes = truckDes;
        this.thumbnail = thumbnail;
    }
}
