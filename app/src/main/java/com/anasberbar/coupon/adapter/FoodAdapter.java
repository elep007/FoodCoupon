package com.anasberbar.coupon.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.activity.FoodActivity;
import com.anasberbar.coupon.models.Food;
import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by yarolegovich on 07.03.2017.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> data;
    private Context mContext;

    public FoodAdapter(Context context, List<Food> data) {
        mContext=context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_coupon_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Glide.with(holder.itemView.getContext())
                .load(data.get(position).getmImage())
                .into(holder._image);
        holder._name.setText(data.get(position).getmName());
        holder._couponCount.setText(Integer.toString(data.get(position).getmCouponCount()));
        holder._couponDescription.setText(data.get(position).getmDescription());

        if(data.get(position).ismIsFavorite()){
            holder._imgFavor.setVisibility(View.VISIBLE);
        }
        else{
            holder._imgFavor.setVisibility(View.INVISIBLE);
        }
        //to show onclick coupon temp
        final String name=data.get(position).getmName();

        holder._image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, FoodActivity.class).putExtra("foodId",data.get(position).getmId());
                mContext.startActivity(intent);
            }
        });
//        holder._imgFavor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,name+" successfully added in your favorite list.",Toast.LENGTH_LONG).show();
//            }
//        });
        //--------------
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView _image;
        private ImageView _imgFavor;
        private TextView _name;
        private TextView _couponCount;
        private TextView _couponDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            _image = (ImageView) itemView.findViewById(R.id.imgCoupon);
            _imgFavor=(ImageView) itemView.findViewById(R.id.imgFavor);
            _name=(TextView) itemView.findViewById(R.id.txtCouponName);
            _couponCount=(TextView) itemView.findViewById(R.id.txtCouponCount);
            _couponDescription=(TextView) itemView.findViewById(R.id.txtCouponDescription);
        }
    }
}
