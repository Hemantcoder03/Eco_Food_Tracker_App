package com.hemant.ecofoodtrackerapp.models;

public class FoodDataModel {

    String itemFoodName, itemDonorProfileId, itemDonateDate,
            itemRateCount, itemDonorProfileImg, itemOrderStatus, itemOrderUid, itemShortDesc, itemExpiryTime, itemFoodImage,
            itemId;
    int itemQuantity;
    LocationModel itemDonorNearbyLoc;


    //It is required otherwise arises problem for fetch data
    public FoodDataModel() {
    }

    public FoodDataModel(String itemFoodName, String itemDonorProfileId, String itemDonateDate, String itemRateCount, String itemDonorProfileImg, String itemOrderStatus, String itemOrderUid, String itemShortDesc, String itemExpiryTime, String itemFoodImage, String itemId, int itemQuantity, LocationModel itemDonorNearbyLoc) {
        this.itemFoodName = itemFoodName;
        this.itemDonorProfileId = itemDonorProfileId;
        this.itemDonateDate = itemDonateDate;
        this.itemRateCount = itemRateCount;
        this.itemDonorProfileImg = itemDonorProfileImg;
        this.itemOrderStatus = itemOrderStatus;
        this.itemOrderUid = itemOrderUid;
        this.itemShortDesc = itemShortDesc;
        this.itemExpiryTime = itemExpiryTime;
        this.itemFoodImage = itemFoodImage;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.itemDonorNearbyLoc = itemDonorNearbyLoc;
    }

    public String getItemFoodName() {
        return itemFoodName;
    }

    public void setItemFoodName(String itemFoodName) {
        this.itemFoodName = itemFoodName;
    }

    public String getItemDonorProfileId() {
        return itemDonorProfileId;
    }

    public void setItemDonorProfileId(String itemDonorProfileId) {
        this.itemDonorProfileId = itemDonorProfileId;
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

    public String getItemOrderStatus() {
        return itemOrderStatus;
    }

    public void setItemOrderStatus(String itemOrderStatus) {
        this.itemOrderStatus = itemOrderStatus;
    }

    public String getItemOrderUid() {
        return itemOrderUid;
    }

    public void setItemOrderUid(String itemOrderUid) {
        this.itemOrderUid = itemOrderUid;
    }

    public String getItemShortDesc() {
        return itemShortDesc;
    }

    public void setItemShortDesc(String itemShortDesc) {
        this.itemShortDesc = itemShortDesc;
    }

    public String getItemExpiryTime() {
        return itemExpiryTime;
    }

    public void setItemExpiryTime(String itemExpiryTime) {
        this.itemExpiryTime = itemExpiryTime;
    }

    public String getItemFoodImage() {
        return itemFoodImage;
    }

    public void setItemFoodImage(String itemFoodImage) {
        this.itemFoodImage = itemFoodImage;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public LocationModel getItemDonorNearbyLoc() {
        return itemDonorNearbyLoc;
    }

    public void setItemDonorNearbyLoc(LocationModel itemDonorNearbyLoc) {
        this.itemDonorNearbyLoc = itemDonorNearbyLoc;
    }
}
