package in.clubgo.app.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.VipTableAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.Tickets.ActivityTickets;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.modal.ModalVipTable;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityVIPTable extends BaseActivity {

    private VipTableAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalVipTable> mDataList;

    private ImageView ivBack;
    private TextView tvBook, tvCall;

    private static String itemId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viptable);

        inIt();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((VipTableAdapter) mAdapter).setOnClickListener(new VipTableAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, boolean bools) {
                if (bools) {
                    itemId = mDataList.get(position).getId();
                    Log.e("VIP True", itemId);
                } else {
                    itemId = null;
                    Log.e("VIP True", "Soam > " + itemId + " < Soam");
                }

            }
        });
    }

    private void inIt() {

        mDataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_vip);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new VipTableAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);


        ivBack = (ImageView) findViewById(R.id.back);
        tvCall = (TextView) findViewById(R.id.vip_txtCall);
        tvBook = (TextView) findViewById(R.id.vip_txtBookNow);

        ivBack.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        tvBook.setOnClickListener(this);

        webServices(ActivityEventsDetails.eventId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.vip_txtBookNow:
                if (itemId != null) {
                    webServicesBook(itemId);
                }
                break;
            case R.id.vip_txtCall:
                AppController.getInstance().mCalling(this);
                break;
            default:
                break;
        }
    }

    private void webServices(final String ids) {
        showProgress();
        String URLs = Constants.BaseURL + "eventdetailjson.php?e_vip=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        // Result handling
                        Log.e("vip table detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray mArray = mResponse.getJSONArray("vipticket");

                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject jsonObject = mArray.getJSONObject(i);
                                ModalVipTable list = new ModalVipTable();
                                list.setId(jsonObject.getString("vip_id"));
                                list.setName(jsonObject.getString("vip_name"));
                                list.setPrice("\u20B9 " + jsonObject.getString("vip_price"));
                                list.setDescription(jsonObject.getString("vip_desc"));
                                list.setItemId(jsonObject.getString("e_id"));
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
                makeToast("Network Error!\nPlease try Again");

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void webServicesBook(final String vipId) {

        showProgress();
        Log.e("vipId", vipId);
        Log.e("userid", USERUNIQUEID);
        String URLs = Constants.BaseURL + "vipbook.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dismissProgress();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("vipbook") == 0) {
                                Toast.makeText(getApplicationContext(), "You are bit late!", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getInt("vipbook") == 1) {
                                Toast.makeText(getApplicationContext(), "Congratulations!\nyour table is booked", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityVIPTable.this, ActivityTickets.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                ActivityVIPTable.this.finish();

                            }
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("vip", vipId);
                params.put("userid", USERUNIQUEID);
                params.put("date", ActivityEventsDetails.bookDate);
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

    }
}
