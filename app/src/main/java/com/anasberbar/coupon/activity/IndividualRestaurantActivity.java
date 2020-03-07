package com.anasberbar.coupon.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.adapter.FoodAdapter;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.Food;
import com.anasberbar.coupon.models.Restaurant;
import com.bumptech.glide.Glide;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class IndividualRestaurantActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener{
    private int mId;
    private Restaurant mRestaurant;
    private List<Food> mFoods=new ArrayList<>();
    private int currentCouponId;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_restaurant);

        mId=getIntent().getIntExtra("restaurantId",0);
        mRestaurant=Common.getInstance().getCurrentRestaurant(mId);
        addEventListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getFoodsOfThis();
        initView();
    }

    public void addEventListener(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mRestaurant.getmName());
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
        findViewById(R.id.imgCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mRestaurant.getmPhoneNumber()));
                startActivity(intent);
            }
        });
        findViewById(R.id.imgAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q=an+address+city"));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/?saddr=25.258145,51.535955&daddr=28.258145,51.535955"));

                startActivity(intent);
            }
        });
    }
    //temp
    private void getFoodsOfThis(){
        for(Food theFood : Common.getInstance().getmFoods()){
            if(theFood.getmRestaurantId()==mId){
                mFoods.add(theFood);
            }
        }
    }
    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(mFoods.get(positionInDataSet));
    }
    private void onItemChanged(Food food) {
        //Toast.makeText(this,coupon.getmName(),Toast.LENGTH_LONG).show();
    }
    private void initView(){

        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new FoodAdapter(this,mFoods));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(500);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        ImageView _image=findViewById(R.id.imgRestaurant);
        ImageView _logo=findViewById(R.id.imgLogo);
        TextView _name=findViewById(R.id.txtRestaurantName);
        TextView _openTime=findViewById(R.id.txtOpenTime);
        TextView _phoneNumber=findViewById(R.id.txtPhoneNumber);
        TextView _couponCount=findViewById(R.id.txtCouponCount);
        TextView _description=findViewById(R.id.txtDescription);
        Glide.with(this)
                .asBitmap()
                .load(mRestaurant.getmImage())
                .into(_image);
        Glide.with(this)
                .asBitmap()
                .load(mRestaurant.getmLogo())
                .into(_logo);
        _name.setText(mRestaurant.getmName());
        _openTime.setText(mRestaurant.getmOpenTime());
        _phoneNumber.setText(mRestaurant.getmPhoneNumber());
        _couponCount.setText(Integer.toString(mRestaurant.getCouponCount()));
        _description.setText(mRestaurant.getmDescription());
    }
}
