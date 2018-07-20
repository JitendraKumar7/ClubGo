package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jitendra Soam on 21/3/16.
 */
public class VenueBarAdapter extends RecyclerView.Adapter<VenueBarAdapter.CustomViewHolder> {

    private Context context;
    private List<String> mImage;
    private static MyClickListener myClickListener;

    public VenueBarAdapter(Context context, List<String> mImage) {
        this.context = context;
        this.mImage = mImage;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage;
        private TextView tvTitle, tvOffers;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.view_home_title);
            tvOffers = (TextView) itemView.findViewById(R.id.view_home_offers);
            ivImage = (ImageView) itemView.findViewById(R.id.view_home_image);

            tvTitle.setVisibility(View.GONE);
            tvOffers.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_home, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        Picasso.with(context)
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

}