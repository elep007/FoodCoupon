package com.anasberbar.coupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Food;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFoodAdapter extends ArrayAdapter<Food> {

    private Context mContext;
    private List<Food> mFoods = new ArrayList<>();

    public FavoriteFoodAdapter(Context context, ArrayList<Food> list) {
        super(context, 0 , list);
        mContext = context;
        mFoods = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_coupon,parent,false);

        Food currentMovie = mFoods.get(position);

        if(!currentMovie.getmImage().trim().isEmpty()) {
            final ImageView image = (ImageView) listItem.findViewById(R.id.imgFood);
            String imagePath = currentMovie.getmImage();
            Glide.with(mContext)
                    .load(imagePath)
                    .into(image);
        }

        TextView name = (TextView) listItem.findViewById(R.id.txtCouponName);
        name.setText(currentMovie.getmName());

        TextView address = (TextView) listItem.findViewById(R.id.txtRestaurantName);
        address.setText(currentMovie.getmRestaurant().getmName());

        TextView distance = (TextView) listItem.findViewById(R.id.txtDistance);
        distance.setText(Integer.toString(currentMovie.getmRestaurant().getmDistanceFromHere())+"km");

        TextView count = (TextView) listItem.findViewById(R.id.txtCouponCount);
        count.setText(Integer.toString(currentMovie.getmCouponCount()));

        return listItem;
    }
}