package com.anasberbar.coupon.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;

import static android.content.Context.CLIPBOARD_SERVICE;

public class InviteFragment extends Fragment {
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_invite, container, false);
        addEventListener();
        initView();
        return mView;
    }

    private void addEventListener(){
        mView.findViewById(R.id.imgCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.content.ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Source Text", Common.getInstance().getmUser().getmPromoCode());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(mView.getContext(),getResources().getString(R.string.copied_to_clipboard),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(){
        TextView _promocode=mView.findViewById(R.id.txtPromoCode);
        _promocode.setText(Common.getInstance().getmUser().getmPromoCode());
    }

}
