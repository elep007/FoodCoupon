package com.anasberbar.coupon.activity;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.anasberbar.coupon.MainActivity;
import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.Food;
import com.anasberbar.coupon.models.Restaurant;
import com.anasberbar.coupon.models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    String loadFinish="fail";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getData();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loadFinish.equals("login")) {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(loadFinish.equals("main")) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
    private void getData(){
        JsonObject json = new JsonObject();
        String androidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        json.addProperty("deviceid",androidDeviceId);

        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"getdata.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    loadFinish="login";
                                    //read restaurant data
                                    ArrayList<Restaurant> restaurants=new ArrayList<>();
                                    JsonArray restaurantsJson = result.get("restaurants").getAsJsonArray();
                                    for (JsonElement restaurantElement : restaurantsJson) {
                                        JsonObject theRestaurant = restaurantElement.getAsJsonObject();

                                        int id = theRestaurant.get("id").getAsInt();
                                        String name = theRestaurant.get("name").getAsString();
                                        String logo = theRestaurant.get("logo").getAsString();
                                        String image = theRestaurant.get("image").getAsString();
                                        String address = theRestaurant.get("address").getAsString();
                                        String position = theRestaurant.get("position").getAsString();
                                        String phone = theRestaurant.get("phone").getAsString();
                                        String opentime = theRestaurant.get("opentime").getAsString();
                                        String description = theRestaurant.get("description").getAsString();
                                        String pin=theRestaurant.get("pin").getAsString();
                                        restaurants.add(new Restaurant(id,name,logo,image,address,phone,opentime,description,pin,position));
                                    }
                                    Common.getInstance().setmRestaurants(restaurants);

                                    ArrayList<Food> foods=new ArrayList<>();
                                    JsonArray foodsJson = result.get("foods").getAsJsonArray();
                                    for (JsonElement foodElement : foodsJson) {
                                        JsonObject theFood = foodElement.getAsJsonObject();

                                        int id = theFood.get("id").getAsInt();
                                        int restaurantId = theFood.get("restaurantid").getAsInt();
                                        String name = theFood.get("name").getAsString();
                                        String image = theFood.get("image").getAsString();
                                        String description = theFood.get("description").getAsString();
                                        String needdescription = theFood.get("needdescription").getAsString();
                                        foods.add(new Food(id,restaurantId,name,image,description,needdescription));
                                    }
                                    Common.getInstance().setmFoods(foods);

                                    double MONTHLY_FEE=result.get("monthly_fee").getAsDouble();
                                    double YEARLY_FEE=result.get("yearly_fee").getAsDouble();
                                    String contact_email=result.get("contact_email").getAsString();
                                    String contact_phone=result.get("contact_phone").getAsString();

                                    Common.getInstance().setMONTHLY_FEE(MONTHLY_FEE);
                                    Common.getInstance().setYEARLY_FEE(YEARLY_FEE);
                                    Common.getInstance().setCONTACT_EMAIL(contact_email);
                                    Common.getInstance().setCONTACT_PHONE(contact_phone);
                                    //add special data here

                                    //add most loved data here

                                    //user data
                                    String session = result.get("session").getAsString();
                                    if(session.equals("ok")){
                                        JsonObject userJson = result.get("user").getAsJsonObject();

                                        int id=userJson.get("id").getAsInt();
                                        String name=userJson.get("name").getAsString();
                                        String email=userJson.get("email").getAsString();
                                        String mobile=userJson.get("mobile").getAsString();
                                        String password=userJson.get("password").getAsString();
                                        String avatar=userJson.get("avatar").getAsString();
                                        String language=userJson.get("language").getAsString();
                                        String startdate=userJson.get("startdate").getAsString();
                                        String expiredate=userJson.get("expiredate").getAsString();
                                        String promocode=userJson.get("promocode").getAsString();
                                        double wallet=userJson.get("wallet").getAsDouble();

                                        User user=new User(id,name,email,mobile,password,language,avatar,startdate,expiredate,wallet,promocode);
                                        Common.getInstance().setmUser(user);

                                        JsonArray couponIdsJson=result.get("coupons").getAsJsonArray();
                                        JsonArray foodIdsJson=result.get("foodids").getAsJsonArray();
                                        JsonArray favoritesJson=result.get("favorites").getAsJsonArray();

                                        ArrayList<Integer> couponIds=new ArrayList<>();
                                        ArrayList<Integer> foodIds=new ArrayList<>();
                                        ArrayList<Integer> favorites=new ArrayList<>();

                                        for(JsonElement theCouponElement : couponIdsJson){
                                            couponIds.add(theCouponElement.getAsInt());
                                        }
                                        for(JsonElement theFoodIdElement : foodIdsJson){
                                            foodIds.add(theFoodIdElement.getAsInt());
                                        }
                                        for(JsonElement theFavariteElement : favoritesJson){
                                            favorites.add(theFavariteElement.getAsInt());
                                        }
                                        Common.getInstance().setCoupons(couponIds,foodIds);
                                        Common.getInstance().setFavoriteFood(favorites);

                                        loadFinish="main";
                                    }

                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(),getResources().getString(R.string.connection_fail_check_your_connection),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.connection_fail_check_your_connection),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
