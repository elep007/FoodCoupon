package com.anasberbar.coupon.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;

import com.anasberbar.coupon.models.Food;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;


public class UseCouponActivity extends AppCompatActivity {
    OtpView otpView;
    int mRestaurantId;
    int mFoodId;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_coupon);

        mFoodId=getIntent().getIntExtra("foodId",0);
        mRestaurantId=getIntent().getIntExtra("restaurantId",0);
        otpView = findViewById(R.id.otp_view);
        mContext=this;
        addEventListener();

    }
    private void addEventListener(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.use_coupon));
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

        ImageView _logo=findViewById(R.id.imgLogo);
        Glide.with(this)
                .asBitmap()
                .load(Common.getInstance().getCurrentRestaurant(mRestaurantId).getmLogo())
                .into(_logo);

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {

            }
        });

        findViewById(R.id.btnTakeCoupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Bright_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getResources().getString(R.string.checking));
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setCancelable(false);
                progressDialog.show();

                JsonObject json = new JsonObject();
                json.addProperty("userid", Common.getInstance().getmUser().getmId());
                json.addProperty("foodid", mFoodId);
                json.addProperty("restaurantid",mRestaurantId);
                json.addProperty("restaurantpin",otpView.getText().toString());

                Ion.with(mContext)
                        .load(Common.getInstance().getBaseURL() + "takecoupon.php")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                if(result!=null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        //Toast.makeText(mContext, getResources().getString(R.string.coupon_is_taken_successfully), Toast.LENGTH_LONG).show();
                                        int couponid = result.get("couponid").getAsInt();
                                        Food currentFood = Common.getInstance().getCurrentFood(mFoodId);
                                        currentFood.removeCoupon(couponid);

                                        String msg="Coupon ID :"+couponid+"\n"+getResources().getString(R.string.coupon_is_taken_successfully);
                                        Common.getInstance().showAlert(mContext,getResources().getString(R.string.app_name),msg);

                                        setResult(102);
                                    } else if (status.equals("wrong")) {
                                        //Toast.makeText(mContext, getResources().getString(R.string.wrong_pin_try_again), Toast.LENGTH_LONG).show();
                                        Common.getInstance().showAlert(mContext,getResources().getString(R.string.app_name),getResources().getString(R.string.wrong_pin_try_again));
                                    } else {
                                        Toast.makeText(mContext, getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
                                    }
                                }
                                else{
                                    Toast.makeText(mContext, getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

}
