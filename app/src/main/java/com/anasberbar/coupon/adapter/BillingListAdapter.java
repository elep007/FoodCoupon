package com.anasberbar.coupon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.BillingItem;

import java.util.ArrayList;
import java.util.List;

public class BillingListAdapter extends ArrayAdapter<BillingItem> {

    private Context mContext;
    private List<BillingItem> mItems = new ArrayList<>();

    public BillingListAdapter(Context context, ArrayList<BillingItem> list) {
        super(context, 0 , list);
        mContext = context;
        mItems = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate((position % 2==0)?R.layout.list_item_billing:R.layout.list_item_billing_even, parent, false);
        }

        BillingItem currentMovie = mItems.get(position);

        TextView no = (TextView) listItem.findViewById(R.id.txtNo);
        no.setText(Integer.toString(position+1));

        TextView date = (TextView) listItem.findViewById(R.id.txtDate);
        date.setText(currentMovie.getmDate());

        TextView membership = (TextView) listItem.findViewById(R.id.txtMembership);
        membership.setText(currentMovie.getmMembershipType());

        TextView amount = (TextView) listItem.findViewById(R.id.txtAmount);
        amount.setText(Double.toString(currentMovie.getmAmount()));
        if(currentMovie.getmAmount()>0){
            amount.setTextColor(mContext.getResources().getColor(R.color.colorText1));
        }
        else{
            amount.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }

        return listItem;
    }
}