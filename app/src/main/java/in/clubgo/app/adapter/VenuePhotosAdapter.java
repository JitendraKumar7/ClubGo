package in.clubgo.app.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.app.AppController;
import in.clubgo.app.utility.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jitendra Soam on 31/3/16.
 */
public class VenuePhotosAdapter extends RecyclerView.Adapter<VenuePhotosAdapter.CustomViewHolder> {

    private Context mContext;
    private List<String> mImage;

    private static MyClickListener myClickListener;

    public VenuePhotosAdapter(Context mContext, String ids) {
        webServices(ids);
        this.mContext = mContext;
        mImage = new ArrayList<>();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private ImageView ivImage;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.around_txtTitle);
            tvTitle.setVisibility(View.GONE);
            ivImage = (ImageView) itemView.findViewById(R.id.around_ivImageView);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_arrountown, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        //holder.tvTitle.setText(mDataList.get(position).getTitle());

        Picasso.with(mContext)
                .load(Constants.BaseImage + mImage.get(position))
                .placeholder(R.drawable.image_holder)
                .fit().centerInside()
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mImage.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position);
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    private void webServices(final String ids) {

        String URLs = Constants.BaseURL + "venuedetail.php?photo=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("photo detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONArray jsonArray = mResponse.getJSONArray("photo");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                mImage.add(jsonArray.getString(i));
                            }
                            notifyDataSetChanged();

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
}


