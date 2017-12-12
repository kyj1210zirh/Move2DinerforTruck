package com.mjc.yhs.move2diner.DTO;


import android.text.TextUtils;

public class MenuListItem {
    private String foodName;
    private String foodDescribe;
    private int foodPrice;
    private String foodStoragePath;

    private int foodEA;//only use PosFragment.

    public MenuListItem(){}
    public MenuListItem(String foodName, String foodDescribe, int foodPrice) {
        this.foodName = foodName;
        this.foodDescribe = foodDescribe;
        this.foodPrice = foodPrice;
    }

    public MenuListItem(String foodName, String foodDescribe, int foodPrice, String foodStoragePath) {
        this.foodName = foodName;
        this.foodDescribe = foodDescribe;
        this.foodPrice = foodPrice;
        this.foodStoragePath = foodStoragePath;
    }

    public int getFoodEA() {
        return foodEA;
    }

    public void setFoodEA(int foodEA) {
        this.foodEA = foodEA;
    }

    public void setFoodName(String name)
    {
        foodName = name;
    }

    public void setFoodDescribe(String describe)
    {
        foodDescribe = describe;
    }

    public void setFoodPrice(int price)
    {
        foodPrice = price;
    }

    public String getFoodName()
    {
        return foodName;
    }

    public String getFoodDescribe()
    {
        return foodDescribe;
    }

    public int getFoodPrice()
    {
        return foodPrice;
    }

    public String getFoodStoragePath() {
        return foodStoragePath;
    }

    public void setFoodStoragePath(String foodStoragePath) {
        this.foodStoragePath = foodStoragePath;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MenuListItem){
            MenuListItem another = (MenuListItem) obj;
            return TextUtils.equals(this.foodName, another.foodName);
        }
        return false;
    }
}
