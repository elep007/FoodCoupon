package com.anasberbar.coupon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class MobileVerifyActivity extends AppCompatActivity {

    String mName;
    //String mEmail;
    String mMobile;
    String mPassword;
    String mPromocode;
    String motp;

    private EditText _editTextCode;

    //private String mVerificationId;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verify);

        mName=getIntent().getStringExtra("name");
        //mEmail=getIntent().getStringExtra("email");
        mMobile=getIntent().getStringExtra("mobile");
        mPassword=getIntent().getStringExtra("password");
        mPromocode=getIntent().getStringExtra("promocode");
        motp=getIntent().getStringExtra("otp");

        //sendSMS();
        Toast.makeText(getBaseContext(),motp,Toast.LENGTH_LONG).show();

        //mAuth = FirebaseAuth.getInstance();
        _editTextCode=findViewById(R.id.editVerificationCode);

        //sendVerificationCode(mMobile);

        TextView _mobile=findViewById(R.id.txtPhoneNumber);
        _mobile.setText(mMobile);

        addEventListner();

    }

//    private void sendVerificationCode(String mobile) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                mobile,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallbacks);
//    }
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//
//            //Getting the code sent by SMS
//            String code = phoneAuthCredential.getSmsCode();
//
//            //sometime the code is not detected automatically
//            //in this case the code will be null
//            //so user has to manually enter the code
//            if (code != null) {
//                _editTextCode.setText(code);
//                //verifying the code
//                //verifyVerificationCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(MobileVerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            Log.d("error_phone",e.getMessage());
//        }
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//
//            //storing the verification id that is sent to the user
//            mVerificationId = s;
//        }
//    };
//
//
//    private void verifyVerificationCode(String code) {
//        //creating the credential
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//
//        //signing the user
//        signInWithPhoneAuthCredential(credential);
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(MobileVerifyActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            //verification successful we will start the profile activity
//                            signup();
//
//                        } else {
//
//                            //verification unsuccessful.. display an error message
//
//                            String message = "Somthing is wrong, we will fix it soon...";
//
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                message = "Invalid code entered...";
//                            }
//
//                            Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }

    private void addEventListner(){
        findViewById(R.id.btnVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = _editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    _editTextCode.setError("Enter valid code");
                    _editTextCode.requestFocus();
                    return;
                }

                if(code.equals(motp)){
                    signup();
                }
                else{
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_try_again), Toast.LENGTH_LONG).show();
                }

            }
        });

        findViewById(R.id.txtSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getBaseContext(),SignupActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });

        findViewById(R.id.btnResend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send verification code with sms
                sendSMS();
                findViewById(R.id.btnResend).setEnabled(false);
            }
        });

    }

    private void sendSMS(){

        JsonObject json = new JsonObject();
        json.addProperty("api_id","API276483456646");
        json.addProperty("api_password","elep0077");
        json.addProperty("sms_type","P");
        json.addProperty("encoding","T");
        json.addProperty("sender_id","TSTALA");
        json.addProperty("phonenumber",mMobile.substring(1));
        json.addProperty("textmessage","verification code "+motp);
        //json.addProperty("textmessage","Hi, Ahmed. I am XiangYi from freelancer. I am not available whatsapp. can u contact on skype? my skype id is prodev12345(mail: prodev12345@yandex.com). please contact in skype. your reply in freelancer is so slow :) I am waiting you. thanks");
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
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.verification_code_has_been_sent_on_your_phone), Toast.LENGTH_LONG).show();
                                } else if (status.equals("F")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.message_has_not_sent_), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_try_again), Toast.LENGTH_LONG).show();
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

    private void signup(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.saving));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();


        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();
        json.addProperty("name",mName);
        json.addProperty("email","");
        json.addProperty("mobile",mMobile);
        json.addProperty("password",mPassword);
        json.addProperty("promocode",mPromocode);

        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"userregister.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(),getResources().getString(R.string.you_are_successfully_registered),Toast.LENGTH_LONG).show();

                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },1000);

                                } else if (status.equals("existmobile")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.your_phone_number_has_already_registered_), Toast.LENGTH_LONG).show();
                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_try_again), Toast.LENGTH_LONG).show();
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
