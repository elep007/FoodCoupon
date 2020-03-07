package com.anasberbar.coupon.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.activity.PaymentActivity;
import com.anasberbar.coupon.adapter.BillingListAdapter;
import com.anasberbar.coupon.models.BillingItem;
import com.anasberbar.coupon.models.Common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class BillingFragment extends Fragment {
    ListView _lstBilling;
    TextView _currendBalance;
    BillingListAdapter mAdapter;
    ArrayList<BillingItem> mTransactions=new ArrayList<>();
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_billing, container, false);
        _lstBilling=mView.findViewById(R.id.lstBilling);

        addEventListener();
        initView();
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode==102){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addEventListener(){
        mView.findViewById(R.id.imgCoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mView.getContext(), PaymentActivity.class);
                startActivityForResult(intent,101);
            }
        });
        TabLayout _tabLayout = (TabLayout) mView.findViewById(R.id.tabBilling);
        _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<BillingItem> temp=new ArrayList<>();
                switch(tab.getPosition()) {
                    case 0:
                        temp=mTransactions;
                        break;
                    case 1:
                        for(BillingItem theItem:mTransactions){
                            if(theItem.getmAmount()>0){
                                temp.add(theItem);
                            }
                        }
                        break;
                    case 2:
                        for(BillingItem theItem:mTransactions){
                            if(theItem.getmAmount()<0){
                                temp.add(theItem);
                            }
                        }
                        break;
                }
                mAdapter = new BillingListAdapter(mView.getContext(), temp);
                _lstBilling.setAdapter(mAdapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView(){
        getBillingData();
        _currendBalance=mView.findViewById(R.id.txtWallet);
        _currendBalance.setText(String.format(Locale.US,"%.2f",Common.getInstance().getmUser().getmWallet()));
    }

    public void getBillingData(){
        final ProgressDialog progressDialog = new ProgressDialog(mView.getContext(), R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("userid", Common.getInstance().getmUser().getmId());
        Ion.with(mView.getContext())
                .load(Common.getInstance().getBaseURL() + "gettransaction.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();

                        String status=result.get("status").getAsString();
                        if(status.equals("ok")){
                            mTransactions.clear();
                            JsonArray transactionsJson = result.get("transactions").getAsJsonArray();
                            for (JsonElement transactionElement : transactionsJson) {
                                JsonObject theTransaction = transactionElement.getAsJsonObject();

                                String paydate = theTransaction.get("paydate").getAsString();
                                String transaction = theTransaction.get("transaction").getAsString();
                                double amount = theTransaction.get("amount").getAsDouble();
                                String type = theTransaction.get("type").getAsString();
                                if(type.equals("spent")){
                                    amount=-1*amount;
                                }

                                mTransactions.add(new BillingItem(paydate,transaction,amount));
                            }
                            Collections.reverse(mTransactions);
                            mAdapter = new BillingListAdapter(mView.getContext(), mTransactions);
                            _lstBilling.setAdapter(mAdapter);
                        }
                        else if(status.equals("notransaction")){

                        }
                        else{
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.connection_fail_check_your_connection),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
