package com.anasberbar.coupon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class ForgotActivity extends AppCompatActivity {

    CountryCodePicker _ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        _ccp = findViewById(R.id.ccp);

        addEventListner();
    }

    private void addEventListner() {
        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLoginInfo();

            }
        });
        findViewById(R.id.txtLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLogin();
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void moveToLogin() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void getLoginInfo() {
        EditText _mobile = (EditText) findViewById(R.id.editMobile);
        if(_mobile.getText().toString().length()<8){
            _mobile.setError(getResources().getString(R.string.enter_valid_phone_number));
            return;
        }
        else{
            _mobile.setError(null);
        }
        String mobile = _ccp.getSelectedCountryCodeWithPlus() + _mobile.getText().toString();

        // TODO: Implement your own signup logic here.
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();

        json.addProperty("mobile", mobile);
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL() + "getpassword.php")
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

                                    String mobile = result.get("mobile").getAsString();
                                    String password = result.get("password").getAsString();

                                    sendSMS(mobile, password);

                                } else if (status.equals("nouser")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.you_are_not_registered_), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendSMS(String mobile, String password) {

        JsonObject json = new JsonObject();
        json.addProperty("api_id", "API276483456646");
        json.addProperty("api_password", "elep0077");
        json.addProperty("sms_type", "P");
        json.addProperty("encoding", "T");
        json.addProperty("sender_id", "TSTALA");
        json.addProperty("phonenumber", mobile.substring(1));
        json.addProperty("textmessage", "password " + password);
        try {
            Ion.with(getBaseContext())
                    .load("http://api.smsala.com/api/SendSMS")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("S")) {
                                    //send firebase request
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.your_password_sent_been_on_your_phone), Toast.LENGTH_LONG).show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            moveToLogin();
                                            finish();
                                        }
                                    }, 3000);

                                } else if (status.equals("F")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.message_has_not_sent_), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
