package in.clubgo.app.venues;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
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
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.utility.Constants;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityVenue extends BaseDrawerActivity implements View.OnClickListener {

    private String str1, str2;
    private TextView tvTxt1, tvTxt2;
    private ImageView ivImg1, ivImg2;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        ivBtnVenues.setImageResource(R.mipmap.venue_selected);
        tvBtnVenues.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        inIt();

    }

    private void inIt() {

        ivImg1 = (ImageView) findViewById(R.id.venues_img1);
        ivImg2 = (ImageView) findViewById(R.id.venues_img2);

        tvTxt1 = (TextView) findViewById(R.id.venues_txt1);
        tvTxt2 = (TextView) findViewById(R.id.venues_txt2);

        if (ConnectivityReceiver.isConnected()) {
            showProgress();
            webService();
        } else {
            showDialog(false);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                webService();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (str1 != null && str2 != null) {
            switch (v.getId()) {
                case R.id.venues_img1:
                    bundle.putString("cafe", str1);
                    bundle.putString("clubs", str2);
                    bundle.putString("value", str1);
                    launchIntent(ActivityLists.class, bundle, false);
                    break;
                case R.id.venues_img2:
                    bundle.putString("cafe", str1);
                    bundle.putString("clubs", str2);
                    bundle.putString("value", str2);
                    launchIntent(ActivityLists.class, bundle, false);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            webService();
            hideDialog();
        } else {
            showDialog(false);
        }
    }

    private void webService() {
        String URLs = Constants.BaseURL + "catejson.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.e("Venue Response", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray venueArray = mResponse.getJSONArray("cate");

                            JSONObject jsonObject1 = venueArray.getJSONObject(0);
                            str1 = jsonObject1.getString("cate_id");
                            Picasso.with(activity)
                                    .load(Constants.BaseImage + jsonObject1.getString("image"))
                                    .placeholder(R.drawable.image_holder)
                                    .fit().centerCrop()
                                    .into(ivImg1);
                            tvTxt1.setText(jsonObject1.getString("cate_title"));
                            ivImg1.setOnClickListener(ActivityVenue.this);


                            JSONObject jsonObject2 = venueArray.getJSONObject(1);
                            str2 = jsonObject2.getString("cate_id");
                            Picasso.with(activity)
                                    .load(Constants.BaseImage + jsonObject2.getString("image"))
                                    .placeholder(R.drawable.image_holder)
                                    .fit().centerCrop()
                                    .into(ivImg2);
                            tvTxt2.setText(jsonObject2.getString("cate_title"));
                            ivImg2.setOnClickListener(ActivityVenue.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dismissProgress();
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
