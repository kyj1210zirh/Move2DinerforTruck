package com.mjc.yhs.move2diner.DTO;

public class TruckInfoItem {
    private String truckName,truckDes,thumbnail,busiInfo;
    private Boolean payCard;


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

    public String getBusiInfo() {
        return busiInfo;
    }

    public void setBusiInfo(String busiInfo) {
        this.busiInfo = busiInfo;
    }



    public TruckInfoItem(String truckName, String truckDes, String thumbnail, String busiInfo) {
        this.truckName = truckName;
        this.truckDes = truckDes;
        this.thumbnail = thumbnail;
        this.busiInfo = busiInfo;
    }
}
