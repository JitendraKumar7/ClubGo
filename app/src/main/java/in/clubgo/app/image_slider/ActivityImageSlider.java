package in.clubgo.app.image_slider;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.pager.PagerImageSlider;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityImageSlider extends BaseActivity {

    private TextView tvName;
    private ViewPager mViewPager;
    private List<String> mDataList;
    private PagerImageSlider mAdapter;

    private String URLs;
    private int position = 0;
    private ImageView ivBack;
    private String value, itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        mDataList = new ArrayList<>();

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        value = bundle.getString("value");
        itemId = bundle.getString("itemId");
        position = bundle.getInt("position");

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tvName = (TextView) findViewById(R.id.slider_txtName);
        mAdapter = new PagerImageSlider(this, mDataList);
        mViewPager.setAdapter(mAdapter);

        if (value.equals("food") || value.equals("bar")) {
            URLs = Constants.BaseURL + "venuedetail.php?food=" + itemId;
            webServices(URLs);
        } else if (value.equals("artist")) {
            tvName.setText(bundle.getString("title"));
            URLs = Constants.BaseURL + "eventdetailjson.php?e_artist=" + itemId;
            webServices(URLs);
        } else if (value.equals("dress")) {
            tvName.setText(bundle.getString("title"));
            URLs = Constants.BaseURL + "eventdetailjson.php?e_dress=" + itemId;
            webServices(URLs);
        } else if (value.equals("menu")) {
            URLs = Constants.BaseURL + "eventdetailjson.php?e_menu=" + itemId;
            webServices(URLs);
        } else {
            URLs = Constants.BaseURL + "venuedetail.php?photo=" + itemId;
            webServices(URLs);
        }

        ivBack = (ImageView) findViewById(R.id.slider_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void webServices(final String URLs) {
        Log.e(value, URLs);

        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("Slider Response", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            if (value.equals("food")) {
                                JSONArray mArray = mResponse.getJSONArray("food");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            } else if (value.equals("bar")) {
                                JSONArray mArray = mResponse.getJSONArray("bar");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            } else if (value.equals("artist")) {
                                JSONArray mArray = mResponse.getJSONArray("artist");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            } else if (value.equals("dress")) {
                                JSONArray mArray = mResponse.getJSONArray("dress");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            } else if (value.equals("menu")) {
                                JSONArray mArray = mResponse.getJSONArray("menu");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            } else {
                                JSONArray mArray = mResponse.getJSONArray("photo");
                                for (int i = 0; i < mArray.length(); i++) {
                                    mDataList.add(mArray.getString(i));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mViewPager.setCurrentItem(position, true);

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

    @Override
    public void onClick(View v) {

    }
}
