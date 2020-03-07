package com.anasberbar.coupon.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class Common {

    private String baseURL="http://10.0.2.2/food_admin/backend/";//"http://The-work-kw.com/food/backend/";//
    private ArrayList<Food> mFoods;
    private ArrayList<Restaurant> mRestaurants;
    private ArrayList<Integer> mFavorites;
    private double MONTHLY_FEE = 4.99;
    private double YEARLY_FEE = 49;
    private double EGP_rate=16.51;
    private String CONTACT_EMAIL;
    private String CONTACT_PHONE;

    User mUser;

    private static Common instance = new Common();

    public void Common(){

    }

    public static Common getInstance()
    {
        return instance;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User user){
        mUser=user;
    }


    public ArrayList<Restaurant> getmRestaurants() {
        return mRestaurants;
    }

    public void setmRestaurants(ArrayList<Restaurant> mRestaurants) {
        this.mRestaurants = mRestaurants;
    }

    public ArrayList<Food> getmFoods() {
        return mFoods;
    }

    public Food getCurrentFood(int foodId){
        for(Food theFood : mFoods){
            if(theFood.getmId()==foodId){
                return theFood;
            }
        }
        return null;
    }

    public Restaurant getCurrentRestaurant(int restaurantId){
        for(Restaurant theRestaurant : mRestaurants){
            if(theRestaurant.getmId()==restaurantId){
                return theRestaurant;
            }
        }
        return null;
    }

    public int getCouponCount(){
        int couponCount=0;
        for(Food theFood : mFoods){
            couponCount+=theFood.getmCouponCount();
        }
        return couponCount;
    }
    public void setmFoods(ArrayList<Food> mFoods) {
        this.mFoods = mFoods;
    }

    public void setCoupons(ArrayList<Integer> couponIds,ArrayList<Integer> foodIds){
        //reset old coupon
        for(Food theFood : mFoods){
            theFood.setmCouponIds(new ArrayList<Integer>());
        }
        for(int theFoodId: foodIds){
            for(Food theFood : mFoods){
                if(theFoodId==theFood.getmId()){
                    int index=foodIds.indexOf(theFoodId);
                    theFood.addCoupon(couponIds.get(index));
                    break;
                }
            }
        }
    }

    public void setFavoriteFood(ArrayList<Integer> favorites){
        for(int theFoodId: favorites){
            for(Food theFood : mFoods){
                if(theFoodId==theFood.getmId()){
                    theFood.setmIsFavorite(true);
                    break;
                }
            }
        }
    }

    public void showAlert(Context context, String title, String message){
        new android.app.AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_logo)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.ok, null)
                .show();
    }

    public void setMONTHLY_FEE(double MONTHLY_FEE) {
        this.MONTHLY_FEE = MONTHLY_FEE;
    }

    public void setYEARLY_FEE(double YEARLY_FEE) {
        this.YEARLY_FEE = YEARLY_FEE;
    }

    public double getMONTHLY_FEE() {
        return MONTHLY_FEE;
    }

    public double getYEARLY_FEE() {
        return YEARLY_FEE;
    }

    public String getCONTACT_EMAIL() {
        return CONTACT_EMAIL;
    }

    public void setCONTACT_EMAIL(String CONTACT_EMAIL) {
        this.CONTACT_EMAIL = CONTACT_EMAIL;
    }

    public String getCONTACT_PHONE() {
        return CONTACT_PHONE;
    }

    public void setCONTACT_PHONE(String CONTACT_PHONE) {
        this.CONTACT_PHONE = CONTACT_PHONE;
    }

    public double getEGP_rate() {
        return EGP_rate;
    }

    public ProgressDialog waitingDialogShow(Context context,String msg){
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public void updateDistance(double latitude, double longitude){
        for(Restaurant theRestaurant : mRestaurants){
            theRestaurant.updateDistance(latitude,longitude);
        }
    }
}
