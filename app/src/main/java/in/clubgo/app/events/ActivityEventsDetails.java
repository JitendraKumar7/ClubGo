package in.clubgo.app.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
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

import in.clubgo.app.adapter.EventDetailGridAdapter;
import in.clubgo.app.adapter.EventDetailListAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.book.ActivityBook;
import in.clubgo.app.book.ActivityVIPTable;
import in.clubgo.app.indicator.CirclePageIndicator;
import in.clubgo.app.main.ActivityLogin;
import in.clubgo.app.map.MapsActivity;
import in.clubgo.app.modal.ModalSingleEvent;
import in.clubgo.app.pager.PagerSlider;
import in.clubgo.app.popups.PopUpEventsMenu;
import in.clubgo.app.review.ActivityReview;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityEventsDetails extends BaseActivity {

    private ScrollView scrollView;
    private RecyclerView rvGrid, rvList;
    private EventDetailGridAdapter gridAdapter;
    private EventDetailListAdapter listAdapter;
    private List<ModalSingleEvent> list1, list2;

    private PopUpEventsMenu popUpEventsMenu;
    private String itemId, latitude, longitude;
    private Fragment fEventDetail, fEventTickets;

    private RatingBar mRatingBar;
    private RelativeLayout toolbar;
    private int finalHeight, finalWidth;
    private TextView tvEvents, tvTickets;
    private boolean wish = false, tool = false;
    private ImageView ivBack, ivWish, ivMainImageView, ivEvents, ivTickets, ivEventMenu;
    private TextView tvTitle, tvReview, tvType, tvVenueName, tvName, tvLocation, tvBookFull, tvBook, tvVipTable;

    private ViewPager mViewPager;
    private PagerSlider mAdapter;
    private List<String> mDataList;
    private String shareTime;
    public int NUM_PAGES = 0, currentPage = 0;
    public static String title, eventId, ticId, bookDate, time;

    private Animation animAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_details);
        time = "";
        ticId = "";
        eventId = "";
        bookDate = "";
        NUM_PAGES = 0;
        currentPage = 0;

        popUpEventsMenu = new PopUpEventsMenu(this);

        if (Utility.isInternetConnected(this)) {

            inIt();

            final Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            eventId = bundle.getString("value");
            webServices(eventId);
        } else {
            showDialog(false);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((PopUpEventsMenu) popUpEventsMenu).setOnItemClickListener(new PopUpEventsMenu.MyClickListener() {
            @Override
            public void onItemClick(View v) {

                switch (v.getId()) {
                    case R.id.menu_frmLocate:
                        Bundle bundle = new Bundle();
                        bundle.putString("title", tvName.getText() + "");
                        bundle.putString("latitude", latitude);
                        bundle.putString("longitude", longitude);
                        launchIntent(MapsActivity.class, bundle, false);
                        break;
                    case R.id.menu_frmShare:
                        String event = tvTitle.getText().toString();
                        String start = shareTime;
                        String venue = tvLocation.getText().toString();
                        String str = event + "\nStart At : " + start + "\nVenue : " + venue;
                        shareListUrl(ActivityEventsDetails.this, str);
                        break;
                    case R.id.menu_frmCallUs:
                        AppController.getInstance().mCalling(ActivityEventsDetails.this);
                        break;
                    default:
                        break;
                }
                popUpEventsMenu.mDismiss();
            }
        });
    }

    private void inIt() {

        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        ivMainImageView = (ImageView) findViewById(R.id.scaled_image);
        tvTitle = (TextView) findViewById(R.id.eventdetail_txtTitle);

        tvReview = (TextView) findViewById(R.id.eventdetail_txtReview);
        tvType = (TextView) findViewById(R.id.eventdetail_txtType);
        tvName = (TextView) findViewById(R.id.eventdetail_txtName);
        tvLocation = (TextView) findViewById(R.id.eventdetail_txtLocation);
        tvVenueName = (TextView) findViewById(R.id.eventdetail_txtVenueName);

        mRatingBar = (RatingBar) findViewById(R.id.eventdetail_rating);

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

        rvGrid = (RecyclerView) findViewById(R.id.rv_grid);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        rvGrid.setHasFixedSize(true);
        rvList.setHasFixedSize(true);

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();

        rvGrid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        gridAdapter = new EventDetailGridAdapter(list1, this);
        rvGrid.setAdapter(gridAdapter);

        listAdapter = new EventDetailListAdapter(list2, this);
        rvList.setAdapter(listAdapter);

        RelativeLayout frmMap = (RelativeLayout) findViewById(R.id.eventdetail_rlMap);
        frmMap.setOnClickListener(this);

        tvEvents = (TextView) findViewById(R.id.event_txtEventDetail);
        tvTickets = (TextView) findViewById(R.id.event_txtTicketDetail);
        ivEvents = (ImageView) findViewById(R.id.imgEventDetail);
        ivTickets = (ImageView) findViewById(R.id.imgTicketsDetail);

        ivEventMenu = (ImageView) findViewById(R.id.event_menu);
        ivBack = (ImageView) findViewById(R.id.back);
        ivWish = (ImageView) findViewById(R.id.toolbar_wish);
        tvBook = (TextView) findViewById(R.id.event_txtBook);
        tvBookFull = (TextView) findViewById(R.id.event_txtBookFull);
        tvVipTable = (TextView) findViewById(R.id.event_txtVipTable);

        tvEvents.setOnClickListener(this);
        tvTickets.setOnClickListener(this);

        ivEventMenu.setOnClickListener(this);
        tvReview.setOnClickListener(this);

        ivBack.setOnClickListener(this);
        ivWish.setOnClickListener(this);

        tvBook.setOnClickListener(this);
        tvBookFull.setOnClickListener(this);
        tvVipTable.setOnClickListener(this);

        tvTickets.performClick();

    }

    // Add your Fragment Event Detail
    private void addEventFragment() {
        fEventDetail = new FragmentDetail();
        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        transation.replace(R.id.frmEventDetail, fEventDetail);
        transation.commit();
        ivEvents.setImageDrawable(getResources().getDrawable(R.mipmap.top_grey));
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    // Remove Fragment Event Detail
    private void removeEventFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fEventDetail != null) {
            transaction.remove(fEventDetail);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fEventDetail = null;
            ivEvents.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
        }
    }

    // Add your Fragment Ticket
    private void addTicketFragment() {
        fEventTickets = new FragmentTickets();
        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        transation.replace(R.id.frmTickets, fEventTickets);
        transation.commit();
        ivTickets.setImageDrawable(getResources().getDrawable(R.mipmap.top_grey));
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    // Remove Fragment Ticket
    private void removeTicketFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fEventTickets != null) {
            transaction.remove(fEventTickets);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fEventTickets = null;
            ivTickets.setImageDrawable(getResources().getDrawable(R.mipmap.bottom_grey));
        }
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
                        Constants.deleteWishlist("event", BaseDrawerActivity.USERUNIQUEID, eventId);
                        if (tool) {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b));
                        } else {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart));
                        }
                    } else {
                        wish = true;
                        Constants.addWishlist("event", BaseDrawerActivity.USERUNIQUEID, eventId);
                        if (tool) {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_b_selected));
                        } else {
                            ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_selected));
                        }
                    }
                }
                break;
            case R.id.event_txtEventDetail:
                v.startAnimation(animAlpha);
                if (fEventDetail == null) {
                    if (fEventTickets != null) {
                        removeTicketFragment();
                    }
                    addEventFragment();
                } else {
                    removeEventFragment();
                }
                break;
            case R.id.event_txtTicketDetail:
                v.startAnimation(animAlpha);
                if (fEventTickets == null) {
                    if (fEventDetail != null) {
                        removeEventFragment();
                    }
                    addTicketFragment();
                } else {
                    removeTicketFragment();
                }
                break;

            case R.id.eventdetail_rlMap:
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", tvName.getText() + "");
                bundle1.putString("latitude", latitude);
                bundle1.putString("longitude", longitude);
                launchIntent(ActivityReview.class, bundle1, false);
                break;

            case R.id.event_menu:
                popUpEventsMenu.mShow(ivEventMenu);
                break;
            case R.id.eventdetail_txtReview:
                Bundle bundle2 = new Bundle();
                bundle2.putString("value", itemId);
                launchIntent(ActivityReview.class, bundle2, false);
                break;
            case R.id.event_txtBook:
            case R.id.event_txtBookFull:
                if (USERUNIQUEID == null) {
                    launchIntent(ActivityLogin.class, false);
                } else {
                    if (ticId.equals("")) {
                        tvTickets.performClick();
                    } else if (eventId.equals("")) {
                        tvTickets.performClick();
                    } else if (bookDate.equals("")) {
                        tvTickets.performClick();
                    } else {

                        if (FragmentTickets.CoupleCount == 0 &&
                                FragmentTickets.FemaleCount == 0 &&
                                FragmentTickets.MaleCount == 0) {
                            makeToast("Please Select Number of People");
                        } else {
                            webServices(eventId, FragmentTickets.type + "", bookDate);
                        }
                    }
                }
                break;
            case R.id.event_txtVipTable:
                if (USERUNIQUEID == null) {
                    launchIntent(ActivityLogin.class, false);
                } else {
                    if (ticId.equals("")) {
                        tvTickets.performClick();
                    } else if (eventId.equals("")) {
                        tvTickets.performClick();
                    } else if (bookDate.equals("")) {
                        tvTickets.performClick();
                    } else {
                        launchIntent(ActivityVIPTable.class, true);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void webServices(final String ids) {
        showProgress();
        String URLs = Constants.BaseURL + "eventdetailjson.php?e_id=" + ids + "&userid=" + USERUNIQUEID;
        Log.e("URLs", URLs);
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("event detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONObject jsonObject = mResponse.getJSONObject("eventdetail");
                            JSONArray eArray = jsonObject.getJSONArray("event");
                            JSONObject obj = eArray.getJSONObject(0);

                            tvTitle.setText(obj.getString("e_name"));
                            if (!(obj.getString("reviews").equals("0"))) {
                                tvReview.setVisibility(View.VISIBLE);
                                tvReview.setText(obj.getString("reviews") + " Reviews");
                            }
                            String tag1 = obj.getString("tag1");
                            String tag2 = obj.getString("tag2");

                            if (tag1.equals(tag2) || tag2.equals("")) {
                                tvType.setText(obj.getString("tag1"));
                            } else {
                                tvType.setText(obj.getString("tag1") + ", " + obj.getString("tag2"));
                            }
                            tvVenueName.setText(obj.getString("heading"));
                            tvName.setText(obj.getString("venue"));
                            tvLocation.setText(obj.getString("location"));
                            shareTime = obj.getString("TIME");

                            if (obj.getString("vip_exist").equals("1")) {
                                tvBookFull.setVisibility(View.GONE);
                                tvBook.setVisibility(View.VISIBLE);
                                tvVipTable.setVisibility(View.VISIBLE);
                            }

                            if (obj.getString("userwish").equals("1")) {
                                ivWish.setImageDrawable(getResources().getDrawable(R.mipmap.ic_heart_selected));
                                wish = true;
                            }

                            itemId = obj.getString("item_id");
                            latitude = obj.getString("item_lat");
                            longitude = obj.getString("item_lng");

                            try {
                                mRatingBar.setRating(Float.parseFloat(obj.getString("item_rating")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            loadWebView(obj.getString("e_desc"));

                            JSONArray ivArray = jsonObject.getJSONArray("image");

                            for (int i = 0; i < ivArray.length(); i++) {
                                ModalSingleEvent list = new ModalSingleEvent();
                                list.setPath(Constants.BaseImage + ivArray.getString(i));
                                list1.add(list);
                            }

                            JSONArray ofArray = jsonObject.getJSONArray("offer");
                            for (int i = 0; i < ofArray.length(); i++) {
                                JSONObject offer = ofArray.getJSONObject(i);
                                ModalSingleEvent list = new ModalSingleEvent();
                                list.setOffer(offer.getString("offer_title"));
                                list.setTitle(offer.getString("offer_desc"));
                                list2.add(list);
                                if (rvList.getVisibility() == View.GONE) {
                                    rvList.setVisibility(View.VISIBLE);
                                }
                            }

                            JSONArray slide = mResponse.getJSONArray("slide");
                            for (int i = 0; i < slide.length(); i++) {
                                mDataList.add(Constants.BaseImage + slide.getString(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NUM_PAGES = mDataList.size();
                        mAdapter.notifyDataSetChanged();
                        listAdapter.notifyDataSetChanged();
                        gridAdapter.notifyDataSetChanged();
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

    private void loadWebView(final String string) {
        String str = "<html> <head> </head> <body style=\"text-align:justify;color:black;background-color:#F5F5F5;font-size:14px;\">" + string + " </body> </html>";
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadData(str, "text/html; charset=utf-8", "utf-8");
    }

    private void shareListUrl(final Context ctx, final String str) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Club Go Event Detail");
        share.putExtra(
                Intent.EXTRA_TEXT, str);

        ctx.startActivity(Intent.createChooser(share, "Entertainment Application"));
    }

    private void webServices(final String eventid, final String type, final String bookdate) {

        String[] separate = bookdate.split(":");
        final String date = separate[0] + " " + separate[1] + " " + separate[2];

        String URLs = Constants.BaseURL + "bookingcheck.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("event ticket", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            if (mResponse.getString("msg").equals("1")) {
                                launchIntent(ActivityBook.class, true);
                            } else {
                                makeToast("Sory! you are bit late!");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
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
                params.put("bookdate", date);
                params.put("type", type);
                params.put("eventid", eventid);
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            hideDialog();
        } else {
            showDialog(false);
        }
    }
}