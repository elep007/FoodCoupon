package com.anasberbar.coupon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.anasberbar.coupon.LocaleHelper;
import com.anasberbar.coupon.MainActivity;
import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    CountryCodePicker _ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _ccp=findViewById(R.id.ccp);
        addEventListener();
    }
    private void addEventListener(){
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        findViewById(R.id.txtReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), ForgotActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.txtSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        findViewById(R.id.imgLanguage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Configuration configuration = getResources().getConfiguration();
                if(LocaleHelper.getLanguage(getBaseContext()).equals("ar")){
                    LocaleHelper.setLocale(getBaseContext(), "en");
                    configuration.setLayoutDirection(new Locale("en"));
                }
                else{
                    LocaleHelper.setLocale(getBaseContext(), "ar");
                    configuration.setLayoutDirection(new Locale("ar"));
                }
                getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

                Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validate(){
        boolean valid=true;
        EditText _mobile=(EditText) findViewById(R.id.editMobile);
        EditText _pin=(EditText) findViewById(R.id.editPIN);

        String mobile =_mobile.getText().toString();
        String password = _pin.getText().toString();

        if (mobile.isEmpty() || mobile.length()<8) {
            _mobile.setError(getResources().getString(R.string.enter_valid_phone_number));
            valid = false;
        } else {
            _mobile.setError(null);
        }

        if (password.isEmpty() || password.length() < 1 ) {
            _pin.setError(getResources().getString(R.string.please_enter_password));
            valid = false;
        } else {
            _pin.setError(null);
        }

        return valid;
    }
    private void onLoginSuccess(){
        Intent intent=new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void onLoginFailed(String message){
        Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
    }
    private void login(){
        if (!validate()) {
            onLoginFailed(getResources().getString(R.string.enter_your_information_));
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.authenticating));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        EditText _mobile=(EditText) findViewById(R.id.editMobile);
        EditText _pin=(EditText) findViewById(R.id.editPIN);

        String mobile = _ccp.getSelectedCountryCodeWithPlus()+_mobile.getText().toString();
        String password = _pin.getText().toString();
        String androidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);

        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();

        json.addProperty("mobile",mobile);
        json.addProperty("password",password);
        json.addProperty("deviceid",androidDeviceId);
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"userverify.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    //read user data
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

                                    onLoginSuccess();
                                } else if (status.equals("nouser")) {
                                    onLoginFailed(getResources().getString(R.string.you_are_not_registered_));
                                }else if(status.equals("wrongpassword")){
                                    onLoginFailed(getResources().getString(R.string.wrong_password_try_again));
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
