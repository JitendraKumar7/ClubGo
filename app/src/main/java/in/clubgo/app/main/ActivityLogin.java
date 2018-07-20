package in.clubgo.app.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;
import in.clubgo.app.facebook.ActivityFacebook;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.otp.ActivityOTP;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityLogin extends BaseActivity {

    private ImageView ivFacebook;
    private TextView tvLogin, tvForgot;
    private EditText evEmail, evPassword;

    private int ts, sts;
    private TextView title, subTitle;
    private View loginView1, loginView2, loginView3, loginView4, loginView5;


    private Bundle bundle;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String facebook_id, facebook_name, facebook_gender, facebook_email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);

        inIt();


        bundle = new Bundle();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");

    }

    private void inIt() {


        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.subTitle);
        AppController.getInstance().setFontFamily(title);
        AppController.getInstance().setFontFamily(subTitle);

        evEmail = (EditText) findViewById(R.id.login_txtEmail);
        evPassword = (EditText) findViewById(R.id.login_txtPassword);

        tvLogin = (TextView) findViewById(R.id.login_txtLogin);
        tvForgot = (TextView) findViewById(R.id.login_txtForgotPassword);
        tvLogin.setOnClickListener(this);
        tvForgot.setOnClickListener(this);

        ivFacebook = (ImageView) findViewById(R.id.startup_ivFacebook);
        ivFacebook.setOnClickListener(this);

        SpannableString content = new SpannableString("Forgot Password?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvForgot.setText(content);

        findViewById(R.id.back).setOnClickListener(this);

        loginView1 = findViewById(R.id.login_view1);
        loginView2 = findViewById(R.id.login_view2);
        loginView3 = findViewById(R.id.login_view3);
        loginView4 = findViewById(R.id.login_view4);
        loginView5 = findViewById(R.id.login_view5);


        ts = (int) title.getTextSize();
        sts = (int) subTitle.getTextSize();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(ConnectivityReceiver.isConnected())) {
            showDialog(true);
        }
        attachKeyboardListeners();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_txtLogin:
                final String email = evEmail.getText().toString().trim();
                if (email.length() == 0) {
                    evEmail.setError("Required");
                } else if (!isValidEmail(email)) {
                    evEmail.setError("Invalid Email");
                }

                final String pass = evPassword.getText().toString().trim();
                if (pass.length() == 0) {
                    evPassword.setError("Required");
                } else if (!isValidPassword(pass)) {
                    evPassword.setError("Invalid Password");
                }
                if (isValidEmail(email) && isValidPassword(pass)) {
                    webService(email, pass);
                }
                break;
            case R.id.login_txtForgotPassword:
                mForgotPassword();
                break;

            case R.id.startup_ivFacebook:

                loginButton.performClick();
                loginButton.setPressed(true);
                loginButton.invalidate();
                loginButton.registerCallback(callbackManager, mCallBack);
                loginButton.setPressed(false);
                loginButton.invalidate();
                break;

            default:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onHideKeyboard() {

        Log.e("onHideKeyboard", "onHideKeyboard");
        if (loginView1.getVisibility() == View.GONE &&
                loginView2.getVisibility() == View.GONE) {
            loginView1.setVisibility(View.VISIBLE);
            loginView2.setVisibility(View.VISIBLE);
            loginView3.setVisibility(View.VISIBLE);
            loginView4.setVisibility(View.VISIBLE);
            loginView5.setVisibility(View.VISIBLE);
        }

        if (title != null && subTitle != null) {
            int t = ts / 2;
            int s = sts / 2;
            //title.setTextSize(t);
            //subTitle.setTextSize(s);

            Log.e("title", "Size " + t);
            Log.e("subTitle", "Size " + s);
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {

        Log.e("onShowKeyboard", "onShowKeyboard");
        if (loginView1.getVisibility() == View.VISIBLE &&
                loginView2.getVisibility() == View.VISIBLE) {
            loginView1.setVisibility(View.GONE);
            loginView2.setVisibility(View.GONE);
            loginView3.setVisibility(View.GONE);
            loginView4.setVisibility(View.GONE);
            loginView5.setVisibility(View.GONE);
        }

        if (title != null && subTitle != null) {
            int t = ts / 3;
            int s = sts / 3;
            //title.setTextSize(t);
            //subTitle.setTextSize(s);

            Log.e("title", "Size " + t);
            Log.e("subTitle", "Size " + s);
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

    // forgot password dialog box
    private void mForgotPassword() {
        final Dialog dialog = new Dialog(ActivityLogin.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        final EditText tvEmail = (EditText) dialog.findViewById(R.id.forgot_txtEmail);
        Button sendButton = (Button) dialog.findViewById(R.id.forgot_send_button);
        Button cancleButton = (Button) dialog.findViewById(R.id.forgot_cancel_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvEmail.getText().toString().equals("")) {
                    tvEmail.setError("Required");
                } else if (isValidEmail(tvEmail.getText().toString())) {
                    dialog.dismiss();
                    webService(tvEmail.getText().toString());
                } else {
                    tvEmail.setError("Invalid Email Id");
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // validating email
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9+_.]{4,16}");
        if (pass != null && pass.length() > 4) {
            return true;
        }
        return false;
    }

    // for forgot password
    private void webService(final String email) {
        showProgress();
        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "forget.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Forget Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("sent") == 1) {
                                Toast.makeText(getApplicationContext(),
                                        "Please check your mail\n" +
                                                "Reset password link send your id", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops, Something happened wrong!",
                                        Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    // for email login
    private void webService(final String email, final String password) {
        showProgress();
        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "signuplogin.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Login Response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("id").toString().trim().equals("your password is incorrect")
                                    || jsonObject.getString("id").toString().trim().equals("your emailid is incorrect")) {
                                makeToast("Email id & Password Incorrect!");
                            } else {
                                Utility.savePref(getApplicationContext(), Constants.USER_ID, jsonObject.getString("id").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_NAME, jsonObject.getString("name").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_EMAIL, jsonObject.getString("email").toString().trim());
                                Utility.savePref(getApplicationContext(), Constants.USER_IMAGE, Constants.BaseImage + jsonObject.getString("image").toString().trim());
                                launchIntent(ActivityHome.class, true);

                                ActivityStartUp.finish.finish();
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
                params.put("loginemail", email);
                params.put("loginpassword", password);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
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
                                    webServices(facebook_id);
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
    private void webServices(final String fbid) {
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
                                ActivityStartUp.finish.finish();
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
