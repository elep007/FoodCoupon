package com.anasberbar.coupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.CouponHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class CouponHistoryItemAdapter extends ArrayAdapter<CouponHistoryItem> {

    private Context mContext;
    private List<CouponHistoryItem> mItems = new ArrayList<>();

    public CouponHistoryItemAdapter(Context context, ArrayList<CouponHistoryItem> list) {
        super(context, 0 , list);
        mContext = context;
        mItems = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate((position % 2==0)?R.layout.list_item_history:R.layout.list_item_history_even,parent,false);

        CouponHistoryItem currentMovie = mItems.get(position);

        TextView no = (TextView) listItem.findViewById(R.id.txtNo);
        no.setText(Integer.toString(position+1));

        TextView date = (TextView) listItem.findViewById(R.id.txtDate);
        date.setText(currentMovie.getmDate());

        TextView couponid = (TextView) listItem.findViewById(R.id.txtCouponId);
        couponid.setText(Integer.toString(currentMovie.getmCouponId()));

        TextView foodname = (TextView) listItem.findViewById(R.id.txtCouponName);
        foodname.setText(currentMovie.getmFoodname());
        return listItem;
    }
}