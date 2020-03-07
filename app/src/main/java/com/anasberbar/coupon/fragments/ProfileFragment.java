package com.anasberbar.coupon.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.anasberbar.coupon.LocaleHelper;
import com.anasberbar.coupon.MainActivity;
import com.anasberbar.coupon.R;
import com.anasberbar.coupon.activity.SplashActivity;
import com.anasberbar.coupon.activity.UpgradeMembershipActivity;
import com.anasberbar.coupon.models.Common;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    View mView;
    String mAvatarPath="";
    String mPassword;
    ImageView _image;
    EditText _name;
    TextView _membership;
    TextView _couponCount;
    TextView _expireDate;
    //EditText _email;
    TextView _password;
    TextView _language;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_profile, container, false);

        addEventListener();
        initView();
        //Log.d("test","now");
        return mView;
    }

    private void addEventListener(){
        _image=mView.findViewById(R.id.imgAvatar);
        _name=mView.findViewById(R.id.editName);
        _membership=mView.findViewById(R.id.txtMembership);
        _couponCount=mView.findViewById(R.id.txtCouponCount);
        _expireDate=mView.findViewById(R.id.txtNewDate);
        //_email=mView.findViewById(R.id.editEmail);
        _password=mView.findViewById(R.id.txtPassword);
        _language=mView.findViewById(R.id.txtLanguage);

        mPassword=Common.getInstance().getmUser().getmPassword();

        mView.findViewById(R.id.imageAddAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(ProfileFragment.this).single().start();
            }
        });
        mView.findViewById(R.id.txtLanguage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_language.getText().toString().equals("English")){
                    _language.setText("عربى");
                    //Common.getInstance().getmUser().setmLanguage("عربى");

                    //LocaleHelper.setLocale(mView.getContext(), "ar");

                    //It is required to recreate the activity to reflect the change in UI.

                }
                else{
                    _language.setText("English");
                    //Common.getInstance().getmUser().setmLanguage("English");
                    //LocaleHelper.setLocale(mView.getContext(), "en");

                }
                saveButtonEnable(true);
            }
        });
        mView.findViewById(R.id.txtNewDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=Common.getInstance().getmUser().getmMembership();
                if(Common.getInstance().getmUser().getmMembership().equals("Free")){
                    Intent intent=new Intent(mView.getContext(), UpgradeMembershipActivity.class);
                    startActivityForResult(intent,101);
                }
            }
        });
        mView.findViewById(R.id.txtPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView _txtPin = (TextView) mView.findViewById(R.id.txtPassword);

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mView.getContext());
                final View mView = layoutInflaterAndroid.inflate(R.layout.input_change_password, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mView.getContext());
                alertDialogBuilderUserInput.setView(mView);

                final EditText _currentPin = (EditText) mView.findViewById(R.id.editCurrentPIN);
                final EditText _newPin = (EditText) mView.findViewById(R.id.editNewPIN);
                final EditText _confirmPin = (EditText) mView.findViewById(R.id.editConfirmPIN);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setIcon(R.drawable.ic_password)
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                mPassword=_newPin.getText().toString();
                                if(_currentPin.getText().toString().equals(Common.getInstance().getmUser().getmPassword())){
                                    if(mPassword.length()>4){
                                        if(mPassword.equals(_confirmPin.getText().toString())){
                                            _txtPin.setText(mPassword.substring(0,1)+new String(new char[mPassword.length()-1]).replace("\0", "*"));
                                            saveButtonEnable(true);
                                        }
                                        else{
                                            Common.getInstance().showAlert(mView.getContext(),getResources().getString(R.string.change_password),getResources().getString(R.string.confirm_password_doesnt_match_));
                                        }
                                    }else{
                                        Common.getInstance().showAlert(mView.getContext(),getResources().getString(R.string.change_password),getResources().getString(R.string.please_enter_password_more_));
                                    }
                                }
                                else{
                                    Common.getInstance().showAlert(mView.getContext(),getResources().getString(R.string.change_password),getResources().getString(R.string.current_password_doent_match));
                                }
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                Button nbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.colorText1));
                Button pbutton = alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        mView.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        _name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButtonEnable(true);
            }
        });
//        _email.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                saveButtonEnable(true);
//            }
//        });
    }
    private void saveButtonEnable(boolean value){
        Button _saveButton=mView.findViewById(R.id.btnSave);
        if(value==false){
            _saveButton.setBackgroundColor(Color.GRAY);
            _saveButton.setEnabled(false);
        }
        else{
            _saveButton.setBackgroundColor(getResources().getColor(R.color.colorSuccessButton));
            _saveButton.setEnabled(true);
        }
    }

    private boolean isValidate(){
        boolean valid=true;

        String name =_name.getText().toString();
        //String email =_email.getText().toString();

        if(name.isEmpty() || name.length()<4){
            _name.setError(getResources().getString(R.string.enter_a_valid_name_at_least_4_characters));
            valid=false;
        }
        else{
            _name.setError(null);
        }

//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _email.setError("Enter valid email");
//            valid = false;
//        } else {
//            _email.setError(null);
//        }
        return valid;
    }

    private void saveProfile(){

        if(!isValidate()){
            return;
        }

        JsonObject json = new JsonObject();
        if(!mAvatarPath.isEmpty()){
            Bitmap bm = BitmapFactory.decodeFile(mAvatarPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

            Common.getInstance().getmUser().setmAvatar("image/user_photo/"+Integer.toString(Common.getInstance().getmUser().getmId())+".jpg");
            json.addProperty("avatar",encodedImage);
        }

        json.addProperty("id",Common.getInstance().getmUser().getmId());
        json.addProperty("name",_name.getText().toString());
        json.addProperty("email","");
        json.addProperty("password",mPassword);
        json.addProperty("language",_language.getText().toString());

        final ProgressDialog progressDialog = new ProgressDialog(mView.getContext(), R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.saving));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Ion.with(mView.getContext())
            .load(Common.getInstance().getBaseURL() + "userprofileupdate.php")
            .setJsonObjectBody(json)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    progressDialog.dismiss();

                    String status=result.get("status").getAsString();
                    if(status.equals("ok")){
                        Toast.makeText(mView.getContext(),getResources().getString(R.string.successfully_save),Toast.LENGTH_LONG).show();

                        Common.getInstance().getmUser().setmName(_name.getText().toString());
                        Common.getInstance().getmUser().setmEmail("");
                        Common.getInstance().getmUser().setmPassword(mPassword);

                        if(!mAvatarPath.isEmpty()) {
                            String avatarPath = result.get("avatar").getAsString();
                            Common.getInstance().getmUser().setmAvatar(avatarPath);
                            ((MainActivity)getActivity()).defaultAvatar();
                            mAvatarPath="";
                        }

                        ((MainActivity)getActivity()).initView();

                        if(!_language.getText().toString().equals(Common.getInstance().getmUser().getmLanguage())){
                            Common.getInstance().getmUser().setmLanguage(_language.getText().toString());

                            Intent intent=new Intent(mView.getContext(), MainActivity.class);
                            startActivity(intent);
                            ((MainActivity)getActivity()).finish();
                        }
                        saveButtonEnable(false);
                    }
                    else{
                        Toast.makeText(mView.getContext(),getResources().getString(R.string.fail_saving),Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void initView(){
        Glide.with(this)
                .asBitmap()
                .load(Common.getInstance().getmUser().getmAvatar())
                .into(_image);
        _name.setText(Common.getInstance().getmUser().getmName());
        TextView _mobile=mView.findViewById(R.id.txtMobile);
        _mobile.setText(Common.getInstance().getmUser().getmMobile());
        _couponCount.setText(Integer.toString(Common.getInstance().getCouponCount()));

        if(Common.getInstance().getmUser().getmMembership().equals("Free")){
            _membership.setText(getResources().getString(R.string.free_account));
            _expireDate.setText(getResources().getString(R.string.upgrade_now));
        }
        else{
            _membership.setText(getResources().getString(R.string.premium_account));
            _expireDate.setText(Common.getInstance().getmUser().getmExpireDate()+getResources().getString(R.string.newly));
        }

        //_email.setText(Common.getInstance().getmUser().getmEmail());
        _password.setText(Common.getInstance().getmUser().getPasswordtoShow());
        _language.setText(Common.getInstance().getmUser().getmLanguage());

        saveButtonEnable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            if(image!=null) {
                mAvatarPath=image.getPath();
                _image.setImageURI(Uri.parse(mAvatarPath));
                saveButtonEnable(true);

            }
        }
        if(requestCode==101){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
