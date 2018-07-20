package in.clubgo.app.venues;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.VenueAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.location.ActivityLocation;
import in.clubgo.app.main.ActivityLogin;
import in.clubgo.app.modal.ModalVenues;
import in.clubgo.app.popups.PopUpVenuesCategory;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityLists extends BaseDrawerActivity {

    private String str, s1, s2;
    private VenueAdapter mAdapter;
    private PopUpVenuesCategory popup;
    private RecyclerView mRecyclerView;
    private List<ModalVenues> mDataList;
    private TextView tvPopup, tvLocation;
    private ImageView ivPopup, ivLocation, ivLocationArrow;
    private String stringLatitude, stringLongitude, stringCity = "", stringAddress = "";


    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);
        ivBtnVenues.setImageResource(R.mipmap.venues_selected);
        tvBtnVenues.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        s1 = bundle.getString("cafe");
        s2 = bundle.getString("clubs");
        str = bundle.getString("value");

        inIt();


        stringLatitude = Utility.getPref(getApplicationContext(), "stringLatitude", "0.00");
        stringLongitude = Utility.getPref(getApplicationContext(), "stringLongitude", "0.00");
        stringCity = Utility.getPref(getApplicationContext(), "stringCity", "");
        stringAddress = Utility.getPref(getApplicationContext(), "stringAddress", "");

        if (Utility.getPref(this, "user_location", null) == null) {
            if (stringAddress.length() > 5) {
                tvLocation.setText(stringAddress);
            } else {
                tvLocation.setText(stringCity);
            }
        } else {
            tvLocation.setText(Utility.getPref(getApplicationContext(), "user_location", ""));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                webService(str);
            }
        });

        if (ConnectivityReceiver.isConnected()) {
            webService(str);
        } else {
            showDialog(false);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mSwipeRefreshLayout.setEnabled(false);

    }

    private void inIt() {

        mDataList = new ArrayList<>();

        tvPopup = (TextView) findViewById(R.id.venuesDetail_txtPopUp);
        ivPopup = (ImageView) findViewById(R.id.venuesDetail_ivArrowDown);

        ivLocation = (ImageView) findViewById(R.id.venuesDetail_ivLocation);
        tvLocation = (TextView) findViewById(R.id.venuesDetail_txtLocation);
        ivLocationArrow = (ImageView) findViewById(R.id.venuesDetail_ivArrow);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        mAdapter = new VenueAdapter(activity, mDataList);
        mRecyclerView.setAdapter(mAdapter);

        popup = new PopUpVenuesCategory(this);

        tvPopup.setOnClickListener(this);
        ivPopup.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivLocationArrow.setOnClickListener(this);

        if (str.equals(s1)) {
            tvPopup.setText("Cafe & Lounges");
        } else if (str.equals(s2)) {
            tvPopup.setText("Clubs");
        } else {
            tvPopup.setText("All");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((VenueAdapter) mAdapter).setOnClickListener(new VenueAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {

                switch (view.getId()) {

                    case R.id.venueList_heart:
                    case R.id.venueList_heart_selected:
                        launchIntent(ActivityLogin.class, false);
                        break;
                    default:
                        Bundle bundle = new Bundle();
                        bundle.putString("value", ids);
                        launchIntent(ActivityVenuesDetails.class, bundle, false);
                        break;
                }
            }
        });

        ((PopUpVenuesCategory) popup).setOnItemClickListener(new PopUpVenuesCategory.MyClickListener() {
            @Override
            public void onItemClick(View v) {

                switch (v.getId()) {
                    case R.id.pop_txtAll:
                        webService("0");
                        tvPopup.setText("All");
                        break;
                    case R.id.pop_txtCafe:
                        webService(s1);
                        tvPopup.setText("Cafe & Lounges");
                        break;
                    case R.id.pop_txtClubs:
                        webService(s2);
                        tvPopup.setText("Clubs");
                        break;
                    default:
                        break;
                }
                popup.mDismiss();
                ivPopup.setImageResource(R.mipmap.back_grey);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (popup.isShowing()) {
            popup.mDismiss();
            ivPopup.setImageResource(R.mipmap.back_grey);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.venuesDetail_ivArrow:
            case R.id.venuesDetail_ivLocation:
            case R.id.venuesDetail_txtLocation:
                if (popup.isShowing()) {
                    popup.mDismiss();
                    ivPopup.setImageResource(R.mipmap.back_grey);
                }
                Bundle bundle = new Bundle();
                bundle.putString("cafe", s1);
                bundle.putString("clubs", s2);
                bundle.putString("value", str);
                bundle.putString("activity", "venues");
                launchIntent(ActivityLocation.class, bundle, false);
                break;

            case R.id.venuesDetail_txtPopUp:
            case R.id.venuesDetail_ivArrowDown:
                if (popup.isShowing()) {
                    popup.mDismiss();
                    ivPopup.setImageResource(R.mipmap.back_grey);
                } else {
                    popup.mShow(tvPopup);
                    ivPopup.setImageResource(R.mipmap.bottom_grey);
                }
                break;


        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            webService(str);
            hideDialog();
        } else {
            showDialog(false);
        }
    }

    private void webService(final String ids) {
        str = ids;
        showProgress();
        mDataList.clear();
        mSwipeRefreshLayout.setRefreshing(false);
        String URLs = Constants.BaseURL + "venuesjson.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dismissProgress();
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray venueArray = mResponse.getJSONArray("venues");
                            for (int i = 0; i < venueArray.length(); i++) {
                                JSONObject jsonObject = venueArray.getJSONObject(i);
                                ModalVenues list = new ModalVenues();
                                list.setType(jsonObject.getString("type"));
                                list.setId(jsonObject.getString("item_id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("item_img1"));
                                list.setTitle(jsonObject.getString("item_title"));
                                list.setLocation(jsonObject.getString("item_location"));
                                list.setRating(jsonObject.getString("item_rating"));
                                list.setWish(jsonObject.getString("userwish"));
                                mDataList.add(list);
                            }

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cate_id", ids);
                params.put("alati", stringLatitude);
                params.put("alongi", stringLongitude);
                params.put("userid", (USERUNIQUEID == null) ? "null" : USERUNIQUEID);
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
