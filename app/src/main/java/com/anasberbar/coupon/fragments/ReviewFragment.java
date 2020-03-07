package com.anasberbar.coupon.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.adapter.BillingListAdapter;
import com.anasberbar.coupon.adapter.CouponHistoryItemAdapter;
import com.anasberbar.coupon.models.BillingItem;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.CouponHistoryItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;

public class ReviewFragment extends Fragment {
    View mView;
    ListView _listHistory;
    CouponHistoryItemAdapter mAdapter;

    ArrayList<CouponHistoryItem> mHistoryItems=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_review, container, false);
        _listHistory=mView.findViewById(R.id.lstReviews);

        getCouponHistoryData();
        return mView;
    }
    public void getCouponHistoryData(){
        final ProgressDialog progressDialog = new ProgressDialog(mView.getContext(), R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("userid", Common.getInstance().getmUser().getmId());
        Ion.with(mView.getContext())
                .load(Common.getInstance().getBaseURL() + "gethistory.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();

                        String status=result.get("status").getAsString();
                        if(status.equals("ok")){
                            mHistoryItems.clear();
                            JsonArray historyJson = result.get("histories").getAsJsonArray();
                            for (JsonElement historyElement : historyJson) {
                                JsonObject theHistory = historyElement.getAsJsonObject();

                                String date = theHistory.get("date").getAsString();
                                int couponid = theHistory.get("couponid").getAsInt();
                                String foodname = theHistory.get("foodname").getAsString();
                                mHistoryItems.add(new CouponHistoryItem(date,couponid,foodname));
                            }
                            Collections.reverse(mHistoryItems);
                            mAdapter = new CouponHistoryItemAdapter(mView.getContext(), mHistoryItems);
                            _listHistory.setAdapter(mAdapter);
                        }
                        else if(status.equals("nohistory")){

                        }
                        else{
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.fail_try_again),Toast.LENGTH_LONG).show();
                        }
                    }
                });

        mAdapter=new CouponHistoryItemAdapter(mView.getContext(),mHistoryItems);
        _listHistory.setAdapter(mAdapter);
    }
}
