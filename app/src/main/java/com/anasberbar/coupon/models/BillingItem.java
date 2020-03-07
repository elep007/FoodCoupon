package com.anasberbar.coupon.models;

public class BillingItem {
    private String mDate;
    private String mMembershipType;
    private double mAmount;
    public  BillingItem(String date, String membership, double amount){
        mDate=date;
        mMembershipType=membership;
        mAmount=amount;
    }

    public double getmAmount() {
        return mAmount;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmMembershipType() {
        return mMembershipType;
    }
}
