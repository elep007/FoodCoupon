package com.anasberbar.coupon.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Restaurant {
    private int mId;
    private String mName;
    private String mLogo;
    private String mImage;
    private String mAddress;
    private String mPhoneNumber;
    private String mOpenTime;
    private String mDescription;
    private String mPin;
    private String mPosition;
    private int mDistanceFromHere;

    public Restaurant(int id,String name,String logo,String image,String address,String phone,String opentime,String description,String pin,String position){
        mId=id;
        mName=name;
        mLogo=logo;
        mImage=image;
        mAddress=address;
        mPhoneNumber=phone;
        mOpenTime=opentime;
        mDescription=description;
        mPin=pin;

        mPosition=position;
        mDistanceFromHere=0;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public int getmDistanceFromHere() {
        return mDistanceFromHere;
    }

    public String getmLogo() {
        return Common.getInstance().getBaseURL()+mLogo;
    }

    public String getmImage() {
        return Common.getInstance().getBaseURL()+mImage;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public String getmOpenTime() {
        return mOpenTime;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmPin() {
        return mPin;
    }

    public int getCouponCount(){
        int count=0;
        for(Food theFood : Common.getInstance().getmFoods()){
            if(theFood.getmRestaurantId()==mId){
                count+=theFood.getmCouponCount();
            }
        }
        return count;
    }

    public static class Comparators {

        public static Comparator<Restaurant> NAME = new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                String capitalO1=o1.mName.toUpperCase();
                String capitalO2=o2.mName.toUpperCase();
                return capitalO1.compareTo(capitalO2);
            }
        };
        public static Comparator<Restaurant> DISTANCE = new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.mDistanceFromHere - o2.mDistanceFromHere;
            }
        };

    }

    public void updateDistance(double latitude, double longitude){
        String pos[]=mPosition.split(",");
        float[] distances = new float[1];

        if(pos.length>1){
            Location.distanceBetween(latitude, longitude,Double.valueOf(pos[0]), Double.valueOf(pos[1]),distances);
        }
        mDistanceFromHere=(int)distances[0];
    }
}
