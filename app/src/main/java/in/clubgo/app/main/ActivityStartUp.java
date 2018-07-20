package in.clubgo.app.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.defuzed.clubgo.R;

import in.clubgo.app.GPStraker.SimpleLocation;
import in.clubgo.app.app.AppController;
import in.clubgo.app.facebook.ActivityFacebook;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

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

public class ActivityStartUp extends BaseActivity {

    private TextView tvLogin;
    private SimpleLocation location;
    public static ActivityStartUp finish;
    private ImageView ivFacebook, ivPhone;

    private Bundle bundle;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String facebook_id, facebook_name, facebook_gender, facebook_email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utility.getPref(getApplicationContext(), Constants.USER_ID, null) != null) {
            launchIntent(ActivityHome.class, true);
        }
        // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_startup);

        if (ActivityHome.jitendra_soam == null) {

            if (!checkFineLocationPermission() || !checkCoarseLocationPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE,
                                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1);
            } else {

                // construct a new instance of SimpleLocation
                location = new SimpleLocation(this);
                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    location.showSettingsAlert(this);

                } else if (Utility.getPref(getApplicationContext(), Constants.USER_LOCATION, null) == null) {
                    Utility.savePref(getApplicationContext(), Constants.USER_LATITUDE, String.valueOf(location.getLatitude()));
                    Utility.savePref(getApplicationContext(), Constants.USER_LONGITUDE, String.valueOf(location.getLongitude()));
                    Utility.savePref(getApplicationContext(), Constants.USER_CITY, String.valueOf(location.getLocality(this)));
                    Utility.savePref(getApplicationContext(), Constants.USER_ADDRESS, String.valueOf(location.getAddressLine(this)));
                }

            }
        } else {
            Log.e("Jitendra Saom", "Jitendra Soam");
        }
        ActivityStartUp.finish = this;

        inIt();
    }

    private void inIt() {


        bundle = new Bundle();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");


        TextView title = (TextView) findViewById(R.id.title);
        TextView subTitle = (TextView) findViewById(R.id.subTitle);

        AppController.getInstance().setFontFamily(title);
        AppController.getInstance().setFontFamily(subTitle);

        tvLogin = (TextView) findViewById(R.id.startup_txtLogin);
        ivPhone = (ImageView) findViewById(R.id.startup_ivPhone);
        ivFacebook = (ImageView) findViewById(R.id.startup_ivFacebook);

        tvLogin.setOnClickListener(this);
        ivPhone.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);

        TextView tvSkip = (TextView) findViewById(R.id.startup_txtSkip);
        tvSkip.setOnClickListener(this);

        SpannableString content = new SpannableString("Skip for now");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvSkip.setText(content);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(ConnectivityReceiver.isConnected())) {
            showDialog(true);
        }

        if (location != null) {
            // make the device update its location
            location.beginUpdates();
        }
    }

    @Override
    protected void onPause() {
        if (location != null) {
            // stop location updates (saves battery)
            location.endUpdates();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.startup_txtLogin:
                launchIntent(ActivityLogin.class, false);
                break;

            case R.id.startup_ivPhone:
                launchIntent(ActivitySignUp.class, false);
                break;

            case R.id.startup_ivFacebook:

                loginButton.performClick();
                loginButton.setPressed(true);
                loginButton.invalidate();
                loginButton.registerCallback(callbackManager, mCallBack);
                loginButton.setPressed(false);
                loginButton.invalidate();

                break;
            case R.id.startup_txtSkip:
                launchIntent(ActivityHome.class, true);
                break;

            default:
                Log.e("default", "default");
                break;
        }
    }

    public boolean checkFineLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkCoarseLocationPermission() {
        String permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            hideDialog();
        } else {
            showDialog(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // construct a new instance of SimpleLocation
                    location = new SimpleLocation(this);
                    if (!location.hasLocationEnabled()) {
                        // ask the user to enable location access
                        SimpleLocation.openSettings(this);

                    } else if (Utility.getPref(getApplicationContext(), Constants.USER_LOCATION, null) == null) {
                        Utility.savePref(getApplicationContext(), Constants.USER_LATITUDE, String.valueOf(location.getLatitude()));
                        Utility.savePref(getApplicationContext(), Constants.USER_LONGITUDE, String.valueOf(location.getLongitude()));
                        Utility.savePref(getApplicationContext(), Constants.USER_CITY, String.valueOf(location.getLocality(this)));
                        Utility.savePref(getApplicationContext(), Constants.USER_ADDRESS, String.valueOf(location.getAddressLine(this)));
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // App code
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            LoginManager.getInstance().logOut();
                            Log.e("Facebook Response: ", object + " J");

                            try {
                                facebook_id = object.getString("id").toString();
                                facebook_name = object.getString("name").toString();
                                facebook_gender = object.getString("gender").toString();
                                bundle.putString(Constants.UNIQUE_ID, facebook_id);
                                bundle.putString(Constants.USER_NAME, facebook_name);
                                if (object.has("email")) {
                                    facebook_email = object.getString("email").toString();
                                    bundle.putString(Constants.USER_EMAIL, facebook_email);

                                    String USER_IMAGE = "https://graph.facebook.com/" + facebook_id + "/picture?type=large";
                                    Utility.savePref(getApplication(), Constants.USER_IMAGE, USER_IMAGE);
                                    webService(facebook_id);
                                } else {
                                    Log.e("facebook_email", "Email id issue");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            //pDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            //pDialog.dismiss();
        }
    };

    // for facebook login
    private void webService(final String fbid) {
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
                                Utility.savePref(getApplicationContext(), Constants.USER_ID, jsonObject.getString("id").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_NAME, jsonObject.getString("name").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_EMAIL, jsonObject.getString("email").toString().trim());
                                launchIntent(ActivityHome.class, true);
                            } else {
                                launchIntent(ActivityFacebook.class, bundle, false);
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
                params.put("fbid", fbid);
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
