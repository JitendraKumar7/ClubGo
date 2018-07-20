package in.clubgo.app.filter;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.FiltersTagsAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.events.ActivityEvents;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityFilters extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private ArrayAdapter<String> locAdapter;
    private TextView tvCost1, tvCost2, tvCost3;
    private List<String> category, mId, mLocation;


    private RecyclerView mRecyclerView;
    private FiltersTagsAdapter mAdapter;
    private List<String> mDataList;

    private SeekBar seekBar;
    private TextView tvApply;
    private RadioGroup rgSortBy, rgOffers;
    private RadioButton rbSortBy, rbOffers;


    public static String cateId, offers, tag = "", strCost = "0", location, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        tag = "";
        mId = new ArrayList<>();
        category = new ArrayList<>();
        mLocation = new ArrayList<>();
        mDataList = new ArrayList<>();

        mId.add("0");
        mLocation.add("Select Location");

        findViewById(R.id.filter_back).setOnClickListener(this);

        if (Utility.isInternetConnected(this)) {
            inIt();
        } else {

        }
    }

    private void inIt() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_tags);
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new FiltersTagsAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);

        tvCost1 = (TextView) findViewById(R.id.filter_txtCost1);
        tvCost2 = (TextView) findViewById(R.id.filter_txtCost2);
        tvCost3 = (TextView) findViewById(R.id.filter_txtCost3);

        tvCost1.setOnClickListener(this);
        tvCost2.setOnClickListener(this);
        tvCost3.setOnClickListener(this);

        rgSortBy = (RadioGroup) findViewById(R.id.radio_sortby);
        rgOffers = (RadioGroup) findViewById(R.id.radio_offers);

        tvApply = (TextView) findViewById(R.id.filter_txtApply);
        tvApply.setOnClickListener(this);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        locAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mLocation);
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(locAdapter);

        seekBar = (SeekBar) findViewById(R.id.seekBarDistance);
        webServices();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        //String item = parent.getItemAtPosition(position).toString();
        location = mId.get(position);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.filter_back:
                onBackPressed();
                break;
            case R.id.filter_txtCost1:
                tvCost1.setTextColor(getResources().getColor(R.color.white));
                tvCost1.setBackgroundResource(R.drawable.back_reds);
                tvCost2.setTextColor(getResources().getColor(R.color.black));
                tvCost2.setBackgroundResource(R.drawable.back_gray);
                tvCost3.setTextColor(getResources().getColor(R.color.black));
                tvCost3.setBackgroundResource(R.drawable.back_gray);
                strCost = "0";
                break;
            case R.id.filter_txtCost2:
                tvCost1.setTextColor(getResources().getColor(R.color.black));
                tvCost1.setBackgroundResource(R.drawable.back_gray);
                tvCost2.setTextColor(getResources().getColor(R.color.white));
                tvCost2.setBackgroundResource(R.drawable.back_reds);
                tvCost3.setTextColor(getResources().getColor(R.color.black));
                tvCost3.setBackgroundResource(R.drawable.back_gray);
                strCost = "2";
                break;
            case R.id.filter_txtCost3:
                tvCost1.setTextColor(getResources().getColor(R.color.black));
                tvCost1.setBackgroundResource(R.drawable.back_gray);
                tvCost2.setTextColor(getResources().getColor(R.color.black));
                tvCost2.setBackgroundResource(R.drawable.back_gray);
                tvCost3.setTextColor(getResources().getColor(R.color.white));
                tvCost3.setBackgroundResource(R.drawable.back_reds);
                strCost = "3";
                break;
            case R.id.filter_txtApply:
                if (Utility.isInternetConnected(this)) {
                    apply();
                } else {
                    showDialog(false);
                }
                break;
            default:
                break;
        }
    }

    private void webServices() {
        String URLs = Constants.BaseURL + "filterget.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("Filter Response", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            category.add("all");
                            JSONArray catArray = mResponse.getJSONArray("cate");
                            for (int i = 0; i < catArray.length(); i++) {
                                JSONObject jsonObject = catArray.getJSONObject(i);
                                category.add(jsonObject.getString("cate_id"));
                            }


                            JSONArray tagArray = mResponse.getJSONArray("tag");
                            for (int i = 0; i < tagArray.length(); i++) {
                                JSONObject jsonObject = tagArray.getJSONObject(i);
                                mDataList.add(jsonObject.getString("tag_title"));
                            }
                            mAdapter.notifyDataSetChanged();

                            JSONArray locArray = mResponse.getJSONArray("location");
                            for (int i = 0; i < locArray.length(); i++) {
                                JSONObject jsonObject = locArray.getJSONObject(i);
                                mId.add(jsonObject.getString("loc_id"));
                                mLocation.add(jsonObject.getString("loc_title"));
                            }
                            locAdapter.notifyDataSetChanged();
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

    private void apply() {
        int selectedId1 = rgSortBy.getCheckedRadioButtonId();
        int selectedId2 = rgOffers.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        rbSortBy = (RadioButton) findViewById(selectedId1);
        rbOffers = (RadioButton) findViewById(selectedId2);

        switch (selectedId1) {
            case R.id.radio_sortby_all:
                cateId = category.get(0);
                break;
            case R.id.radio_sortby_cafe:
                cateId = category.get(1);
                break;
            case R.id.radio_sortby_club:
                cateId = category.get(2);
                break;
            default:
                break;
        }

        switch (selectedId2) {
            case R.id.radio_offers_all:
                offers = "all";
                break;
            case R.id.radio_offers_discounted:
                offers = "discounted";
                break;
            case R.id.radio_offers_free:
                offers = "free";
                break;
            default:
                break;
        }

        int seekValue = seekBar.getProgress();
        distance = Integer.toString(seekValue);

        Log.e("cate_id", cateId);
        Log.e("offers", offers);
        Log.e("cost", strCost);
        Log.e("location", location);
        Log.e("distance", distance);

        List<Integer> pos = mAdapter.getSelectedItems();

        for (int i = 0; i < pos.size(); i++) {
            if (i == pos.size()) {
                tag = tag + mDataList.get(pos.get(i));
            } else {
                tag = tag + mDataList.get(pos.get(i)) + ",";
            }
        }

        Log.e("Position", tag);

        if (location.equals("0")) {
            makeToast("Please Select Location");
        } else {
            launchIntent(ActivityEvents.class, false);
            Utility.savePref(getApplicationContext(), "events", "ActivityFilters");
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}

