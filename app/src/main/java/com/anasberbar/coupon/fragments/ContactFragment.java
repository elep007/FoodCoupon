package com.anasberbar.coupon.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ContactFragment extends Fragment {
    View mView;
    EditText _title;
    EditText _message;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_contact, container, false);

        addEventListener();
        initView();

        return mView;
    }

    private void addEventListener(){
        mView.findViewById(R.id.imgSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _title=mView.findViewById(R.id.editMessageTitle);
                _message=mView.findViewById(R.id.editMessage);
                sendMessage(_title.getText().toString(),_message.getText().toString());
            }
        });
    }

    private void sendMessage(String title, String message){
        if(title.trim().isEmpty()){
            _title.setError("Enter title");
            return;
        }
        else{
            _title.setError(null);
        }
        if(message.trim().isEmpty()){
            _message.setError("Enter Message");
            return;
        }
        else{
            _message.setError(null);
        }

        final ProgressDialog progressDialog = new ProgressDialog(mView.getContext(), R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.sending));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getmUser().getmId());
        json.addProperty("title",title);
        json.addProperty("message",message);
        json.addProperty("useremail",Common.getInstance().getmUser().getmEmail());
        Ion.with(mView.getContext())
                .load(Common.getInstance().getBaseURL() + "sendmessage.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        String status=result.get("status").getAsString();
                        if(status.equals("ok")){
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.success_sent),Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mView.getContext(),getResources().getString(R.string.fail_try_again),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initView(){
        TextView _email=mView.findViewById(R.id.txtEmail);
        TextView _phone=mView.findViewById(R.id.txtMobile);

        _email.setText(Common.getInstance().getCONTACT_EMAIL());
        _phone.setText(Common.getInstance().getCONTACT_PHONE());
    }
}
