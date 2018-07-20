package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalReview;

import java.util.List;

/**
 * Created by Jitendra Soam on 20/4/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHoldeer> {

    List<ModalReview> mData;
    Context mContext;


    public ReviewAdapter(Context mContext, List<ModalReview> mData) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public class CustomViewHoldeer extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView tvDescription, tvUsername, tvSubTitle, tvRated, tvRate;

        public CustomViewHoldeer(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tvDescription = (TextView) itemView.findViewById(R.id.review_txtDetail);
            tvUsername = (TextView) itemView.findViewById(R.id.review_txtUsername);
            tvSubTitle = (TextView) itemView.findViewById(R.id.review_txtSubTitle);
            tvRate = (TextView) itemView.findViewById(R.id.review_txtRate);
            tvRated = (TextView) itemView.findViewById(R.id.review_txtRated);

            imageView = (ImageView) itemView.findViewById(R.id.view_home_image);
        }

        @Override
        public void onClick(View v) {

        }
    }


    @Override
    public CustomViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_review, parent, false);
        CustomViewHoldeer cvh = new CustomViewHoldeer(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CustomViewHoldeer holder, int position) {

        //holder.imageView.setImageResource(mData.get(position).);
        holder.tvDescription.setText(mData.get(position).getDesc());
        holder.tvUsername.setText(mData.get(position).getUsername());
        holder.tvSubTitle.setText(mData.get(position).getTime());
        holder.tvRate.setText(mData.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}