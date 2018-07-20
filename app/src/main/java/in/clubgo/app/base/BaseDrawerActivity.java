package in.clubgo.app.base;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.about.ActivityAbout;
import in.clubgo.app.adapter.SearchAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.artiest.ActivityArtist;
import in.clubgo.app.events.ActivityEvents;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.home.ActivityOffers;
import in.clubgo.app.main.ActivityLogin;
import in.clubgo.app.main.ActivityStartUp;
import in.clubgo.app.modal.ModalList;
import in.clubgo.app.Tickets.ActivityTickets;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.venues.ActivityVenuesDetails;
import in.clubgo.app.venues.ActivityVenue;
import in.clubgo.app.filter.ActivityFilters;
import in.clubgo.app.location.ActivityLocation;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.wishlist.ActivityWishList;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseDrawerActivity extends BaseActivity {

    private MyListener listener;
    private EditText tvBtnSearch;
    private DrawerLayout mDrawerLayout;
    private ImageView ivOpenDrawer, ivNavProfilePic, ivNavClose, ivNavEdit, ivFilter;
    protected ImageView ivBtnHome, ivBtnEvents, ivBtnVenues, ivBtnArtiest, ivBtnOffers;
    private FrameLayout frameHome, frameEvents, frameVenues, frameArtiest, frameOthers;
    protected TextView tvBtnHome, tvBtnEvents, tvBtnVenues, tvBtnArtiest, tvBtnOffers,
            tvName, tvEmail, tvAbout, tvWishList, tvTickets, tvInviteFriends, tvSign;

    private List<ModalList> mSearchList;
    private SearchAdapter mSearchAdapter;
    private RecyclerView searchRecyclerView;


    @Override
    public void setContentView(int layoutResID) {
        RelativeLayout fullView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        listener = new MyListener();
        inItBase();
    }

    public void inItBase() {

        tvBtnSearch = (EditText) findViewById(R.id.base_txtSearch);
        tvBtnSearch.addTextChangedListener(myTextWatcher);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ivFilter = (ImageView) findViewById(R.id.base_btnFilter);
        ivOpenDrawer = (ImageView) findViewById(R.id.base_btnOpenDrawer);
        ivOpenDrawer.setOnClickListener(listener);

        ivFilter.setOnClickListener(listener);

        frameHome = (FrameLayout) findViewById(R.id.base_frameHome);
        frameEvents = (FrameLayout) findViewById(R.id.base_frameEvents);
        frameVenues = (FrameLayout) findViewById(R.id.base_frameVenues);
        frameArtiest = (FrameLayout) findViewById(R.id.base_frameArtiest);
        frameOthers = (FrameLayout) findViewById(R.id.base_frameOthers);

        ivBtnHome = (ImageView) findViewById(R.id.base_ivHome);
        ivBtnEvents = (ImageView) findViewById(R.id.base_ivEvents);
        ivBtnVenues = (ImageView) findViewById(R.id.base_ivVenues);
        ivBtnArtiest = (ImageView) findViewById(R.id.base_ivArtiest);
        ivBtnOffers = (ImageView) findViewById(R.id.base_ivOffers);

        tvBtnHome = (TextView) findViewById(R.id.base_txtHome);
        tvBtnEvents = (TextView) findViewById(R.id.base_txtEvents);
        tvBtnVenues = (TextView) findViewById(R.id.base_txtVenues);
        tvBtnArtiest = (TextView) findViewById(R.id.base_txtArtiest);
        tvBtnOffers = (TextView) findViewById(R.id.base_txtOffers);

        frameHome.setOnClickListener(listener);
        frameEvents.setOnClickListener(listener);
        frameVenues.setOnClickListener(listener);
        frameArtiest.setOnClickListener(listener);
        frameOthers.setOnClickListener(listener);

        ivNavClose = (ImageView) findViewById(R.id.nav_ivClose);
        ivNavEdit = (ImageView) findViewById(R.id.nav_ivEdit);
        ivNavClose.setOnClickListener(listener);
        ivNavEdit.setOnClickListener(listener);

        tvName = (TextView) findViewById(R.id.nav_txtName);
        tvEmail = (TextView) findViewById(R.id.nav_txtEmail);

        tvAbout = (TextView) findViewById(R.id.nav_txtAbout);
        tvSign = (TextView) findViewById(R.id.nav_txtSign);
        tvWishList = (TextView) findViewById(R.id.nav_txtWishlist);
        tvTickets = (TextView) findViewById(R.id.nav_txtBookedTickets);
        tvInviteFriends = (TextView) findViewById(R.id.nav_txtInviteFriends);

        tvAbout.setOnClickListener(listener);
        tvSign.setOnClickListener(listener);
        tvTickets.setOnClickListener(listener);
        tvWishList.setOnClickListener(listener);
        tvInviteFriends.setOnClickListener(listener);

        ivNavProfilePic = (ImageView) findViewById(R.id.nav_user_profile);

        mSearchList = new ArrayList();
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRv);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mSearchAdapter = new SearchAdapter(activity, mSearchList);
        searchRecyclerView.setAdapter(mSearchAdapter);

        final String guest = "https://placeholdit.imgix.net/~text?txtsize=37&txt=Guest%20Session&w=250&h=250";
        tvName.setText(Utility.getPref(getApplicationContext(), Constants.USER_NAME, "ClubGo Guest"));
        tvEmail.setText(Utility.getPref(getApplicationContext(), Constants.USER_EMAIL, ""));
        Picasso.with(activity)
                .load(Utility.getPref(getApplicationContext(), Constants.USER_IMAGE, guest))
                .placeholder(R.drawable.image_holder)
                .into(ivNavProfilePic);

        if (USERUNIQUEID == null) {
            tvSign.setText("Sign In");
        } else {
            tvSign.setText("Sign Out");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView locHome = (ImageView) findViewById(R.id.base_btnFilter);

        if (activity instanceof ActivityHome) {
            locHome.setImageResource(R.mipmap.ic_location);
        } else {
            locHome.setImageResource(R.mipmap.ic_filter);
        }

        ((SearchAdapter) mSearchAdapter).setOnClickListener(new SearchAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, String ids, String type) {
                if (type.equals("venue")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("value", ids);
                    launchIntent(ActivityVenuesDetails.class, bundle, false);
                } else if (type.equals("event")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("value", ids);
                    launchIntent(ActivityEventsDetails.class, bundle, false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (searchRecyclerView.getVisibility() == View.VISIBLE) {
            mSearchList.clear();
            searchRecyclerView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void exitToAppication() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout_layout);
        Button button = (Button) dialog.findViewById(R.id.logout_logout_button);
        Button cancleButton = (Button) dialog.findViewById(R.id.logout_cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("ClubGo", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear().commit();
                dialog.dismiss();
                Intent newIntent = new Intent(activity, ActivityStartUp.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                finish();
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

    private void webService(final String txtSearch) {

        mSearchList.clear();
        String URL = Constants.BaseURL + "searchjson.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("Search Responce", response);
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray eArray = mResponse.getJSONArray("event");
                            for (int i = 0; i < eArray.length(); i++) {
                                JSONObject obj = eArray.getJSONObject(i);
                                ModalList modal = new ModalList();
                                modal.setTitle("event");
                                modal.setItemId(obj.getString("e_id"));
                                modal.setName(obj.getString("e_name"));
                                modal.setDescription(obj.getString("e_desc"));
                                modal.setType("event");
                                modal.setImage(Constants.BaseImage + obj.getString("feature_image"));
                                mSearchList.add(modal);
                            }

                            JSONArray vArray = mResponse.getJSONArray("venue");
                            for (int i = 0; i < vArray.length(); i++) {
                                JSONObject obj = vArray.getJSONObject(i);
                                ModalList modal = new ModalList();
                                if (i == 0) {
                                    modal.setTitle("venue");
                                } else {
                                    modal.setTitle("");
                                }
                                modal.setType("venue");
                                modal.setItemId(obj.getString("item_id"));
                                modal.setName(obj.getString("item_title"));
                                modal.setDescription("Venues");
                                modal.setImage(Constants.BaseImage + obj.getString("item_img1"));
                                mSearchList.add(modal);
                            }
                            searchRecyclerView.setVisibility(View.VISIBLE);
                            mSearchAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("s", txtSearch);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    public class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.base_btnOpenDrawer:
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;

                case R.id.base_btnFilter:
                    if (activity instanceof ActivityHome) {
                        Bundle bundle = new Bundle();
                        bundle.putString("activity", "activity");
                        launchIntent(ActivityLocation.class, bundle, false);
                    } else {
                        launchIntent(ActivityFilters.class, false);
                    }
                    break;

                case R.id.nav_user_profile:
                    break;

                case R.id.nav_txtAbout:
                    launchIntent(ActivityAbout.class, false);
                    break;

                case R.id.nav_txtWishlist:
                    if (USERUNIQUEID == null) {
                        launchIntent(ActivityLogin.class, false);
                    } else {
                        launchIntent(ActivityWishList.class, false);
                    }
                    break;

                case R.id.nav_txtBookedTickets:
                    if (USERUNIQUEID == null) {
                        launchIntent(ActivityLogin.class, false);
                    } else {
                        launchIntent(ActivityTickets.class, false);
                    }
                    break;

                case R.id.nav_txtInviteFriends:
                    Utility.shareListUrl(activity);
                    break;

                case R.id.base_frameHome:
                    if (!(activity instanceof ActivityHome))
                        launchIntent(ActivityHome.class, false);
                    break;

                case R.id.base_frameEvents:
                    if (!(activity instanceof ActivityEvents)) {
                        launchIntent(ActivityEvents.class, false);
                        Utility.savePref(getApplicationContext(), "events", "ActivityEvents");
                    }
                    break;

                case R.id.base_frameVenues:
                    if (!(activity instanceof ActivityVenue))
                        launchIntent(ActivityVenue.class, false);
                    break;
                case R.id.base_frameArtiest:
                    if (!(activity instanceof ActivityArtist))
                        launchIntent(ActivityArtist.class, false);
                    break;
                case R.id.base_frameOthers:
                    if (!(activity instanceof ActivityOffers))
                        launchIntent(ActivityOffers.class, false);
                    break;
                case R.id.nav_txtSign:
                    if (USERUNIQUEID == null) {
                        launchIntent(ActivityLogin.class, false);
                    } else {
                        exitToAppication();
                    }
                    break;
                default:
                    break;
            }

            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }

    private final TextWatcher myTextWatcher = new TextWatcher() {

        private Timer timer = new Timer();
        private final long DELAY = 500; // milliseconds

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.e("beforeTextChanged", s.toString());
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e("onTextChanged", s.toString());
        }

        public void afterTextChanged(final Editable s) {

            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            if (s.length() != 0)
                                webService(s.toString());
                            // TODO: do what you need here (refresh list)
                            // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                        }
                    },
                    DELAY
            );

            Log.e("afterTextChanged", s.toString());
        }
    };

}

