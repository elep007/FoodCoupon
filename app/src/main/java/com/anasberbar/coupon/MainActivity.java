package com.anasberbar.coupon;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.activity.LoginActivity;
import com.anasberbar.coupon.activity.PaymentActivity;
import com.anasberbar.coupon.fragments.BillingFragment;
import com.anasberbar.coupon.fragments.ContactFragment;
import com.anasberbar.coupon.fragments.FavoriteFragment;
import com.anasberbar.coupon.fragments.HomeFragment;
import com.anasberbar.coupon.fragments.InviteFragment;
import com.anasberbar.coupon.fragments.ProfileFragment;
import com.anasberbar.coupon.fragments.ReviewFragment;
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
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//language set
        Configuration configuration = getResources().getConfiguration();
        if(Common.getInstance().getmUser().getmLanguage().equals("English")){
            LocaleHelper.setLocale(getBaseContext(), "en");
            configuration.setLayoutDirection(new Locale("en"));
        }
        else{
            LocaleHelper.setLocale(getBaseContext(), "ar");
            configuration.setLayoutDirection(new Locale("ar"));
        }
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
//==============
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getData();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        hView.findViewById(R.id.txtCurrentBalance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        moveToHome();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initView();
        getData();
    }

    private void moveToHome(){
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle(R.string.app_name);
    }

    public void defaultAvatar(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        final ImageView _avatar = (ImageView) hView.findViewById(R.id.imgAvatar);
        _avatar.setImageResource(R.drawable.sample_photo);
    }

    public void initView() {
        //nav init
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        final ImageView _avatar = (ImageView) hView.findViewById(R.id.imgAvatar);
        String avatarPath =  Common.getInstance().getmUser().getmAvatar();
        Ion.with(this)
                .load(avatarPath)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (e == null) {
                            _avatar.setImageBitmap(result);
                        }
                    }
                });

        TextView _name = (TextView) hView.findViewById(R.id.txtName);
        _name.setText(Common.getInstance().getmUser().getmName());
        TextView _membership=(TextView) hView.findViewById(R.id.txtMembership);
        _membership.setText(Common.getInstance().getmUser().getmMembership().equals("Free")?getResources().getString(R.string.free_account):getResources().getString(R.string.premium_account));
        TextView _wallet = (TextView) hView.findViewById(R.id.txtCurrentBalance);
        _wallet.setText(String.format(Locale.US,"%.2f",Common.getInstance().getmUser().getmWallet()));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (toolbar.getTitle().equals(getResources().getString(R.string.app_name))) {
                super.onBackPressed();
            } else {
                moveToHome();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            toolbar.setTitle(R.string.app_name);
            toolbar.setBackgroundColor(Color.TRANSPARENT);
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            toolbar.setTitle(R.string.menu_profile);

        } else if (id == R.id.nav_favorite) {
            fragment = new FavoriteFragment();
            toolbar.setTitle(R.string.menu_favorite);
        } else if (id == R.id.nav_billing) {
            fragment = new BillingFragment();
            toolbar.setTitle(R.string.menu_billing);
        } else if (id == R.id.nav_coupon) {
            fragment = new ReviewFragment();
            toolbar.setTitle(R.string.menu_review);
        } else if (id == R.id.nav_invite) {
            fragment = new InviteFragment();
            toolbar.setTitle(R.string.menu_invite);
        } else if (id == R.id.nav_contact) {
            fragment = new ContactFragment();
            toolbar.setTitle(R.string.menu_contact);
        } else if (id == R.id.nav_logout) {
            moveToLogin();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void moveToLogin() {
        JsonObject json = new JsonObject();
        json.addProperty("mobile",Common.getInstance().getmUser().getmMobile());

        Ion.with(getBaseContext())
            .load(Common.getInstance().getBaseURL() + "userlogout.php")
            .setJsonObjectBody(json)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {

                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
    }

    private void getData(){

        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getmUser().getmId());

        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"getfulldata.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
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

                                        initView();
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
