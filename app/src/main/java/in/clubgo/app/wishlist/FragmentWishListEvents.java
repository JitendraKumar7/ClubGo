package in.clubgo.app.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.EventAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.base.BaseFragment;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.modal.ModalEvent;
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jitendra Soam on 13/4/16.
 */
public class FragmentWishListEvents extends BaseFragment {

    private EventAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalEvent> mDataList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FragmentWishListEvents() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        mDataList = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new EventAdapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setRefreshing(true);
                webService(false);
            }
        });
        webService(true);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((EventAdapter) mAdapter).setOnItemClickListener(new EventAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {
                switch (view.getId()) {
                    /*case R.id.venue_txtBook:
                        Intent i = new Intent(ActivityWishList.this, ActivityBook.class);
                        startActivity(i);
                        break;*/
                    default:
                        Bundle bundle = new Bundle();
                        bundle.putString("value", ids);
                        launchIntent(ActivityEventsDetails.class, bundle, false);
                        break;
                }
            }
        });
    }

    private void webService(boolean b) {
        if (b) {
            showProgress();
        }

        final String stringLatitude = Utility.getPref(getActivity().getApplicationContext(), "stringLatitude", "0.00");
        final String stringLongitude = Utility.getPref(getActivity().getApplicationContext(), "stringLongitude", "0.00");
        // Request a string response
        final StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "userwishget.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("event");
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

                                list.setOffer_exist(jsonObject.getString("offer_exist"));
                                list.setOffer(jsonObject.getString("offer"));
                                list.setPrice_exist(jsonObject.getString("price_exist"));
                                list.setDiscounted_price(jsonObject.getString("discounted_price"));

                                list.setTag1(jsonObject.getString("tag1"));
                                list.setTag2(jsonObject.getString("tag2"));

                                list.setCost(jsonObject.getString("e_cost"));
                                list.setWish(jsonObject.getString("userwish"));

                                list.setDistance(jsonObject.getString("distance") + " Km");

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
                mSwipeRefreshLayout.setRefreshing(false);
                makeToast("Network Error!\nPlease try Again");

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category", "event");
                params.put("alati", stringLatitude);
                params.put("alongi", stringLongitude);
                params.put("userid", BaseActivity.USERUNIQUEID);
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




