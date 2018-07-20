package in.clubgo.app.artiest;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.AdapterArtist;
import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.base.BaseDrawerActivity;
import in.clubgo.app.modal.ModalHome;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityArtist extends BaseDrawerActivity {

    private AdapterArtist mAdapter;
    private List<ModalHome> mDataList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        ivBtnArtiest.setImageResource(R.mipmap.artist_selected);
        tvBtnArtiest.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                webService(false);
            }
        });

        inIt();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AdapterArtist) mAdapter).setMyClickListener(new AdapterArtist.MyClickListener() {
            @Override
            public void onItemClik(View view, String ids) {
                Bundle bundle = new Bundle();
                bundle.putString("value", ids);
                launchIntent(ActivityArtistDetails.class, bundle, false);
            }
        });
    }

    private void inIt() {
        mDataList = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_artist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new AdapterArtist(this, mDataList);
        recyclerView.setAdapter(mAdapter);

        if (ConnectivityReceiver.isConnected()) {
            webService(true);
        } else {
            showDialog(false);
        }
    }

    private void webService(boolean b) {
        if (b) {
            showProgress();
        }
        mDataList.clear();
        String URLs = Constants.BaseURL + "artistjson.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("dj");
                            for (int i = 0; i < artistArray.length(); i++) {
                                JSONObject jsonObject = artistArray.getJSONObject(i);
                                ModalHome list = new ModalHome();
                                list.setId(jsonObject.getString("d_id"));
                                list.setImage(Constants.BaseImage + jsonObject.getString("d_img"));
                                list.setTitle(jsonObject.getString("d_name"));
                                mDataList.add(list);
                            }

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            webService(true);
            hideDialog();
        } else {
            showDialog(false);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
