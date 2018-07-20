package in.clubgo.app.artiest;


import android.content.Intent;
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
import in.clubgo.app.utility.Utility;
import in.clubgo.app.utility.Constants;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityArtistDetails extends BaseActivity {

    private Bundle bundle;
    private ImageView ivBack, ivArtist;
    private TextView tvTitle, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        ivBack = (ImageView) findViewById(R.id.artist_back);
        tvTitle = (TextView) findViewById(R.id.artist_txtTitle);
        ivArtist = (ImageView) findViewById(R.id.artist_imageView);
        tvDescription = (TextView) findViewById(R.id.artist_txtDetail);

        ivBack.setOnClickListener(this);

        if (Utility.isInternetConnected(this)) {
            final Intent intent = this.getIntent();
            bundle = intent.getExtras();
            webService(Constants.BaseURL + "artistjson.php?id=" + bundle.getString("value"));
        } else {
            showDialog(false);
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    public void webService(final String URLs) {
        showProgress();
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgress();
                        // Result handling
                        Log.e("Artist Response", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONArray artistArray = mResponse.getJSONArray("dj");
                            JSONObject jsonObject = artistArray.getJSONObject(0);
                            jsonObject.getString("d_id");
                            tvTitle.setText(jsonObject.getString("d_name"));
                            tvDescription.setText(jsonObject.getString("d_desc"));

                            Picasso.with(getApplicationContext())
                                    .load(Constants.BaseImage + jsonObject.getString("d_img"))
                                    .placeholder(R.drawable.image_holder)
                                    .fit().centerInside()
                                    .into(ivArtist);


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
            webService(Constants.BaseURL + "artistjson.php?id=" + bundle.getString("value"));
            hideDialog();
        } else {
            showDialog(false);
        }
    }
}