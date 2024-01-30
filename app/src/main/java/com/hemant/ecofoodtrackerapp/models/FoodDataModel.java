package com.hemant.ecofoodtrackerapp.models;

public class FoodDataModel {

    String itemFoodName, itemDonorProfileName, itemDonorNearbyLoc, itemDonateDate, itemRateCount, itemDonorProfileImg;

    //It is required otherwise arises problem for fetch data
    public FoodDataModel(){}

    public FoodDataModel(String itemFoodName, String itemDonorProfileName, String itemDonorNearbyLoc, String itemDonateDate, String itemRateCount, String itemDonorProfileImg) {
        this.itemFoodName = itemFoodName;
        this.itemDonorProfileName = itemDonorProfileName;
        this.itemDonorNearbyLoc = itemDonorNearbyLoc;
        this.itemDonateDate = itemDonateDate;
        this.itemRateCount = itemRateCount;
        this.itemDonorProfileImg = itemDonorProfileImg;
    }

    public String getItemFoodName() {
        return itemFoodName;
    }

    public void setItemFoodName(String itemFoodName) {
        this.itemFoodName = itemFoodName;
    }

    public String getItemDonorProfileName() {
        return itemDonorProfileName;
    }

    public void setItemDonorProfileName(String itemDonorProfileName) {
        this.itemDonorProfileName = itemDonorProfileName;
    }

    public String getItemDonorNearbyLoc() {
        return itemDonorNearbyLoc;
    }

    public void setItemDonorNearbyLoc(String itemDonorNearbyLoc) {
        this.itemDonorNearbyLoc = itemDonorNearbyLoc;
    }

    public String getItemDonateDate() {
        return itemDonateDate;
    }

    public void setItemDonateDate(String itemDonateDate) {
        this.itemDonateDate = itemDonateDate;
    }

    public String getItemRateCount() {
        return itemRateCount;
    }

    public void setItemRateCount(String itemRateCount) {
        this.itemRateCount = itemRateCount;
    }

    public String getItemDonorProfileImg() {
        return itemDonorProfileImg;
    }

    public void setItemDonorProfileImg(String itemDonorProfileImg) {
        this.itemDonorProfileImg = itemDonorProfileImg;
    }

}
