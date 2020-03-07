package com.anasberbar.coupon.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.Food;
import com.anasberbar.coupon.models.Restaurant;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class FoodActivity extends AppCompatActivity {
    private int mFoodId;
    private Food mFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        mFoodId=getIntent().getIntExtra("foodId",0);
        mFood = Common.getInstance().getCurrentFood(mFoodId);

        addEventListener();
        initView();
    }

    public void addEventListener(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mFood.getmName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backbutton);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
        findViewById(R.id.imgCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mFood.getmRestaurant().getmPhoneNumber()));
                startActivity(intent);
            }
        });
        findViewById(R.id.imgAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/?saddr=25.258145,51.535955&daddr=28.258145,51.535955"));
                startActivity(intent);
            }
        });
        findViewById(R.id.useCoupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),UseCouponActivity.class)
                        .putExtra("foodId",mFood.getmId())
                        .putExtra("restaurantId",mFood.getmRestaurantId());
                startActivityForResult(intent,101);
            }
        });
        findViewById(R.id.upgradeMembership).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),UpgradeMembershipActivity.class);
                startActivityForResult(intent,101);
            }
        });
        findViewById(R.id.imgFavor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView _favor=findViewById(R.id.imgFavor);
                _favor.setVisibility(View.INVISIBLE);
                mFood.setmIsFavorite(true);

                JsonObject json = new JsonObject();
                json.addProperty("userid",Common.getInstance().getmUser().getmId());
                json.addProperty("foodid",mFoodId);

                Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL() + "foodsetfavorite.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            String status=result.get("status").getAsString();
                            if(status.equals("ok")){
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.this_food_is_added_in_your_favorites),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.connection_fail_check_your_connection),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode==102){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView(){
        ImageView _image=findViewById(R.id.imgFood);
        Glide.with(this)
            .asBitmap()
            .load(mFood.getmImage())
            .into(_image);
        ImageView _logo=findViewById(R.id.imgLogo);
        Glide.with(this)
                .asBitmap()
                .load(mFood.getmRestaurant().getmLogo())
                .into(_logo);
        ImageView _favor=findViewById(R.id.imgFavor);
        if(mFood.ismIsFavorite()){
            _favor.setVisibility(View.INVISIBLE);
        }


        if(Common.getInstance().getmUser().getmMembership().equals("Free")){
            findViewById(R.id.useCoupon).setVisibility(View.GONE);
            findViewById(R.id.upgradeMembership).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.upgradeMembership).setVisibility(View.GONE);
            findViewById(R.id.useCoupon).setVisibility(View.VISIBLE);
        }

        Button _use=findViewById(R.id.useCoupon);
        if(mFood.getmCouponCount()==0){
            _use.setEnabled(false);
            _use.setBackgroundColor(Color.GRAY);
        }
        else{
            _use.setEnabled(true);
            _use.setBackgroundColor(getResources().getColor(R.color.colorButtonSave));
        }
        TextView _name=findViewById(R.id.txtFoodName);
        _name.setText(mFood.getmName());
        TextView _restaurantName=findViewById(R.id.txtRestaurantName);
        _restaurantName.setText(mFood.getmRestaurant().getmName());
        TextView _couponCount=findViewById(R.id.txtCouponCount);
        _couponCount.setText(Integer.toString(mFood.getmCouponCount()));
        TextView _phone=findViewById(R.id.txtPhoneNumber);
        _phone.setText(mFood.getmRestaurant().getmPhoneNumber());
        TextView _description=findViewById(R.id.txtDescription);
        _description.setText(mFood.getmDescription());
        TextView _needDescription=findViewById(R.id.txtNeedDescription);
        _needDescription.setText(mFood.getmNeedDescription());
        TextView _openTime=findViewById(R.id.txtOpenTime);
        _openTime.setText(mFood.getmRestaurant().getmOpenTime());
    }
}
