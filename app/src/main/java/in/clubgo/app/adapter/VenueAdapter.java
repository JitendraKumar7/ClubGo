package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.modal.ModalVenues;
import in.clubgo.app.utility.Constants;

import com.squareup.picasso.Picasso;

import java.util.List;


public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalVenues> mDataList;
    private static MyClickListener myClickListener;


    public VenueAdapter(Context context, List<ModalVenues> mDataList) {
        this.context = context;
        this.mDataList = mDataList;

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage, ivHeart, ivHeartSelected;
        private TextView tvTitle, tvLoc, tvRating;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.venueList_txtTitle);
            tvLoc = (TextView) itemView.findViewById(R.id.venueList_txtLocation);
            tvRating = (TextView) itemView.findViewById(R.id.eventList_txtEventBook);
            ivImage = (ImageView) itemView.findViewById(R.id.venueList_ivImageView);
            ivHeart = (ImageView) itemView.findViewById(R.id.venueList_heart);
            ivHeartSelected = (ImageView) itemView.findViewById(R.id.venueList_heart_selected);

            if (BaseActivity.USERUNIQUEID == null) {
                ivHeart.setOnClickListener(this);
                ivHeartSelected.setOnClickListener(this);

            } else {

                ivHeart.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        ivHeartSelected.setVisibility(View.VISIBLE);
                        Constants.addWishlist("venue", BaseActivity.USERUNIQUEID, mDataList.get(getPosition()).getId());
                    }
                });
                ivHeartSelected.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        ivHeart.setVisibility(View.VISIBLE);
                        Constants.deleteWishlist("venue", BaseActivity.USERUNIQUEID, mDataList.get(getPosition()).getId());
                    }
                });
            }

        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), view, mDataList.get(getPosition()).getId());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_venue_list, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        holder.tvTitle.setText(mDataList.get(position).getTitle());
        holder.tvLoc.setText(mDataList.get(position).getLocation());
        holder.tvRating.setText(mDataList.get(position).getRating());

        Picasso.with(context)
                .load(mDataList.get(position).getImage())
                .fit().centerCrop()
                .placeholder(R.drawable.image_holder)
                .into(holder.ivImage);


        if (mDataList.get(position).getWish().equals("1")) {
            holder.ivHeart.setVisibility(View.GONE);
            holder.ivHeartSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivHeart.setVisibility(View.VISIBLE);
            holder.ivHeartSelected.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View view, String ids);
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}


