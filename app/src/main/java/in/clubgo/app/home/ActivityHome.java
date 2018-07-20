package in.clubgo.app.home;

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

import in.clubgo.app.adapter.AroundTownAdapter;
import in.clubgo.app.adapter.BeatmakersAdapter;
import in.clubgo.app.adapter.HotspotsAdapter;
import in.clubgo.app.adapter.NearByAdapter;
import in.clubgo.app.adapter.OffersAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.artiest.ActivityArtist;
import in.clubgo.app.artiest.ActivityArtistDetails;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.events.ActivityEvents;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.modal.ModalEvent;
import in.clubgo.app.modal.ModalHome;
import in.clubgo.app.popups.PopUpEventsOffers;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.venues.ActivityVenuesDetails;
import in.clubgo.app.venues.ActivityVenue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityHome extends BaseDrawerActivity implements View.OnClickListener {

    private List<ModalEvent> mDataList;
    private OffersAdapter mOffersAdapter;
    private NearByAdapter mNearByAdapter;
    private HotspotsAdapter mHotspotsAdapter;
    private BeatmakersAdapter mBeatmakersAdapter;
    private AroundTownAdapter mAroundTownAdapter;

    private PopUpEventsOffers popUpEventsOffers;

    private RecyclerView mAroundRv, mHotspotsRv,
            mOffersRv, mNearByRv, mBeatMakersRv;

    public static String jitendra_soam = "true";
    private List<ModalHome> mAroundList, mHotSpotList,
            mOffersList, mNearByList, mBeatMakersList;

    private TextView tvOffer, tvNearBy, tvAroundMore,
            tvHotspotMore, tvOffersMore, tvNearByMore, tvBeatmakerMore;

    private String stringLatitude, stringLongitude, stringCity = "", stringAddress = "";

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        jitendra_soam = "true";
        mDataList = new ArrayList<>();
        mAroundList = new ArrayList<>();
        mHotSpotList = new ArrayList<>();
        mOffersList = new ArrayList<>();
        mNearByList = new ArrayList<>();
        mBeatMakersList = new ArrayList<>();

        inIt();
        if (ConnectivityReceiver.isConnected()) {
            refreshData();
            showProgress();
        } else {
            showDialog(false);
        }

        popUpEventsOffers = new PopUpEventsOffers(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshData();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        ((AroundTownAdapter) mAroundTownAdapter).setOnClickListener(new AroundTownAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                launchIntent(ActivityEventsDetails.class, bundle, false);
            }
        });

        ((HotspotsAdapter) mHotspotsAdapter).setOnClickListener(new HotspotsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                launchIntent(ActivityVenuesDetails.class, bundle, false);
            }
        });

        ((OffersAdapter) mOffersAdapter).setOnClickListener(new OffersAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                ModalEvent bean = mDataList.get(position);
                popUpEventsOffers.show(bean, view);
            }
        });

        ((NearByAdapter) mNearByAdapter).setOnClickListener(new NearByAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                launchIntent(ActivityVenuesDetails.class, bundle, false);
            }
        });

        ((BeatmakersAdapter) mBeatmakersAdapter).setOnClickListener(new BeatmakersAdapter.MyClickListener() {
            @Override
            public void onItemClick(View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                launchIntent(ActivityArtistDetails.class, bundle, false);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.home_txtAroundMore:
                launchIntent(ActivityEvents.class, false);
                Utility.savePref(getApplicationContext(), "events", "ActivityEvents");
                break;
            case R.id.home_txtHotspotsMore:
                launchIntent(ActivityVenue.class, false);
                break;
            case R.id.home_txtOffersMore:
                //launchIntent(ActivityOffers.class, false);
                break;
            case R.id.home_txtNearByMore:
                launchIntent(ActivityVenue.class, false);
                break;
            case R.id.home_txtBeatmakersMore:
                launchIntent(ActivityArtist.class, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            hideDialog();
            refreshData();
            showProgress();
        } else {
            showDialog(false);
        }
    }

    private void inIt() {

        ivBtnHome.setImageResource(R.mipmap.home_selected);
        tvBtnHome.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        tvOffer = (TextView) findViewById(R.id.home_txtOffer);
        tvNearBy = (TextView) findViewById(R.id.home_txtNearBy);

        tvAroundMore = (TextView) findViewById(R.id.home_txtAroundMore);
        tvOffersMore = (TextView) findViewById(R.id.home_txtOffersMore);
        tvNearByMore = (TextView) findViewById(R.id.home_txtNearByMore);
        tvHotspotMore = (TextView) findViewById(R.id.home_txtHotspotsMore);
        tvBeatmakerMore = (TextView) findViewById(R.id.home_txtBeatmakersMore);

        tvAroundMore.setOnClickListener(this);
        tvHotspotMore.setOnClickListener(this);
        tvOffersMore.setOnClickListener(this);
        tvNearByMore.setOnClickListener(this);
        tvBeatmakerMore.setOnClickListener(this);

        mAroundRv = (RecyclerView) findViewById(R.id.rvAround);
        mAroundRv.setHasFixedSize(true);
        mAroundRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mAroundTownAdapter = new AroundTownAdapter(this, mAroundList);
        mAroundRv.setAdapter(mAroundTownAdapter);

        mHotspotsRv = (RecyclerView) findViewById(R.id.rvHotspots);
        mHotspotsRv.setHasFixedSize(true);
        mHotspotsRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mHotspotsAdapter = new HotspotsAdapter(this, mHotSpotList);
        mHotspotsRv.setAdapter(mHotspotsAdapter);

        mOffersRv = (RecyclerView) findViewById(R.id.rvOffers);
        mOffersRv.setHasFixedSize(true);
        mOffersRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mOffersAdapter = new OffersAdapter(this, mOffersList);
        mOffersRv.setAdapter(mOffersAdapter);

        mNearByRv = (RecyclerView) findViewById(R.id.rvNearBy);
        mNearByRv.setHasFixedSize(true);
        mNearByRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mNearByAdapter = new NearByAdapter(this, mNearByList);
        mNearByRv.setAdapter(mNearByAdapter);

        mBeatMakersRv = (RecyclerView) findViewById(R.id.rvBeatmakers);
        mBeatMakersRv.setHasFixedSize(true);
        mBeatMakersRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mBeatmakersAdapter = new BeatmakersAdapter(this, mBeatMakersList);
        mBeatMakersRv.setAdapter(mBeatmakersAdapter);
    }

    private void refreshData() {

        stringCity = Utility.getPref(getApplicationContext(), Constants.USER_CITY, "");
        stringAddress = Utility.getPref(getApplicationContext(), Constants.USER_ADDRESS, "");
        stringLatitude = Utility.getPref(getApplicationContext(), Constants.USER_LATITUDE, "0.00");
        stringLongitude = Utility.getPref(getApplicationContext(), Constants.USER_LONGITUDE, "0.00");

        Log.e("City", stringCity + " J");
        Log.e("Address", stringAddress + " J");

        Log.e("Latitude", stringLatitude + " J");
        Log.e("Longitude", stringLongitude + " J");
        webService(Constants.BaseURL + "homejson.php?alati=" + stringLatitude + "&alongi=" + stringLongitude);
    }

    private void webService(final String URLs) {
        Log.d("JKS", URLs);

        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dismissProgress();
                        mAroundList.clear();
                        mOffersList.clear();
                        mNearByList.clear();
                        mHotSpotList.clear();
                        mBeatMakersList.clear();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray hotSpotArray = mResponse.getJSONArray("venues");
                            for (int i = 0; i < hotSpotArray.length(); i++) {
                                JSONObject jsonObject = hotSpotArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("img"));
                                list.setTitle(jsonObject.getString("title"));
                                list.setOffers("");
                                list.setPlace(jsonObject.getString("places"));
                                mHotSpotList.add(list);
                            }
                            if (mHotSpotList.size() > 0)
                                mHotspotsAdapter.notifyDataSetChanged();

                            JSONArray aroundArray = mResponse.getJSONArray("event");
                            for (int i = 0; i < aroundArray.length(); i++) {
                                JSONObject jsonObject = aroundArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("img"));
                                // list.setTitle(jsonObject.getString("title"));
                                list.setTitle(jsonObject.getString("featured_text"));
                                list.setPlace(jsonObject.getString("places"));
                                mAroundList.add(list);
                            }
                            if (mAroundList.size() > 0)
                                mAroundTownAdapter.notifyDataSetChanged();

                            JSONArray djArray = mResponse.getJSONArray("dj");
                            for (int i = 0; i < djArray.length(); i++) {
                                JSONObject jsonObject = djArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("img"));
                                list.setOffers("");
                                list.setTitle(jsonObject.getString("title"));
                                list.setPlace("");
                                mBeatMakersList.add(list);
                            }
                            if (mBeatMakersList.size() > 0)
                                mBeatmakersAdapter.notifyDataSetChanged();

                            JSONArray offersArray = mResponse.getJSONArray("offers");
                            for (int i = 0; i < offersArray.length(); i++) {
                                JSONObject jsonObject = offersArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("img"));
                                list.setOffers(jsonObject.getString("offers"));
                                list.setTitle(jsonObject.getString("title"));
                                mOffersList.add(list);

                                ModalEvent offers = new ModalEvent();
                                offers.setId(jsonObject.getString("eid"));
                                offers.setName(jsonObject.getString("eventname"));
                                offers.setVenue(jsonObject.getString("e_venue"));
                                offers.setOfferdesc(jsonObject.getString("offers"));
                                offers.setE_venueid(jsonObject.getString("e_venueid"));
                                offers.setVenueimg(Constants.BaseImage + jsonObject.getString("venueimg"));
                                offers.setImage(Constants.BaseImage + jsonObject.getString("feature_image"));

                                mDataList.add(offers);
                            }
                            if (mOffersList.size() > 0)
                                mOffersAdapter.notifyDataSetChanged();

                            if (offersArray.length() == 0) {
                                tvOffer.setVisibility(View.VISIBLE);
                            }

                            JSONArray nearArray = mResponse.getJSONArray("nearby");
                            for (int i = 0; i < nearArray.length(); i++) {
                                JSONObject jsonObject = nearArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("img"));
                                list.setTitle(jsonObject.getString("title"));
                                list.setPlace(jsonObject.getString("distance"));
                                list.setOffers(jsonObject.getString("places"));
                                mNearByList.add(list);
                            }
                            if (mNearByList.size() > 0)
                                mNearByAdapter.notifyDataSetChanged();

                            if (nearArray.length() == 0) {
                                tvNearBy.setVisibility(View.VISIBLE);
                            }

                            String[] separate = mResponse.getString("address").split(",");

                            if (stringCity.equalsIgnoreCase(stringAddress)) {

                                if (separate[0].length() > 3) {
                                    Utility.savePref(getApplicationContext(), Constants.USER_CITY, separate[0]);
                                } else if (separate.length > 1) {
                                    Utility.savePref(getApplicationContext(), Constants.USER_ADDRESS, separate[1]);
                                }
                                Log.e("USER_CITY", Utility.getPref(getApplicationContext(), Constants.USER_CITY, "USER_CITY"));
                                Log.e("USER_ADDRESS", Utility.getPref(getApplicationContext(), Constants.USER_ADDRESS, "USER_ADDRESS"));
                            }

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
        });

        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

}