package com.anasberbar.coupon.activity;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Locale;

public class UpgradeMembershipActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_membership);
        mContext=this;

        addEventListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initView();
    }

    private void addEventListener(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.upgrade_membership);
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
        findViewById(R.id.btnUpgradeMonthly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.upgrade_membership)
                        .setMessage(getResources().getString(R.string.are_you_sure_you_want_to_upgrade_to_monthly_))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //upgrade membership
                                upgradeMembership("monthly");
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        });
        findViewById(R.id.btnUpgradeYearly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.upgrade_membership))
                        .setMessage(getResources().getString(R.string.are_you_sure_you_want_to_upgrade_to_yearly_))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //upgrade membership
                                upgradeMembership("yearly");
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        });
        findViewById(R.id.imgCoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        Button _upgradeMonthly=findViewById(R.id.btnUpgradeMonthly);
        String upgradeMonthly=getResources().getString(R.string.upgrade_monthly);
        _upgradeMonthly.setText(upgradeMonthly.replace("4.99",Double.toString(Common.getInstance().getMONTHLY_FEE())));
        Button _upgradeYearly=findViewById(R.id.btnUpgradeYearly);
        String upgradeYearly=getResources().getString(R.string.upgrade_yearly);
        _upgradeYearly.setText(upgradeYearly.replace("49.9",Double.toString(Common.getInstance().getYEARLY_FEE())));
        if(Common.getInstance().getmUser().getmWallet()<Common.getInstance().getMONTHLY_FEE() || Common.getInstance().getmUser().getmMembership().equals("Premium")){
            findViewById(R.id.txtMonthlyAvailable).setVisibility(View.VISIBLE);
            findViewById(R.id.btnUpgradeMonthly).setEnabled(false);
            findViewById(R.id.btnUpgradeMonthly).setBackgroundColor(Color.GRAY);
        }
        else{
            findViewById(R.id.txtMonthlyAvailable).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnUpgradeMonthly).setEnabled(true);
        }

        if(Common.getInstance().getmUser().getmWallet()<Common.getInstance().getYEARLY_FEE() || Common.getInstance().getmUser().getmMembership().equals("Premium")){
            findViewById(R.id.txtYearlyAvailable).setVisibility(View.VISIBLE);
            findViewById(R.id.btnUpgradeYearly).setEnabled(false);
            findViewById(R.id.btnUpgradeYearly).setBackgroundColor(Color.GRAY);
        }
        else{
            findViewById(R.id.txtYearlyAvailable).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnUpgradeYearly).setEnabled(true);
        }
        TextView _wallet=findViewById(R.id.txtWallet);
        _wallet.setText(String.format(Locale.US,"%.2f",Common.getInstance().getmUser().getmWallet()));
    }

    private void upgradeMembership(String membership){
        final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Upgrading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("id", Common.getInstance().getmUser().getmId());
        json.addProperty("membership",membership);
        Ion.with(getBaseContext())
                .load(Common.getInstance().getBaseURL() + "upgrademembership.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if(result!=null) {
                            String status = result.get("status").getAsString();
                            if (status.equals("ok")) {
                                double wallet = result.get("wallet").getAsDouble();
                                String startDate = result.get("startdate").getAsString();
                                String expireDate = result.get("expiredate").getAsString();

                                Common.getInstance().getmUser().setmWallet(wallet);
                                Common.getInstance().getmUser().setmStartDate(startDate);
                                Common.getInstance().getmUser().setmExpireDate(expireDate);
                                Common.getInstance().getmUser().setmMembership("Premium");
                                //read coupon data
                                if (result.has("coupons")) {
                                    JsonArray couponIdsJson = result.get("coupons").getAsJsonArray();
                                    JsonArray foodIdsJson = result.get("foodids").getAsJsonArray();

                                    ArrayList<Integer> couponIds = new ArrayList<>();
                                    ArrayList<Integer> foodIds = new ArrayList<>();

                                    for (JsonElement theCouponElement : couponIdsJson) {
                                        couponIds.add(theCouponElement.getAsInt());
                                    }
                                    for (JsonElement theFoodIdElement : foodIdsJson) {
                                        foodIds.add(theFoodIdElement.getAsInt());
                                    }
                                    Common.getInstance().setCoupons(couponIds, foodIds);
                                }

                                initView();
                                setResult(102);
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.successfully_upgraded), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_try_again), Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_try_again), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
