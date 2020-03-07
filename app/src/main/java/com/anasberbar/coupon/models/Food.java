package com.anasberbar.coupon.models;

import java.util.ArrayList;

public class Food {
    private int mId;
    private int mRestaurantId;
    private String mName;
    private String mImage;
    private String mDescription;
    private String mNeedDescription;
    //user variable
    private int mCouponCount=0;
    private ArrayList<Integer> mCouponIds=new ArrayList<>();
    private boolean mIsFavorite;

    public Food(int id,int restaurantId,String name,String image,String description,String needdescription){
        mId=id;
        mRestaurantId=restaurantId;
        mName=name;
        mImage=image;
        mDescription=description;
        mNeedDescription=needdescription;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmImage() {
        return Common.getInstance().getBaseURL()+mImage;
    }

    public int getmCouponCount() {
        return mCouponIds.size();
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmNeedDescription() {
        return mNeedDescription;
    }

    public int getmRestaurantId() {
        return mRestaurantId;
    }

    public void addCoupon(int couponid){
        mCouponIds.add(couponid);
    }

    public void setmCouponIds(ArrayList<Integer> mCouponIds) {
        this.mCouponIds = mCouponIds;
    }

    public void setmIsFavorite(boolean mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }

    public boolean ismIsFavorite() {
        return mIsFavorite;
    }

    public void  removeCoupon(int couponId){
        int index=mCouponIds.indexOf(couponId);
        if(index>=0){
            mCouponIds.remove(index);
        }
    }

    public Restaurant getmRestaurant(){
        int index=0;
        for(Restaurant theRestaurant : Common.getInstance().getmRestaurants()){
            if(theRestaurant.getmId()==mRestaurantId){
                index=Common.getInstance().getmRestaurants().indexOf(theRestaurant);
                break;
            }
        }
        return Common.getInstance().getmRestaurants().get(index);
    }
}
