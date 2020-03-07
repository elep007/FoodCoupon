package com.anasberbar.coupon.models;

import java.util.Date;

public class User {
    int mId;
    String mName;
    String mEmail;
    String mMobile;
    String mPassword;
    String mLanguage;
    String mAvatar;
    String mMembership;
    String mStartDate;
    String mExpireDate;
    double mWallet;
    String mPromoCode;

    public User(int id,String name, String email, String mobile, String password, String language, String avatar,String startDate, String date, double wallet,String promocode){
        mId=id;
        mName=name;
        mEmail=email;
        mMobile=mobile;
        mPassword=password;
        mLanguage=language;
        mAvatar=avatar;
        mMembership=(date.equals("free"))?"Free":"Premium";
        mStartDate=startDate;
        mExpireDate=(date.equals("free"))?"Upgrade now":date;
        mWallet=wallet;
        mPromoCode=promocode;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmAvatar() {
        return Common.getInstance().getBaseURL()+mAvatar;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmExpireDate() {
        return mExpireDate;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public String getmMembership() {
        return mMembership;
    }

    public String getmMobile() {
        return mMobile;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getPasswordtoShow(){
        String temp=mPassword.substring(0,1)+new String(new char[mPassword.length()-1]).replace("\0", "*");
        return temp;
    }
    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public void setmWallet(double mWallet) {
        this.mWallet = mWallet;
    }

    public double getmWallet() {
        return mWallet;
    }

    public String getmStartDate() {
        return mStartDate;
    }

    public void setmStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public void setmExpireDate(String mExpireDate) {
        this.mExpireDate = mExpireDate;
    }

    public void setmMembership(String mMembership) {
        this.mMembership = mMembership;
    }

    public String getmPromoCode() {
        return mPromoCode;
    }
}
