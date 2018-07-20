package in.clubgo.app.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.adapter.ReviewAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.modal.ModalReview;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityReview extends BaseActivity {

    private ImageView ivBack;
    private ReviewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalReview> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(this);
        mDataList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_review);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReviewAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);


        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        webServices(bundle.getString("value"));

    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private void webServices(final String ids) {
        showProgress();
        String URLs = Constants.BaseURL + "review.php?itemid=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        Log.e("Review", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray mArray = mResponse.getJSONArray("reviewlist");

                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject jsonObject = mArray.getJSONObject(i);
                                ModalReview list = new ModalReview();
                                list.setUserid(jsonObject.getString("userid"));
                                list.setReviewid(jsonObject.getString("reviewid"));
                                list.setRating(jsonObject.getString("rating"));
                                list.setUsername(jsonObject.getString("username"));
                                list.setDesc(jsonObject.getString("desc"));
                                list.setReview(jsonObject.getString("review"));
                                list.setTime(jsonObject.getString("time"));
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
