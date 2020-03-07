package com.anasberbar.coupon.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anasberbar.coupon.R;
import com.anasberbar.coupon.models.Common;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "paymentExample";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AQQLvdR26OxNhCsQq_Glz3zmW8qjQ7xYZQfRyPbXFoRRFpwQvyZNQiRK63pi6Os6DTarZu-imCZFXOa-";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .rememberUser(false);

    TextView _currentBalance;
    EditText _chargeAmount;
    Button _makePayment;
    double mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        addEventListener();
        initView();
    }

    private void addEventListener() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.charge_wallet));
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

        _currentBalance=findViewById(R.id.txtCurrentBalance);
        _chargeAmount=findViewById(R.id.editChargeAmount);
        _makePayment=findViewById(R.id.btnPayment);

        findViewById(R.id.btnPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAmount=Double.valueOf(_chargeAmount.getText().toString());
                if(mAmount<=0) return;
                PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(mAmount/Common.getInstance().getEGP_rate()), "USD", "Deposit",PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(getBaseContext(), com.paypal.android.sdk.payments.PaymentActivity.class);
                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        });
    }
    private void initView(){
        _currentBalance.setText(String.format(Locale.US,"%.2f",Common.getInstance().getmUser().getmWallet()));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        setResult(102);
                        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage(getResources().getString(R.string.saving));
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        JsonObject json = new JsonObject();
                        json.addProperty("userid",Common.getInstance().getmUser().getmId());
                        json.addProperty("amount",mAmount);
                        try {
                            Ion.with(this)
                                    .load(Common.getInstance().getBaseURL()+"chargewallet.php")
                                    .setJsonObjectBody(json)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            progressDialog.dismiss();
                                            if (result != null) {
                                                String status = result.get("status").getAsString();
                                                if (status.equals("ok")) {
                                                    Double wallet=result.get("wallet").getAsDouble();
                                                    Common.getInstance().getmUser().setmWallet(wallet);
                                                    _currentBalance.setText(String.format(Locale.US,"%.2f",Common.getInstance().getmUser().getmWallet()));
                                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.success_charging), Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.fail_saving), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                        }catch(Exception e){
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        //Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                        Toast.makeText(getBaseContext(), "an extremely unlikely failure occurred:"+e, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Log.i(TAG, "The user canceled.");
                Toast.makeText(getBaseContext(), getResources().getString(R.string.user_canceled), Toast.LENGTH_LONG).show();
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getBaseContext(), getResources().getString(R.string.an_invalid_payment_), Toast.LENGTH_LONG).show();
            }
        }
    }
}
