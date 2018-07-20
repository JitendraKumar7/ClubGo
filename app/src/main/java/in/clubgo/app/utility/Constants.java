package in.clubgo.app.utility;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import in.clubgo.app.app.AppController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jitendra Soam on 7/4/16.
 */
public class Constants {

    //To store the firebase id in shared preferences
    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String USER_DOB = "user_dob";
    public static final String UNIQUE_ID = "uniqueid";
    public static final String USER_NAME = "user_name";
    public static final String USER_CITY = "stringCity";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_CONTACT = "user_contact";
    public static final String USER_ADDRESS = "stringAddress";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_LOCATION = "user_location";
    public static final String USER_LATITUDE = "stringLatitude";
    public static final String USER_LONGITUDE = "stringLongitude";

    public static final String BaseImage = "http://cgsquad.in/backend/";
    public static final String BaseURL = "http://cgsquad.in/backend/webservice/";

    public static void addWishlist(final String category, final String userid, final String cateid) {

        String URLs = Constants.BaseURL + "userwishpost.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("Wishlist Response", response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category", category);
                params.put("userid", userid);
                params.put("cateid", cateid);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    public static void deleteWishlist(final String category, final String userid, final String cateid) {

        String URLs = Constants.BaseURL + "userwishpost.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("Wishlist Response", response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deletewishcate", category);
                params.put("deleteuserid", userid);
                params.put("deletewishid", cateid);
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
