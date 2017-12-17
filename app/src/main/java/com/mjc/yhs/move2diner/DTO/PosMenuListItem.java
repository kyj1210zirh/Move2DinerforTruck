package com.mjc.yhs.move2diner.DTO;

/**
 * Created by Kang on 2017-12-18.
 */

public class PosMenuListItem {
    private String foodName;
    private int foodPrice;
    private String foodStoragePath;
    private String foodID;
    private int foodEA;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodStoragePath() {
        return foodStoragePath;
    }

    public void setFoodStoragePath(String foodStoragePath) {
        this.foodStoragePath = foodStoragePath;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public int getFoodEA() {
        return foodEA;
    }

    public void setFoodEA(int foodEA) {
        this.foodEA = foodEA;
    }
}
