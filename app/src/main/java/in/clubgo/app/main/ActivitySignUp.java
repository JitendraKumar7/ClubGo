package in.clubgo.app.main;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.otp.ActivityOTP;
import in.clubgo.app.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivitySignUp extends BaseActivity {

    private int ts, sts;
    private Bundle bundle;
    private CheckBox checkBox;
    private TextView tvBtnSignUp;
    private TextView title, subTitle;
    private View signup_view1, signup_view2;
    private EditText evName, evContact, evEmail, evConfirmpass, evPassword, evDOB, evAnniversary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_sign_up);


        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.subTitle);
        AppController.getInstance().setFontFamily(title);
        AppController.getInstance().setFontFamily(subTitle);

        evName = (EditText) findViewById(R.id.signup_txtName);
        evContact = (EditText) findViewById(R.id.signup_txtContact);
        evEmail = (EditText) findViewById(R.id.signup_txtEmail);
        evConfirmpass = (EditText) findViewById(R.id.signup_txtConfirmPasss);
        evPassword = (EditText) findViewById(R.id.signup_txtPassword);
        evDOB = (EditText) findViewById(R.id.signup_txtDOB);
        evAnniversary = (EditText) findViewById(R.id.signup_txtAnniversary);

        checkBox = (CheckBox) findViewById(R.id.signup_checkBox);

        tvBtnSignUp = (TextView) findViewById(R.id.signup_txtBtnSignUp);
        tvBtnSignUp.setOnClickListener(this);

        ImageView ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ts = (int) title.getTextSize();
        sts = (int) subTitle.getTextSize();

        signup_view1 = findViewById(R.id.signup_view1);
        signup_view2 = findViewById(R.id.signup_view2);

    }

    @Override
    public void onResume() {
        super.onResume();

        attachKeyboardListeners();
        if (!(ConnectivityReceiver.isConnected())) {
            showDialog(true);
        }
    }

    @Override
    public void onClick(View v) {
        final String name = evName.getText().toString().trim();
        final String phone = evContact.getText().toString().trim();
        final String email = evEmail.getText().toString().trim();
        final String pass = evPassword.getText().toString().trim();
        final String cpass = evConfirmpass.getText().toString().trim();
        final String dob = evDOB.getText().toString().trim();
        final String anni = evAnniversary.getText().toString().trim();

        if (name.length() == 0) {
            evName.setError("Name is Required!");
        } else if (!isValidPhoneNumber(phone)) {
            evContact.setError("Invalid Mobile Number");
        } else if (!isValidEmail(email)) {
            evEmail.setError("Invalid Email Address");
        } else if (!isValidPassword(pass)) {
            evPassword.setError("Invalid Password");
        } else if (!isValidPassword(cpass)) {
            evConfirmpass.setError("Invalid Password");
        } else if (!(pass.equals(cpass))) {
            evConfirmpass.setError("Password confirmation doesn't match Password");
        } else if (dob.length() == 0) {
            evDOB.setError("DOB is Required!");
        } else {

            if (checkBox.isChecked()) {
                /*evDOB.setText("");
                evName.setText("");
                evEmail.setText("");
                evContact.setText("");
                evAnniversary.setText("");
                evConfirmpass.setText("");*/
                showProgress();
                bundle = new Bundle();
                bundle.putString(Constants.USER_NAME, name);
                bundle.putString(Constants.USER_CONTACT, phone);
                bundle.putString(Constants.USER_EMAIL, email);
                bundle.putString(Constants.USER_PASSWORD, pass);
                bundle.putString(Constants.USER_DOB, dob);
                webService(email, phone);
            } else {
                makeToast("you don't accept the Terms and Conditions and Privacy Policy");
            }
        }

    }

    @Override
    protected void onHideKeyboard() {

        Log.e("onHideKeyboard", "onHideKeyboard");
        signup_view1.setVisibility(View.VISIBLE);
        signup_view2.setVisibility(View.GONE);

        if (title != null && subTitle != null) {
            int t = ts / 2;
            int s = sts / 2;
            //title.setTextSize(t);
            //subTitle.setTextSize(s);
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {

        Log.e("onShowKeyboard", "onShowKeyboard");
        signup_view1.setVisibility(View.GONE);
        signup_view2.setVisibility(View.VISIBLE);

        if (title != null && subTitle != null) {
            int t = ts / 3;
            int s = sts / 3;
            //title.setTextSize(t);
            //subTitle.setTextSize(s);
        }
    }

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
        if (pass != null && pass.length() > 3) {
            return true;
        }
        return false;
    }

    private boolean isValidPhoneNumber(String phone) {

        if (!phone.trim().equals("") || phone.length() > 10) {
            return Patterns.PHONE.matcher(phone).matches();
        }

        return false;
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
                                bundle.putString(Constants.STATUS, "signup");
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



