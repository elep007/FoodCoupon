package com.anasberbar.coupon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Restaurant;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private Context mContext;
    private List<Restaurant> mRestaurants = new ArrayList<>();

    public RestaurantListAdapter(Context context, ArrayList<Restaurant> list) {
        super(context, 0 , list);
        mContext = context;
        mRestaurants = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_restaurant,parent,false);

        Restaurant currentMovie = mRestaurants.get(position);

        if(!currentMovie.getmLogo().trim().isEmpty()) {
            final ImageView image = (ImageView) listItem.findViewById(R.id.imgLogo);
            String imagePath = currentMovie.getmLogo();
            Ion.with(mContext)
                    .load(imagePath)
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (e == null) {
                                image.setImageBitmap(result);
                            }
                        }
                    });
        }

        TextView name = (TextView) listItem.findViewById(R.id.txtName);
        name.setText(currentMovie.getmName());

        TextView address = (TextView) listItem.findViewById(R.id.txtAddress);
        address.setText(currentMovie.getmAddress());

        TextView distance = (TextView) listItem.findViewById(R.id.txtDistance);
        distance.setText(Integer.toString(currentMovie.getmDistanceFromHere()/1000)+"km");

        TextView count = (TextView) listItem.findViewById(R.id.txtCouponCount);
        count.setText(Integer.toString(currentMovie.getCouponCount()));

        return listItem;
    }
}