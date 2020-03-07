package com.anasberbar.coupon.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.activity.FoodActivity;
import com.anasberbar.coupon.adapter.FavoriteFoodAdapter;
import com.anasberbar.coupon.models.Common;
import com.anasberbar.coupon.models.Food;
import com.anasberbar.coupon.models.Restaurant;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    View mView;
    ListView _listFavorite;
    ArrayList<Food> mFavoriteFoods=new ArrayList<>();
    FavoriteFoodAdapter mAdapter;
    int mSelectedPosition=-1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_favorite, container, false);
        _listFavorite=mView.findViewById(R.id.lstFavorites);

        addEventListener();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getFavoriteFood();
    }

    private void addEventListener(){
        _listFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mView.getContext(), FoodActivity.class).putExtra("foodId",mFavoriteFoods.get(position).getmId());
                startActivity(intent);
            }
        });
        _listFavorite.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(mView.getContext())
                        .setIcon(R.drawable.ic_logo)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getResources().getString(R.string.are_you_sure_))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeFavorite(mFavoriteFoods.get(position).getmId());

                                Common.getInstance().getCurrentFood(mFavoriteFoods.get(position).getmId()).setmIsFavorite(false);
                                mFavoriteFoods.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();

                return true;
            }
        });
    }
    private void getFavoriteFood(){
        mFavoriteFoods.clear();
        for(Food theFood: Common.getInstance().getmFoods()){
            if(theFood.ismIsFavorite()){
                mFavoriteFoods.add(theFood);
            }
        }

        mAdapter = new FavoriteFoodAdapter(mView.getContext(), mFavoriteFoods);
        _listFavorite.setAdapter(mAdapter);

    }

    private void removeFavorite(int foodId){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getmUser().getmId());
        json.addProperty("foodid",foodId);
        Ion.with(mView.getContext())
                .load(Common.getInstance().getBaseURL() + "foodremovefavorite.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        String status=result.get("status").getAsString();
                        if(status.equals("ok")){
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.success_remove),Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.fail_try_again),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
