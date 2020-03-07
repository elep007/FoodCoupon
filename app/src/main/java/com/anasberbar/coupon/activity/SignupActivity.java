package com.anasberbar.coupon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {
    CountryCodePicker _ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        addEventListner();
    }

    private void addEventListner(){
        _ccp=findViewById(R.id.ccp);
        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        findViewById(R.id.txtLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLogin();
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        findViewById(R.id.txtInvite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.editPromocode).setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean validate(){
        boolean valid=true;
        EditText _name=(EditText) findViewById(R.id.editName);
        //EditText _email=(EditText) findViewById(R.id.editEmail);
        EditText _mobile=(EditText) findViewById(R.id.editMobile);
        EditText _pin=(EditText) findViewById(R.id.editPIN);

        String name = _name.getText().toString();
        //String email = _email.getText().toString();
        String mobile =_mobile.getText().toString();
        String password = _pin.getText().toString();

        if(name.isEmpty() || name.length()<4){
            _name.setError(getResources().getString(R.string.enter_a_valid_name_at_least_4_characters));
            valid=false;
        }
        else{
            _name.setError(null);
        }
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _email.setError("Enter valid email");
//            valid = false;
//        } else {
//            _email.setError(null);
//        }

        if (mobile.isEmpty() || mobile.length()<8) {
            _mobile.setError(getResources().getString(R.string.enter_valid_phone_number));
            valid = false;
        } else {
            _mobile.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 ) {
            _pin.setError(getResources().getString(R.string.please_enter_password_more_));
            valid = false;
        } else {
            _pin.setError(null);
        }

        return valid;
    }

    private void moveToLogin(){
        Intent intent=new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void onSignupFailed(String message){
        Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
    }
    private void signup(){
        if (!validate()) {
            onSignupFailed(getResources().getString(R.string.enter_your_information_));
            return;
        }

        EditText _name=(EditText) findViewById(R.id.editName);
        //EditText _email=(EditText) findViewById(R.id.editEmail);
        EditText _mobile=(EditText) findViewById(R.id.editMobile);
        EditText _pin=(EditText) findViewById(R.id.editPIN);
        EditText _promocode=(EditText) findViewById(R.id.editPromocode);

        final String name = _name.getText().toString();
        //final String email = _email.getText().toString();
        final String mobile = _ccp.getSelectedCountryCodeWithPlus()+_mobile.getText().toString();
        final String password = _pin.getText().toString();
        final String promocode=_promocode.getText().toString();

        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        final String otp= String.format("%06d", number);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.checking));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();


        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();
        json.addProperty("mobile",mobile);
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"checkexistmobile.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {

                                    Intent intent=new Intent(getBaseContext(),MobileVerifyActivity.class)
                                            .putExtra("name",name)
                                            .putExtra("mobile",mobile)
                                            .putExtra("password",password)
                                            .putExtra("promocode",promocode)
                                            .putExtra("otp",otp);
                                    startActivity(intent);

                                } else if (status.equals("existmobile")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.your_phone_number_has_already_registered_), Toast.LENGTH_LONG).show();
                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.connection_fail_check_your_connection), Toast.LENGTH_LONG).show();
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
