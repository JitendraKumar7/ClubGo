package in.clubgo.app.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.adapter.EventDetailAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.artiest.ActivityArtistDetails;
import in.clubgo.app.image_slider.ActivityImageSlider;
import in.clubgo.app.modal.ModalEventDetails;
import in.clubgo.app.utility.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetail extends Fragment implements View.OnClickListener {

    private String id = null;
    private RecyclerView mRecyclerView;
    private EventDetailAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private ImageView imageView;
    private String rvDressCodeId;
    private List<ModalEventDetails> mDataList;
    private RelativeLayout ivArtiest, ivDressCode, ivMenu;
    private TextView tvTime, tvArtist, tvMusic, tvDressCode, tvMenu, tvCategory;

    public FragmentDetail() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        mDataList = new ArrayList<>();

        imageView = (ImageView) view.findViewById(R.id.event_imgUser);
        tvTime = (TextView) view.findViewById(R.id.event_txtSubTime);
        tvArtist = (TextView) view.findViewById(R.id.event_txtSubArtist);
        tvMusic = (TextView) view.findViewById(R.id.event_txtSubMusic);
        tvDressCode = (TextView) view.findViewById(R.id.event_txtSubDressCode);
        tvMenu = (TextView) view.findViewById(R.id.event_txtSubMenu);
        tvCategory = (TextView) view.findViewById(R.id.event_txtSubCategory);

        ivMenu = (RelativeLayout) view.findViewById(R.id.event_rvMenu);
        ivArtiest = (RelativeLayout) view.findViewById(R.id.event_rvArtist);
        ivDressCode = (RelativeLayout) view.findViewById(R.id.event_rvDressCode);

        ivMenu.setOnClickListener(this);
        ivArtiest.setOnClickListener(this);
        ivDressCode.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EventDetailAdapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mAdapter);

        webServices(ActivityEventsDetails.eventId);
        return view;
    }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);

        switch (v.getId()) {
            case R.id.event_rvArtist:
                if (id != null) {
                    bundle.putString("value", id);
                    Intent i = new Intent(getActivity(), ActivityArtistDetails.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
                break;
            case R.id.event_rvDressCode:
                bundle.putString("value", "dress");
                bundle.putString("itemId", rvDressCodeId);
                bundle.putString("title", tvDressCode.getText().toString());
                Intent intent1 = new Intent(getActivity(), ActivityImageSlider.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.event_rvMenu:
                bundle.putString("value", "menu");
                bundle.putString("itemId", ActivityEventsDetails.eventId);
                Intent intent2 = new Intent(getActivity(), ActivityImageSlider.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void webServices(final String ids) {
        mDataList.clear();
        String URLs = Constants.BaseURL + "eventdetailjson.php?e_detail=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("event detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);
                            JSONObject jsonObject = mResponse.getJSONObject("detail");

                            JSONObject obj = jsonObject.getJSONObject("0");
                            tvTime.setText(obj.getString("TIME"));
                            tvArtist.setText(obj.getString("artist"));
                            tvMusic.setText(obj.getString("music"));
                            rvDressCodeId = obj.getString("dress_id");
                            tvDressCode.setText(obj.getString("dress"));
                            id = obj.getString("artistid");
                            //tvMenu.setText(obj.getString("menu"));
                            tvMenu.setText("Food and Drink");
                            tvCategory.setText(obj.getString("category"));

                            Picasso.with(getActivity())
                                    .load(Constants.BaseImage + obj.getString("image"))
                                    .placeholder(R.drawable.image_holder)
                                    .fit().centerInside()
                                    .into(imageView);

                            JSONArray category = jsonObject.getJSONArray("category");
                            JSONArray catedesc = jsonObject.getJSONArray("catedesc");

                            for (int i = 0; i < category.length(); i++) {
                                String cat = category.getString(i);
                                String des = catedesc.getString(i);
                                if (!(cat.equals("") && des.equals(""))) {
                                    mDataList.add(new ModalEventDetails(R.mipmap.oval, cat, des));
                                }
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
        strRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

}
