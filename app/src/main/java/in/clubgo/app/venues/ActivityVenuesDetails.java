package in.clubgo.app.venues;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.VenueBarAdapter;
import in.clubgo.app.adapter.VenueFoodAdapter;
import in.clubgo.app.adapter.VenuePhotosAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.image_slider.ActivityImageSlider;
import in.clubgo.app.indicator.CirclePageIndicator;
import in.clubgo.app.main.ActivityLogin;
import in.clubgo.app.map.MapsActivity;
import in.clubgo.app.pager.EventDetailPagerAdapter;
import in.clubgo.app.pager.PagerSlider;
import in.clubgo.app.review.ActivityReview;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jitendra Soam on 15/4/16.
 */
public class ActivityVenuesDetails extends BaseActivity {

    private RatingBar ratingBar;
    private ViewPager mViewPager;
    private PagerSlider mAdapter;
    private List<String> mDataList;
    private RecyclerView rv_photos;
    private RelativeLayout toolbar;
    private LinearLayout linearLayout;
    private int finalHeight, finalWidth;
    private FrameLayout frmMenu, frmVenue;
    private List<String> listFood, listBar;

    private VenueBarAdapter bAdapter;
    private VenueFoodAdapter fAdapter;
    private VenuePhotosAdapter gAdapter;
    private boolean wish = false, tool = false;
    public static String itemId, latitude, longitude;
    private static int NUM_PAGES = 0, currentPage = 0;
    private RelativeLayout relativeLayout1, relativeLayout2;
    private ImageView ivBack, ivWish, ivMainImageView, ivMenu, ivPhotos;
    private TextView tvTitle, tvVenueName, tvVenueLocation, tvReview, tvDescriptin1,
            tvUserName1, tvReview1, tvRated1, tvDescriptin2, tvUserName2, tvReview2, tvRated2;

    private Animation animAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_details);

        NUM_PAGES = 0;
        currentPage = 0;

        listFood = new ArrayList<>();
        listBar = new ArrayList<>();

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        itemId = bundle.getString("value");

        fAdapter = new VenueFoodAdapter(this, listFood);
        bAdapter = new VenueBarAdapter(this, listBar);
        gAdapter = new VenuePhotosAdapter(this, itemId);

        if (Utility.isInternetConnected(this)) {
            webServices(itemId);
            inIt();
        } else {
            makeToast("No Internet Connection");
        }
    }

    private void inIt() {
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        ivMainImageView = (ImageView) findViewById(R.id.scaled_image);
        tvTitle = (TextView) findViewById(R.id.venuedetail_txtTitle);

        final TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);

        ViewTreeObserver vto = ivMainImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ivMainImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                finalHeight = ivMainImageView.getMeasuredHeight();
                finalWidth = ivMainImageView.getMeasuredWidth();
                return true;
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                if (scrollY > finalHeight) {
                    tool = true;
                    ivBack.setImageResource(R.mipmap.prv);
                    toolbar_title.setText(tvTitle.getText().toString());
                    toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                    if (wish) {
                        ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b_selected));
                    } else {
                        ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b));
                    }
                } else {
                    tool = false;
                    toolbar_title.setText("");
                    ivBack.setImageResource(R.mipmap.prv_w);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    if (wish) {
                        ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_selected));
                    } else {
                        ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart));
                    }
                }
            }
        });

        ivBack = (ImageView) findViewById(R.id.back);
        ivWish = (ImageView) findViewById(R.id.toolbar_wish);

        ivBack.setOnClickListener(this);
        ivWish.setOnClickListener(this);

        mDataList = new ArrayList<>();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new PagerSlider(this, mDataList);
        mViewPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });


        tvVenueName = (TextView) findViewById(R.id.venuedetail_txtname);
        tvVenueLocation = (TextView) findViewById(R.id.venuedetail_txtLocation);

        ratingBar = (RatingBar) findViewById(R.id.venuedetail_txtRating);

        tvDescriptin1 = (TextView) findViewById(R.id.review_txtDescription1);
        tvUserName1 = (TextView) findViewById(R.id.review_txtUsername1);
        tvReview1 = (TextView) findViewById(R.id.review_txtSubTitle1);
        tvRated1 = (TextView) findViewById(R.id.review_txtRating1);

        tvDescriptin2 = (TextView) findViewById(R.id.review_txtDescription2);
        tvUserName2 = (TextView) findViewById(R.id.review_txtUsername2);
        tvReview2 = (TextView) findViewById(R.id.review_txtSubTitle2);
        tvRated2 = (TextView) findViewById(R.id.review_txtRating2);

        RecyclerView rvFood = (RecyclerView) findViewById(R.id.venuedetail_rvFood);
        rvFood.setHasFixedSize(true);
        rvFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFood.setAdapter(fAdapter);

        RecyclerView rvBar = (RecyclerView) findViewById(R.id.venuedetail_rvBar);
        rvBar.setHasFixedSize(true);
        rvBar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvBar.setAdapter(bAdapter);

        rv_photos = (RecyclerView) findViewById(R.id.venuedetail_rv_photos);
        rv_photos.setHasFixedSize(true);

        rv_photos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_photos.setAdapter(gAdapter);

        final ViewPager pager = (ViewPager) findViewById(R.id.venuedetail_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.venuedetail_tabLayout);

        FragmentManager manager = getSupportFragmentManager();
        EventDetailPagerAdapter eventPager = new EventDetailPagerAdapter(manager);
        pager.setAdapter(eventPager);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(eventPager);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        webServicesMenu(itemId);

        RelativeLayout rlMap = (RelativeLayout) findViewById(R.id.venuedetail_rlMap);
        rlMap.setOnClickListener(this);

        frmMenu = (FrameLayout) findViewById(R.id.venuedetail_frmMenu);
        frmVenue = (FrameLayout) findViewById(R.id.venuedetail_frmVenue);
        frmMenu.setOnClickListener(this);
        frmVenue.setOnClickListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);

        ivMenu = (ImageView) findViewById(R.id.venuedetail_ivMenu);
        ivPhotos = (ImageView) findViewById(R.id.venuedetail_ivPhotos);

        tvReview = (TextView) findViewById(R.id.venuedetail_txtReview);
        tvReview.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        ((VenueFoodAdapter) fAdapter).setOnClickListener(new VenueFoodAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position) {
                final Bundle bundle = new Bundle();
                bundle.putString("itemId", itemId);
                bundle.putString("value", "food");
                bundle.putInt("position", position);
                launchIntent(ActivityImageSlider.class, bundle, false);
            }
        });

        ((VenueBarAdapter) bAdapter).setOnClickListener(new VenueBarAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position) {
                final Bundle bundle = new Bundle();
                bundle.putString("itemId", itemId);
                bundle.putString("value", "bar");
                bundle.putInt("position", position);
                launchIntent(ActivityImageSlider.class, bundle, false);
            }
        });

        ((VenuePhotosAdapter) gAdapter).setOnClickListener(new VenuePhotosAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position) {
                final Bundle bundle = new Bundle();
                bundle.putString("itemId", itemId);
                bundle.putString("value", "photos");
                bundle.putInt("position", position);
                launchIntent(ActivityImageSlider.class, bundle, false);

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                onBackPressed();
                break;
            case R.id.toolbar_wish:
                if (USERUNIQUEID == null) {
                    launchIntent(ActivityLogin.class, false);
                } else {
                    if (wish) {
                        wish = false;
                        Constants.deleteWishlist("venue", BaseDrawerActivity.USERUNIQUEID, itemId);
                        if (tool) {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b));
                        } else {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart));
                        }
                    } else {
                        wish = true;
                        Constants.addWishlist("venue", BaseDrawerActivity.USERUNIQUEID, itemId);
                        if (tool) {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b_selected));
                        } else {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_selected));
                        }
                    }
                }
                break;
            case R.id.venuedetail_rlMap:
                Bundle bundle = new Bundle();
                Intent intent = new Intent(ActivityVenuesDetails.this, MapsActivity.class);
                bundle.putString("title", tvVenueLocation.getText() + "");
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.venuedetail_frmMenu:
                v.startAnimation(animAlpha);
                if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                    ivMenu.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
                } else {
                    if (rv_photos.getVisibility() == View.VISIBLE) {
                        rv_photos.setVisibility(View.GONE);
                        ivPhotos.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
                    }
                    linearLayout.setVisibility(View.VISIBLE);
                    ivMenu.setImageDrawable(getResources().getDrawable(R.mipmap.top_grey));
                }
                break;
            case R.id.venuedetail_frmVenue:
                v.startAnimation(animAlpha);
                if (rv_photos.getVisibility() == View.VISIBLE) {
                    rv_photos.setVisibility(View.GONE);
                    ivPhotos.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
                } else {
                    if (linearLayout.getVisibility() == View.VISIBLE) {
                        linearLayout.setVisibility(View.GONE);
                        ivMenu.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
                    }
                    rv_photos.setVisibility(View.VISIBLE);
                    ivPhotos.setImageDrawable(getResources().getDrawable(R.mipmap.top_grey));
                }
                break;
            case R.id.venuedetail_txtReview:
                Bundle bundles = new Bundle();
                Intent intent1 = new Intent(ActivityVenuesDetails.this, ActivityReview.class);
                bundles.putString("value", itemId);
                intent1.putExtras(bundles);
                startActivity(intent1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    private void webServices(final String ids) {
        showProgress();
        String URLs = Constants.BaseURL + "venuedetail.php?venue=" + ids + "&userid=" + USERUNIQUEID;
        Log.e("URLs", URLs);
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dismissProgress();
                        Log.e("venue detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray jsonArray = mResponse.getJSONArray("detail");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            itemId = jsonObject.getString("id");
                            tvTitle.setText(jsonObject.getString("title"));

                            //tvVenueName.setText(jsonObject.getString("loc_title"));
                            tvVenueName.setText(jsonObject.getString("title"));
                            tvVenueLocation.setText(jsonObject.getString("address"));
                            try {
                                ratingBar.setRating(Float.parseFloat(jsonObject.getString("rating")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            if (jsonObject.getString("userwish").equals("1")) {
                                wish = true;
                                ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_selected));
                            }

                            latitude = jsonObject.getString("lat");
                            longitude = jsonObject.getString("lng");

                            JSONArray slide = mResponse.getJSONArray("slide");
                            for (int i = 0; i < slide.length(); i++) {
                                mDataList.add(Constants.BaseImage + slide.getString(i));
                            }

                            JSONArray review = mResponse.getJSONArray("review");

                            if (review.length() > 0) {
                                JSONObject jsonObject1 = review.getJSONObject(0);
                                tvUserName1.setText(jsonObject1.getString("user_name"));
                                tvReview1.setText(jsonObject1.getString("0"));
                                tvDescriptin1.setText(jsonObject1.getString("r_desc"));
                                tvRated1.setText(jsonObject1.getString("rating"));
                                tvDescriptin1.setVisibility(View.VISIBLE);
                                relativeLayout1.setVisibility(View.VISIBLE);
                            }
                            if (review.length() > 1) {
                                JSONObject jsonObject2 = review.getJSONObject(1);
                                tvUserName2.setText(jsonObject2.getString("user_name"));
                                tvReview2.setText(jsonObject2.getString("0"));
                                tvDescriptin2.setText(jsonObject2.getString("r_desc"));
                                tvRated2.setText(jsonObject2.getString("rating"));
                                tvDescriptin2.setVisibility(View.VISIBLE);
                                relativeLayout2.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NUM_PAGES = mDataList.size();
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

    private void webServicesMenu(final String ids) {

        String URLs = Constants.BaseURL + "venuedetail.php?food=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("menu detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray foodArray = mResponse.getJSONArray("food");

                            for (int i = 0; i < foodArray.length(); i++) {
                                listFood.add(foodArray.getString(i));
                            }

                            JSONArray barArray = mResponse.getJSONArray("bar");

                            for (int i = 0; i < barArray.length(); i++) {
                                listBar.add(barArray.getString(i));
                            }
                            fAdapter.notifyDataSetChanged();
                            bAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    }
}


