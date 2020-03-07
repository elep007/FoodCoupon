package com.anasberbar.coupon.models;

public class CouponHistoryItem {
    private String mDate;
    private int mCouponId;
    private String mFoodname;
    public  CouponHistoryItem(String date, int couponid, String foodname){
        mDate=date;
        mCouponId=couponid;
        mFoodname=foodname;
    }

    public String getmDate() {
        return mDate;
    }

    public int getmCouponId() {
        return mCouponId;
    }

    public String getmFoodname() {
        return mFoodname;
    }
}
