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

import in.clubgo.app.adapter.VenueAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.base.BaseFragment;
import in.clubgo.app.modal.ModalVenues;
import in.clubgo.app.venues.ActivityVenuesDetails;
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
public class FragmentWishListVenues extends BaseFragment {

    private VenueAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalVenues> mDataList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FragmentWishListVenues() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        mDataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new VenueAdapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

        ((VenueAdapter) mAdapter).setOnClickListener(new VenueAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                Intent intent = new Intent(getActivity(), ActivityVenuesDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void webService(boolean b) {
        if (b) {
            showProgress();
        }
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.BaseURL + "userwishget.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray venueArray = mResponse.getJSONArray("venue");
                            for (int i = 0; i < venueArray.length(); i++) {
                                JSONObject jsonObject = venueArray.getJSONObject(i);
                                ModalVenues list = new ModalVenues();
                                list.setId(jsonObject.getString("item_id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("item_img1"));
                                list.setTitle(jsonObject.getString("item_title"));
                                list.setLocation(jsonObject.getString("item_location"));
                                list.setDesc("");
                                list.setWish("1");
                                list.setRating(jsonObject.getString("item_rating"));
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
                params.put("category", "venue");
                params.put("userid", BaseDrawerActivity.USERUNIQUEID);
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
