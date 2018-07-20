package in.clubgo.app.fragment;

/**
 * Created by Jitendra Soam on 20/4/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.adapter.VenueListAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.modal.ModalEventList;
import in.clubgo.app.venues.ActivityVenuesDetails;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentHighlights extends Fragment {

    private VenueListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<ModalEventList> mDataList;

    public FragmentHighlights() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highlights, container, false);

        mDataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new VenueListAdapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        webServices(ActivityVenuesDetails.itemId);
        return view;
    }

    private void webServices(final String ids) {

        String URLs = Constants.BaseURL + "venuedetail.php?high=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("venue detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray jsonArray = mResponse.getJSONArray("high");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                ModalEventList list = new ModalEventList();
                                list.setImage(R.drawable.ticks);
                                list.setTitle(jsonArray.getString(i));
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

    private int getImage(int position) {

        switch (position) {

            case 0:
                return R.mipmap.seating;
            case 1:
                return R.mipmap.nightlife;
            case 2:
                return R.mipmap.bar;
            case 4:
                return R.mipmap.dance;
            case 5:
                return R.mipmap.ratings;
            case 6:
                return R.mipmap.ci;
            case 7:
                return R.mipmap.sports;
            default:
                return R.mipmap.seating;
        }


    }
}
