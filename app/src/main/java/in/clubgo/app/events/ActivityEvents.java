package in.clubgo.app.events;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.clubgo.app.adapter.EventAdapter;
import in.clubgo.app.adapter.EventDateAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.filter.ActivityFilters;
import in.clubgo.app.main.ActivityLogin;
import in.clubgo.app.modal.ModalEvent;
import in.clubgo.app.popups.PopUpEventsOffers;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.venues.ActivityVenuesDetails;

public class ActivityEvents extends BaseDrawerActivity {

    private int pos = 0;
    private EventAdapter mAdapter;
    private List<String> dDataList;
    private EventDateAdapter dAdapter;
    private List<ModalEvent> mDataList;
    private PopUpEventsOffers popUpEventsOffers;
    private TextView tvToday, tvTomorrow, tvLater;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView, dRecyclerView;
    private List<ModalEvent> mDataList1, mDataList2, mDataList3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        mDataList1 = new ArrayList<>();
        mDataList2 = new ArrayList<>();
        mDataList3 = new ArrayList<>();
        popUpEventsOffers = new PopUpEventsOffers(activity);
        ivBtnEvents.setImageResource(R.mipmap.event_selected);
        tvBtnEvents.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        showProgress();
        inIt();
    }

    private void inIt() {
        tvToday = (TextView) findViewById(R.id.event_txtToday);
        tvTomorrow = (TextView) findViewById(R.id.event_txtTomorrow);
        tvLater = (TextView) findViewById(R.id.event_txtLater);

        tvToday.setBackgroundResource(R.drawable.back_red);
        tvToday.setTextColor(getResources().getColor(R.color.white));


        tvToday.setOnClickListener(this);
        tvTomorrow.setOnClickListener(this);
        tvLater.setOnClickListener(this);

        dDataList = new ArrayList<>();

        dRecyclerView = (RecyclerView) findViewById(R.id.rvDate);
        dRecyclerView.setHasFixedSize(true);

        dRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        dAdapter = new EventDateAdapter(activity, dDataList);
        dRecyclerView.setAdapter(dAdapter);

        mDataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        mAdapter = new EventAdapter(activity, mDataList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                switch (pos) {
                    case 0:
                        mDataList1.clear();
                        tvToday.performClick();
                        break;
                    case 1:
                        mDataList2.clear();
                        tvTomorrow.performClick();
                        break;
                    case 2:
                        mDataList3.clear();
                        tvLater.performClick();
                        break;
                    default:
                        break;
                }
                dismissProgress();
            }
        });

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
        if (ConnectivityReceiver.isConnected()) {
            refreshData(pos);
        } else {
            showDialog(false);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((EventAdapter) mAdapter).setOnItemClickListener(new EventAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {

                if (ConnectivityReceiver.isConnected()) {
                    switch (view.getId()) {
                        case R.id.eventlist_frmEventOffer:
                            ModalEvent bean = mDataList.get(position);
                            popUpEventsOffers.show(bean, view);
                            break;

                        case R.id.eventlist_ivHeart:
                        case R.id.eventlist_ivHeart_selected:
                            launchIntent(ActivityLogin.class, false);
                            break;

                        default:
                            Bundle bundle = new Bundle();
                            bundle.putString("value", ids);
                            launchIntent(ActivityEventsDetails.class, bundle, false);
                            break;
                    }
                } else {
                    showDialog(false);
                }
            }
        });

        ((PopUpEventsOffers) popUpEventsOffers).setOnItemClickListener(new PopUpEventsOffers.MyClickListener() {
            @Override
            public void onItemClick(View v, ModalEvent bean) {
                popUpEventsOffers.mDismiss();
                switch (v.getId()) {
                    case R.id.linearLayout_event:
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("value", bean.getId());
                        launchIntent(ActivityEventsDetails.class, bundle1, false);
                        break;
                    case R.id.linearLayout_venues:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("value", bean.getE_venueid());
                        launchIntent(ActivityVenuesDetails.class, bundle2, false);
                        break;
                    default:
                        break;
                }
            }
        });

        ((EventDateAdapter) dAdapter).setMyClickListener(new EventDateAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, String date) {

                if (ConnectivityReceiver.isConnected()) {

                    String stringLatitude = Utility.getPref(getApplicationContext(), Constants.USER_LATITUDE, "0.00");
                    String stringLongitude = Utility.getPref(getApplicationContext(), Constants.USER_LONGITUDE, "0.00");

                    if (position == 9999) {
                        tvLater.performClick();
                    } else {
                        if (Utility.getPref(getApplicationContext(), "events", "ActivityEvents").equals("ActivityEvents")) {
                            webService4(Constants.BaseURL + "eventjson.php?date=" + date +
                                    "&alati=" + stringLatitude +
                                    "&alongi=" + stringLongitude +
                                    "&userid=" + USERUNIQUEID);
                        } else {
                            String tag = ActivityFilters.tag.replaceAll("\\s+", "%20");
                            webService4(Constants.BaseURL + "filterjson.php?" +
                                    "cate_id=" + ActivityFilters.cateId +
                                    "&offer=" + ActivityFilters.offers +
                                    "&location=" + ActivityFilters.location +
                                    "&tag=" + tag +
                                    "&dist=" + ActivityFilters.distance +
                                    "&cost=" + ActivityFilters.strCost +
                                    "&userid=" + USERUNIQUEID +
                                    "&alati=" + stringLatitude +
                                    "&alongi=" + stringLongitude +
                                    "&date=" + date);
                        }
                    }
                } else {
                    //((ActivityEvents) getActivity()).showDialog(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (dRecyclerView.getVisibility() == View.VISIBLE) {
            dRecyclerView.setVisibility(View.GONE);
        }
        tvToday.setBackgroundColor(getResources().getColor(R.color.gray));
        tvTomorrow.setBackgroundColor(getResources().getColor(R.color.gray));
        tvLater.setBackgroundColor(getResources().getColor(R.color.gray));
        tvToday.setTextColor(getResources().getColor(R.color.black));
        tvTomorrow.setTextColor(getResources().getColor(R.color.black));
        tvLater.setTextColor(getResources().getColor(R.color.black));

        switch (v.getId()) {
            case R.id.event_txtToday:
                pos = 0;
                break;
            case R.id.event_txtTomorrow:
                pos = 1;
                break;
            case R.id.event_txtLater:
                pos = 2;
                break;
        }

        if (ConnectivityReceiver.isConnected()) {
            showProgress();
            refreshData(pos);
        } else {
            showDialog(false);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideDialog();
            refreshData(pos);
        } else {
            showDialog(false);
        }
    }

    private void refreshData(final int position) {
        String URLs;
        String stringLatitude = Utility.getPref(getApplicationContext(), Constants.USER_LATITUDE, "0.00");
        String stringLongitude = Utility.getPref(getApplicationContext(), Constants.USER_LONGITUDE, "0.00");

        if (Utility.getPref(getApplicationContext(), "events", "ActivityEvents").equals("ActivityEvents")) {
            URLs = Constants.BaseURL + "eventjson.php?userid=" + USERUNIQUEID +
                    "&alati=" + stringLatitude + "&alongi=" + stringLongitude;
        } else {
            String tag = ActivityFilters.tag.replaceAll("\\s+", "%20");
            URLs = Constants.BaseURL + "filterjson.php?" +
                    "cate_id=" + ActivityFilters.cateId +
                    "&offer=" + ActivityFilters.offers +
                    "&location=" + ActivityFilters.location +
                    "&tag=" + tag +
                    "&dist=" + ActivityFilters.distance +
                    "&cost=" + ActivityFilters.strCost +
                    "&userid=" + USERUNIQUEID +
                    "&alati=" + stringLatitude +
                    "&alongi=" + stringLongitude;
        }
        switch (position) {
            case 0:
                if (mDataList1.size() == 0) {
                    webService1(URLs + "&day=today");
                } else {
                    mDataList.clear();
                    for (int i = 0; i < mDataList1.size(); i++) {
                        ModalEvent bean = mDataList1.get(i);
                        mDataList.add(bean);
                    }
                    dismissProgress();
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                tvToday.setBackgroundResource(R.drawable.back_red);
                tvToday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                if (mDataList2.size() == 0) {
                    webService2(URLs + "&day=tom");
                } else {
                    mDataList.clear();
                    for (int i = 0; i < mDataList2.size(); i++) {
                        ModalEvent bean = mDataList2.get(i);
                        mDataList.add(bean);
                    }
                    dismissProgress();
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                tvTomorrow.setBackgroundResource(R.drawable.back_red);
                tvTomorrow.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                if (mDataList3.size() == 0) {
                    dAdapter.setPosition(9999);
                    webService3(URLs + "&day=soon");
                } else {
                    mDataList.clear();
                    for (int i = 0; i < mDataList3.size(); i++) {
                        ModalEvent bean = mDataList3.get(i);
                        mDataList.add(bean);
                    }
                    dismissProgress();
                    dAdapter.setPosition(9999);
                    dAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    dRecyclerView.setVisibility(View.VISIBLE);
                }
                tvLater.setBackgroundResource(R.drawable.back_red);
                tvLater.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
    }

    private void webService1(final String URLs) {

        Log.e("URLs", URLs);
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.GET, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("today");
                            JSONArray distanceArray = mResponse.getJSONArray("distance");

                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject jsonObject = artistArray.getJSONObject(i);
                                ModalEvent list = new ModalEvent();
                                list.setId(jsonObject.getString("e_id"));
                                list.setName(jsonObject.getString("e_name"));
                                list.setE_type(jsonObject.getString("e_type"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("feature_image"));
                                list.setVenue(jsonObject.getString("e_venue"));
                                list.setDate(jsonObject.getString("e_date"));
                                list.setStart(jsonObject.getString("e_start"));

                                list.setE_venueid(jsonObject.getString("e_venueid"));
                                list.setVenueimg(Constants.BaseImage + jsonObject.getString("venueimg"));
                                list.setOfferdesc(jsonObject.getString("offerdesc"));

                                list.setOffer_exist(jsonObject.getString("offer_exist"));
                                list.setOffer(jsonObject.getString("offer"));
                                list.setPrice_exist(jsonObject.getString("price_exist"));
                                list.setDiscounted_price(jsonObject.getString("discounted_price"));

                                list.setTag1(jsonObject.getString("tag1"));
                                list.setTag2(jsonObject.getString("tag2"));

                                list.setCost(jsonObject.getString("e_cost"));
                                list.setWish(jsonObject.getString("userwish"));

                                //list.setDistance(jsonObject.getString("distance"));
                                String distanceKey = distanceArray.getString(i);
                                JSONObject distance = new JSONObject("{" + distanceKey + "}");
                                list.setDistance(distance.getString("distance"));

                                mDataList.add(list);
                                mDataList1.add(list);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDataList.clear();
                dismissProgress();
                mAdapter.notifyDataSetChanged();
                Log.e("error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void webService2(final String URLs) {

        Log.e("URLs", URLs);
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.GET, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("tom");
                            JSONArray distanceArray = mResponse.getJSONArray("distance");

                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject jsonObject = artistArray.getJSONObject(i);
                                ModalEvent list = new ModalEvent();
                                list.setId(jsonObject.getString("e_id"));
                                list.setName(jsonObject.getString("e_name"));
                                list.setE_type(jsonObject.getString("e_type"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("feature_image"));
                                list.setVenue(jsonObject.getString("e_venue"));
                                list.setDate(jsonObject.getString("e_date"));
                                list.setStart(jsonObject.getString("e_start"));

                                list.setE_venueid(jsonObject.getString("e_venueid"));
                                list.setVenueimg(Constants.BaseImage + jsonObject.getString("venueimg"));
                                list.setOfferdesc(jsonObject.getString("offerdesc"));


                                list.setOffer_exist(jsonObject.getString("offer_exist"));
                                list.setOffer(jsonObject.getString("offer"));
                                list.setPrice_exist(jsonObject.getString("price_exist"));
                                list.setDiscounted_price(jsonObject.getString("discounted_price"));

                                list.setTag1(jsonObject.getString("tag1"));
                                list.setTag2(jsonObject.getString("tag2"));

                                list.setCost(jsonObject.getString("e_cost"));
                                list.setWish(jsonObject.getString("userwish"));

                                //list.setDistance(jsonObject.getString("distance"));
                                String distanceKey = distanceArray.getString(i);
                                JSONObject distance = new JSONObject("{" + distanceKey + "}");
                                list.setDistance(distance.getString("distance"));

                                mDataList.add(list);
                                mDataList2.add(list);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDataList.clear();
                dismissProgress();
                mAdapter.notifyDataSetChanged();
                Log.e("error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void webService3(final String URLs) {

        Log.e("URLs", URLs);
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.GET, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dDataList.clear();
                        mDataList.clear();
                        dismissProgress();
                        dAdapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        dRecyclerView.setVisibility(View.VISIBLE);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("soon");
                            JSONArray distanceArray = mResponse.getJSONArray("distance");

                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject jsonObject = artistArray.getJSONObject(i);
                                ModalEvent list = new ModalEvent();
                                list.setId(jsonObject.getString("e_id"));
                                list.setName(jsonObject.getString("e_name"));
                                list.setE_type(jsonObject.getString("e_type"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("feature_image"));
                                list.setVenue(jsonObject.getString("e_venue"));
                                list.setDate(jsonObject.getString("e_date"));
                                list.setStart(jsonObject.getString("e_start"));

                                list.setE_venueid(jsonObject.getString("e_venueid"));
                                list.setVenueimg(Constants.BaseImage + jsonObject.getString("venueimg"));
                                list.setOfferdesc(jsonObject.getString("offerdesc"));


                                list.setOffer_exist(jsonObject.getString("offer_exist"));
                                list.setOffer(jsonObject.getString("offer"));
                                list.setPrice_exist(jsonObject.getString("price_exist"));
                                list.setDiscounted_price(jsonObject.getString("discounted_price"));

                                list.setTag1(jsonObject.getString("tag1"));
                                list.setTag2(jsonObject.getString("tag2"));

                                list.setCost(jsonObject.getString("e_cost"));
                                list.setWish(jsonObject.getString("userwish"));

                                //list.setDistance(jsonObject.getString("distance"));
                                String distanceKey = distanceArray.getString(i);
                                JSONObject distance = new JSONObject("{" + distanceKey + "}");
                                list.setDistance(distance.getString("distance"));

                                mDataList.add(list);
                                mDataList3.add(list);
                            }

                            JSONArray datesArray = mResponse.getJSONArray("date");
                            for (int i = 0; i < datesArray.length(); i++) {
                                dDataList.add(datesArray.getString(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dAdapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                dDataList.clear();
                mDataList.clear();
                dismissProgress();
                dAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
                Log.e("error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void webService4(final String URLs) {

        Log.e("URLs", URLs);
        // Request a string response
        showProgress();
        StringRequest strRequest = new StringRequest(Request.Method.GET, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("date");
                            JSONArray distanceArray = mResponse.getJSONArray("distance");

                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject jsonObject = artistArray.getJSONObject(i);
                                ModalEvent list = new ModalEvent();
                                list.setId(jsonObject.getString("e_id"));
                                list.setName(jsonObject.getString("e_name"));
                                list.setE_type(jsonObject.getString("e_type"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("feature_image"));
                                list.setVenue(jsonObject.getString("e_venue"));
                                list.setDate(jsonObject.getString("e_date"));
                                list.setStart(jsonObject.getString("e_start"));

                                list.setE_venueid(jsonObject.getString("e_venueid"));
                                list.setVenueimg(Constants.BaseImage + jsonObject.getString("venueimg"));
                                list.setOfferdesc(jsonObject.getString("offerdesc"));


                                list.setOffer_exist(jsonObject.getString("offer_exist"));
                                list.setOffer(jsonObject.getString("offer"));
                                list.setPrice_exist(jsonObject.getString("price_exist"));
                                list.setDiscounted_price(jsonObject.getString("discounted_price"));

                                list.setTag1(jsonObject.getString("tag1"));
                                list.setTag2(jsonObject.getString("tag2"));

                                list.setCost(jsonObject.getString("e_cost"));
                                list.setWish(jsonObject.getString("userwish"));

                                //list.setDistance(jsonObject.getString("distance"));
                                String distanceKey = distanceArray.getString(i);
                                JSONObject distance = new JSONObject("{" + distanceKey + "}");
                                list.setDistance(distance.getString("distance"));
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
                mAdapter.notifyDataSetChanged();
                Log.e("error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

}


