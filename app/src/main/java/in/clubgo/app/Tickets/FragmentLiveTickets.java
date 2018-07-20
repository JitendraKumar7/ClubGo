package in.clubgo.app.Tickets;

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

import in.clubgo.app.adapter.TicketsAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.base.BaseFragment;
import in.clubgo.app.modal.ModalBooked;
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
public class FragmentLiveTickets extends BaseFragment {

    private TicketsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalBooked> mDataList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FragmentLiveTickets() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        mDataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new TicketsAdapter(0, getActivity(), mDataList);
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

    private void webService(boolean b) {
        if (b) {
            showProgress();
        }
        String URLs = Constants.BaseURL + "bookget.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        mDataList.clear();
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("live");
                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject obj = artistArray.getJSONObject(i);
                                ModalBooked list = new ModalBooked();
                                list.setIds(obj.getString("item_id").trim());
                                list.setEventId(obj.getString("event").trim());
                                list.setEventname(obj.getString("event_name").trim());
                                list.setImage(Constants.BaseImage + obj.getString("feat_image"));
                                list.setVenuename(obj.getString("title").trim());
                                list.setVenuelocation(obj.getString("location").trim());

                                if (obj.getString("type").equals("normal")) {
                                    list.setMaleticket(obj.getString("male_ticket").trim());
                                    list.setFemaleticket(obj.getString("female_ticket").trim());
                                    list.setCoupleticket(obj.getString("couple_ticket").trim());
                                    list.setBookedprice(obj.getString("booked_price").trim());
                                    list.setBookid(obj.getString("dynamic_id").trim());
                                    list.setType(obj.getString("type").trim());
                                    list.setName(obj.getString("tic_name").trim());
                                    list.setDate(obj.getString("tiime").trim() + " onwords " + obj.getString("DATE").trim());
                                    list.setLastEntry("Last Entry Time " + obj.getString("lastentry").trim());
                                } else if (obj.getString("type").equals("vip")) {
                                    list.setMaleticket("J");
                                    list.setFemaleticket("J");
                                    list.setCoupleticket("J");
                                    list.setLastEntry("J");
                                    list.setBookid(obj.getString("dynamic").trim());
                                    list.setBookedprice(obj.getString("vip_price").trim());
                                    list.setVipName(obj.getString("vip_name").trim());
                                    list.setVipDes(obj.getString("vip_desc").trim());
                                    list.setType(obj.getString("type").trim());
                                    list.setDate(obj.getString("DATE").trim());
                                    list.setName("VIP Table");
                                }
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
                params.put("group", "live");
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




