package in.clubgo.app.facebook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.main.ActivityStartUp;
import in.clubgo.app.otp.ActivityOTP;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

public class ActivityFacebook extends BaseActivity {

    private Bundle bundle;
    private EditText txtNumber;
    private TextView tvProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        Intent intent = this.getIntent();
        bundle = intent.getExtras();

        inIt();
    }

    private void inIt() {

        findViewById(R.id.back).setOnClickListener(this);
        txtNumber = (EditText) findViewById(R.id.facebook_txtNumber);
        tvProceed = (TextView) findViewById(R.id.facebook_txtProceed);
        txtNumber.setOnClickListener(this);
        tvProceed.setOnClickListener(this);

        txtNumber.clearFocus();
        txtNumber.setFocusable(false);
        txtNumber.setFocusableInTouchMode(false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.facebook_txtNumber:
                txtNumber.setFocusable(true);
                txtNumber.setFocusableInTouchMode(true);
                break;
            case R.id.facebook_txtProceed:
                String number = txtNumber.getText().toString().trim();
                if (number.length() <= 9) {
                    txtNumber.setError("Required");
                } else {
                    bundle.putString(Constants.USER_CONTACT, number);
                    webService(bundle.getString(Constants.USER_EMAIL), number);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideDialog();
        } else {
            showDialog(true);
        }
    }

    private void webService(final String email, final String mobile) {
        showProgress();
        final String URLs = Constants.BaseURL + "otp.php";
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Facebook id", response);
                        // We can logout from facebook by calling following
                        dismissProgress();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("1")) {
                                bundle.putString(Constants.STATUS, "facebook");
                                launchIntent(ActivityOTP.class, bundle, false);
                            } else {
                                makeToast(jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                        dismissProgress();
                        makeToast("Network Error!\nPlease try Again");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("mobile", mobile);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

}
