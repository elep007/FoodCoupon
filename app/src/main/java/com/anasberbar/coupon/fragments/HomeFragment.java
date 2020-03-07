package com.anasberbar.coupon.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.anasberbar.coupon.R;
import com.anasberbar.coupon.activity.RestaurantActivity;
import com.anasberbar.coupon.adapter.RecyclerViewAdapter;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.Food;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private SliderLayout mDemoSlider;
    View mView;
    ArrayList<Food> mSpecial=new ArrayList<>();
    ArrayList<Food> mMostLoved=new ArrayList<>();
    List<String> mAdsImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_home, container, false);

        getAdsImage();
        getCouponData();
        initView();

        return mView;
    }
    private void initView(){
        //slider initiation
        mDemoSlider = (SliderLayout) mView.findViewById(R.id.slider);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
        //set first screen
        TextSliderView textSliderView = new TextSliderView(mView.getContext());
        textSliderView.image(R.drawable.first_slide).setScaleType(BaseSliderView.ScaleType.CenterCrop);
        mDemoSlider.addSlider(textSliderView);

        for(String name : mAdsImages){
            textSliderView = new TextSliderView(mView.getContext());
            // initialize a SliderLayout
            textSliderView.image(name).setScaleType(BaseSliderView.ScaleType.CenterCrop);

            mDemoSlider.addSlider(textSliderView);
        }
        //
        mDemoSlider.stopAutoCycle();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDemoSlider.startAutoCycle();
            }
        },15000);

        mDemoSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), RestaurantActivity.class);
                startActivity(intent);
            }
        });

        //Special coupon , most loved init
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = mView.findViewById(R.id.recSpecial);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), mSpecial);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView1 = mView.findViewById(R.id.recMost);
        recyclerView1.setLayoutManager(layoutManager1);
        RecyclerViewAdapter adapter1 = new RecyclerViewAdapter(getContext(), mMostLoved);
        recyclerView1.setAdapter(adapter1);

        mView.findViewById(R.id.imgRestaurant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), RestaurantActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getAdsImage(){
        mAdsImages.add( "http://The-work-kw.com/image/ads/ads2.jpg");
        mAdsImages.add( "http://The-work-kw.com/image/ads/ads3.jpg");
        mAdsImages.add( "http://The-work-kw.com/image/ads/ads4.jpg");
        mAdsImages.add( "http://The-work-kw.com/image/ads/ads5.jpg");

    }
    private void getCouponData(){
        mSpecial= Common.getInstance().getmFoods();

        mMostLoved=Common.getInstance().getmFoods();
    }

}
