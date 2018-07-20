package in.clubgo.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.modal.ModalEvent;
import in.clubgo.app.utility.Constants;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jitendra Soam on 28/3/16.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalEvent> mDataList;
    private static MyClickListener myClickListener;

    public EventAdapter() {
    }

    public EventAdapter(Context context, List<ModalEvent> mDataList) {
        this.context = context;
        this.mDataList = mDataList;

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FrameLayout frmOffers;
        private ImageView ivMainImage, ivHeart, ivHeartSelected;
        private TextView tvTrending, tvOffers, tvName, tvLocation, tvDateTime, tvCostDiscounted, tvCostPrice, tvEventBook, tvTag1, tvTag2;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            frmOffers = (FrameLayout) itemView.findViewById(R.id.eventlist_frmEventOffer);
            tvTrending = (TextView) itemView.findViewById(R.id.eventlist_txtEventTrending);
            tvOffers = (TextView) itemView.findViewById(R.id.eventlist_txtEventOffer);
            tvName = (TextView) itemView.findViewById(R.id.eventlist_txtEventName);
            tvLocation = (TextView) itemView.findViewById(R.id.eventlist_txtEventLocation);
            ivMainImage = (ImageView) itemView.findViewById(R.id.eventlist_ivMainImage);
            tvCostDiscounted = (TextView) itemView.findViewById(R.id.eventlist_txtEventDiscountedPrice);
            tvCostDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvCostPrice = (TextView) itemView.findViewById(R.id.eventlist_txtEventPrice);
            tvDateTime = (TextView) itemView.findViewById(R.id.eventlist_txtEventTime);
            ivHeart = (ImageView) itemView.findViewById(R.id.eventlist_ivHeart);
            ivHeartSelected = (ImageView) itemView.findViewById(R.id.eventlist_ivHeart_selected);
            tvEventBook = (TextView) itemView.findViewById(R.id.eventList_txtEventBook);

            tvTag1 = (TextView) itemView.findViewById(R.id.eventlist_txtEventTag1);
            tvTag2 = (TextView) itemView.findViewById(R.id.eventlist_txtEventTag2);

            frmOffers.setOnClickListener(this);
            tvEventBook.setOnClickListener(this);

            if (BaseActivity.USERUNIQUEID == null) {
                ivHeart.setOnClickListener(this);
                ivHeartSelected.setOnClickListener(this);
            } else {

                ivHeart.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        ivHeartSelected.setVisibility(View.VISIBLE);
                        Constants.addWishlist("event", BaseActivity.USERUNIQUEID, mDataList.get(getPosition()).getId());
                    }
                });

                ivHeartSelected.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        view.setVisibility(View.GONE);
                        ivHeart.setVisibility(View.VISIBLE);
                        Constants.deleteWishlist("event", BaseActivity.USERUNIQUEID, mDataList.get(getPosition()).getId());
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

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_item, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        if (!(mDataList.get(position).getE_type().equals(""))) {
            holder.tvTrending.setVisibility(View.VISIBLE);
            holder.tvTrending.setText(mDataList.get(position).getE_type());
        } else {
            holder.tvTrending.setVisibility(View.GONE);
        }

        if (mDataList.get(position).getOffer_exist().equals("1")) {
            holder.frmOffers.setVisibility(View.VISIBLE);
            //holder.tvOffers.setText(mDataList.get(position).getOffer());
        } else {
            holder.frmOffers.setVisibility(View.GONE);
        }
        if (mDataList.get(position).getDiscounted_price().equals(mDataList.get(position).getCost())) {
            if (mDataList.get(position).getDiscounted_price().equals("null") || mDataList.get(position).getDiscounted_price().equals("0")) {
                holder.tvCostPrice.setText("Free");
                holder.tvCostDiscounted.setVisibility(View.GONE);
            } else {
                holder.tvCostDiscounted.setVisibility(View.GONE);
                holder.tvCostPrice.setText("\u20B9 " + mDataList.get(position).getDiscounted_price());
            }
        } else {
            if (mDataList.get(position).getPrice_exist().equals("1") && !mDataList.get(position).getCost().equals("0")) {
                holder.tvCostDiscounted.setVisibility(View.VISIBLE);
                holder.tvCostDiscounted.setText("\u20B9 " + mDataList.get(position).getCost());
            } else {
                holder.tvCostDiscounted.setVisibility(View.GONE);
            }
            if (mDataList.get(position).getDiscounted_price().equals("null") || mDataList.get(position).getDiscounted_price().equals("0")) {
                holder.tvCostPrice.setText("Free");
            } else {
                holder.tvCostPrice.setText("\u20B9 " + mDataList.get(position).getDiscounted_price());
            }
        }
        holder.tvName.setText(mDataList.get(position).getName());
        holder.tvDateTime.setText(mDataList.get(position).getStart() + ", " + mDataList.get(position).getDate());

        holder.tvLocation.setText(mDataList.get(position).getVenue() + ", " + mDataList.get(position).getDistance());

        Picasso.with(context)
                .load(mDataList.get(position).getImage())
                .fit().centerInside()
                .placeholder(R.drawable.image_holder)
                .into(holder.ivMainImage);

        if (mDataList.get(position).getWish().equals("1")) {
            holder.ivHeart.setVisibility(View.GONE);
            holder.ivHeartSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivHeart.setVisibility(View.VISIBLE);
            holder.ivHeartSelected.setVisibility(View.GONE);
        }

        if (!(mDataList.get(position).getTag1().equals("")) && !(mDataList.get(position).getTag2().equals(""))) {

            if (mDataList.get(position).getTag1().equals(mDataList.get(position).getTag2())) {
                holder.tvTag1.setVisibility(View.VISIBLE);
                holder.tvTag1.setText(mDataList.get(position).getTag1());
            } else {
                holder.tvTag1.setVisibility(View.VISIBLE);
                holder.tvTag1.setText(mDataList.get(position).getTag1());
                holder.tvTag2.setVisibility(View.VISIBLE);
                holder.tvTag2.setText(mDataList.get(position).getTag2());
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View view, String ids);
    }

}
