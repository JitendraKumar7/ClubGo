package in.clubgo.app.Tickets;

import android.app.Dialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.TicketsAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.pager.TicketsPagerAdapter;
import in.clubgo.app.utility.CustomProgressDialog;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import java.util.HashMap;
import java.util.Map;

public class ActivityTickets extends BaseActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        findViewById(R.id.back).setOnClickListener(this);

        if (Utility.isInternetConnected(this)) {
            inIt();
        } else {
            showDialog(false);
        }
    }

    private void inIt() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Live Tickets"));
        tabLayout.addTab(tabLayout.newTab().setText("Past Tickets"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final TicketsPagerAdapter adapter = new TicketsPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((TicketsAdapter) new TicketsAdapter()).setOnClickListener(new TicketsAdapter.MyClickListener() {
            @Override
            public void onItemClick(View view, String str) {
                Log.e("String", str);
                switch (view.getId()) {
                    case R.id.relativeLayout:
                        Bundle bundle = new Bundle();
                        bundle.putString("value", str);
                        launchIntent(ActivityEventsDetails.class, bundle, false);
                        break;
                    case R.id.ticket_txtReview:
                        if (viewPager.getCurrentItem() == 1) {
                            addReview(str);
                        }
                        break;
                    case R.id.ticket_ivShare:
                        AppController.getInstance().shareListUrl(ActivityTickets.this, str);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void addReview(final String ids) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_add_review);

        final ImageView ivCancel = (ImageView) dialog.findViewById(R.id.booked_ivCancel);
        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.booked_rating);
        final EditText evDescription = (EditText) dialog.findViewById(R.id.booked_txtDescription);
        final TextView tvSubmit = (TextView) dialog.findViewById(R.id.booked_txtSubmit);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] i = new float[]{ratingBar.getRating()};

                String string = "" + i[0];
                //evDescription.setText("value is.. " + i[0]);
                //dialog.dismiss();
                if (evDescription.getText().toString().trim().equals("")) {
                    evDescription.setError("Description Required");
                } else {
                    webServicesBook(ids, evDescription.getText().toString().trim(), string);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();


    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private void webServicesBook(final String vId, final String review, final String rating) {

        final CustomProgressDialog pDialog = new CustomProgressDialog(ActivityTickets.this);
        pDialog.show();

        String URLs = Constants.BaseURL + "reviewadd.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        pDialog.dismiss();
                        Log.e("Add Review", response.toString());
                        Toast.makeText(getApplicationContext(), "Review Added Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                pDialog.dismiss();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", BaseDrawerActivity.USERUNIQUEID);
                params.put("venue", vId);
                params.put("review", review);
                params.put("rating", rating);
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
            inIt();
            hideDialog();
        } else {
            showDialog(false);
        }
    }
}
