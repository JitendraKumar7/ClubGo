package in.clubgo.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.app.AppController;
import in.clubgo.app.venues.ActivityVenuesDetails;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Jitendra Soam on 20/4/16.
 */
public class FragmentBusyNight extends Fragment {

    private TextView tvTitle;
    public FragmentBusyNight() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busy_night, container, false);
        tvTitle = (TextView) view.findViewById(R.id.busy_night);
        webServices(ActivityVenuesDetails.itemId);
        return view;
    }

    private void webServices(final String ids) {

        String URLs = Constants.BaseURL + "venuedetail.php?busy=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("venue detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            String str = "";
                            JSONArray jsonArray = mResponse.getJSONArray("busy");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (i == jsonArray.length() - 1)
                                    str = str + jsonArray.getString(i);
                                else
                                    str = str + jsonArray.getString(i) + ", ";
                            }
                            tvTitle.setText(str);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }
}

