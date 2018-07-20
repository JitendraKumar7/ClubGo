package in.clubgo.app.otp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.listener.SmsBroadcastReceiver;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.utility.Utility;

public class ActivityOTP extends BaseActivity {

    private Bundle bundle;
    private EditText tvOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Intent intent = this.getIntent();
        bundle = intent.getExtras();

        tvOtp = (EditText) findViewById(R.id.otp_txtOtp);

        findViewById(R.id.otp_back).setOnClickListener(this);
        findViewById(R.id.otp_txtVerify).setOnClickListener(this);
        findViewById(R.id.otp_txtReceiveOtp).setOnClickListener(this);
        findViewById(R.id.otp_txtVerifyViaCall).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        SmsBroadcastReceiver.bindListener(new SmsBroadcastReceiver.SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                tvOtp.setText(messageText);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.otp_back:
                onBackPressed();
                break;

            case R.id.otp_txtVerify:
                final String otp = tvOtp.getText().toString().trim();
                if (otp.length() == 0) {
                    tvOtp.setError("Required");
                } else {
                    showProgress();
                    if (bundle.getString(Constants.STATUS).equals("signup")) {
                        webService(bundle.getString(Constants.USER_NAME), bundle.getString(Constants.USER_EMAIL),
                                bundle.getString(Constants.USER_PASSWORD), bundle.getString(Constants.USER_CONTACT),
                                bundle.getString(Constants.USER_DOB), otp);
                    } else {
                        webService(bundle.getString(Constants.USER_NAME),
                                bundle.getString(Constants.USER_EMAIL),
                                bundle.getString(Constants.USER_CONTACT),
                                bundle.getString(Constants.UNIQUE_ID), otp);
                    }
                }
                break;
            case R.id.otp_txtReceiveOtp:
                showProgress();
                webService(Constants.USER_EMAIL, Constants.USER_CONTACT);
                break;
            case R.id.otp_txtVerifyViaCall:
                makeToast("Coming Soon...");
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
        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "otp.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Email Check", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("1")) {
                                makeToast("successfully send again!");
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

    private void webService(final String name, final String email,
                            final String password, final String phone,
                            final String dob, final String otp) {

        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "newsignup.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Sign Up Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("sent").equals("1")) {
                                makeToast("Thank you for Sign up!");
                                Utility.savePref(getApplicationContext(), Constants.USER_ID, jsonObject.getString("id").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_NAME, jsonObject.getString("name").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_EMAIL, jsonObject.getString("email").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_IMAGE, Constants.BaseImage + jsonObject.getString("image").toString().trim());

                                Intent newIntent = new Intent(activity, ActivityHome.class);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(newIntent);
                                finish();

                            } else {
                                //makeToast("Email id Alreay exist!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        makeToast("Network Error!\nPlease try Again");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("pwd", password);
                params.put("phone", phone);
                params.put("dob", dob);
                params.put("anni", "anni");
                params.put("otp", otp);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        try {
            final String str = new String(strRequest.getBody());
            Log.e("Jitendra", str);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void webService(final String name, final String email,
                            final String phone, final String facebookid, final String otp) {

        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "newsignup.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Sign Up Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("sent").equals("1")) {
                                makeToast("Thank you for Sign up!");
                                Utility.savePref(getApplicationContext(), Constants.USER_ID, jsonObject.getString("id").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_NAME, jsonObject.getString("name").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_EMAIL, jsonObject.getString("email").toString().trim());

                                Intent newIntent = new Intent(activity, ActivityHome.class);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(newIntent);
                                finish();

                            } else {
                                //makeToast("Email id Alreay exist!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgress();
                        makeToast("Network Error!\nPlease try Again");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("fbid", facebookid);
                params.put("otp", otp);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        try {
            final String str = new String(strRequest.getBody());
            Log.e("Jitendra", str);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

}
