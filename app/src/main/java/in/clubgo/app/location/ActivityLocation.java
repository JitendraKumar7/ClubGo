package in.clubgo.app.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.defuzed.clubgo.R;

import in.clubgo.app.GPStraker.SimpleLocation;
import in.clubgo.app.adapter.LocationAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.modal.ModalLocation;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.venues.ActivityLists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityLocation extends BaseActivity {

    private Bundle bundle;
    private ImageView ivBack;
    private TextView tvLocation;
    private FrameLayout frameLayout;
    private LocationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalLocation> mDataList;

    private SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        final Intent intent = this.getIntent();
        bundle = intent.getExtras();
        mDataList = new ArrayList<>();


        // construct a new instance of SimpleLocation
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            location.showSettingsAlert(this);
        }

        inIt();

        tvLocation = (TextView) findViewById(R.id.location_txtLocation);

        String stringCity = Utility.getPref(getApplicationContext(), Constants.USER_CITY, "");
        String stringAddress = Utility.getPref(getApplicationContext(), Constants.USER_ADDRESS, "");
        if (Utility.getPref(getApplicationContext(), Constants.USER_LOCATION, null) == null) {
            if (stringAddress.length() > 5) {
                tvLocation.setText(stringAddress);
            } else {
                tvLocation.setText(stringCity);
            }
        } else {
            tvLocation.setText(Utility.getPref(getApplicationContext(), Constants.USER_LOCATION, ""));
        }

    }

    private void inIt() {

        ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new LocationAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.location_frameCurrent);
        frameLayout.setOnClickListener(this);

        if (Utility.isInternetConnected(this)) {
            webService(Constants.BaseURL + "locationjson.php");
        } else {
            makeToast("No Internet Connection");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((LocationAdapter) mAdapter).setOnClickListener(new LocationAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, String location) {
                Utility.savePref(getApplicationContext(), Constants.USER_LOCATION, location);
                Utility.savePref(getApplicationContext(), Constants.USER_LATITUDE, mDataList.get(position).getAlati());
                Utility.savePref(getApplicationContext(), Constants.USER_LONGITUDE, mDataList.get(position).getAlong());
                Toast.makeText(getApplicationContext(), location + "\nSave Successfull!", Toast.LENGTH_SHORT).show();

                if (bundle.getString("activity").equals("venues")) {
                    launchIntent(ActivityLists.class, bundle, true);
                } else {
                    launchIntent(ActivityHome.class, bundle, true);
                }
            }
        });


        // make the device update its location
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                onBackPressed();
                break;
            case R.id.location_frameCurrent:
                if (!checkFineLocationPermission() || !checkCoarseLocationPermission()) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_SMS, Manifest.permission.CALL_PHONE,
                                    Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                            }, 1);
                } else {
                    makeToast("Current Location" + "\nSave Successfull!");
                    Utility.savePref(getApplicationContext(), Constants.USER_LOCATION, null);
                    Utility.savePref(getApplicationContext(), Constants.USER_LATITUDE, String.valueOf(location.getLatitude()));
                    Utility.savePref(getApplicationContext(), Constants.USER_LONGITUDE, String.valueOf(location.getLongitude()));
                    Utility.savePref(getApplicationContext(), Constants.USER_CITY, String.valueOf(location.getLocality(this)));
                    Utility.savePref(getApplicationContext(), Constants.USER_ADDRESS, String.valueOf(location.getAddressLine(this)));
                    if (bundle.getString("activity").equals("venues")) {
                        launchIntent(ActivityLists.class, bundle, true);
                    } else {
                        launchIntent(ActivityHome.class, bundle, true);
                    }
                }
                break;
            default:
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

    private void webService(final String URLs) {
        showProgress();
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.GET, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dismissProgress();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("location");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                ModalLocation list = new ModalLocation();
                                list.setId(obj.getString("loc_id"));
                                list.setTitle(obj.getString("loc_title"));
                                list.setAlati(obj.getString("loc_lat"));
                                list.setAlong(obj.getString("loc_lng"));
                                mDataList.add(list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideDialog();
        } else {
            showDialog(false);
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
}
