package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalSingleEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by defuzed on 6/4/16.
 */
public class EventDetailGridAdapter extends RecyclerView.Adapter<EventDetailGridAdapter.CustomViewHoldeer> {

    private Context mContext;
    private List<ModalSingleEvent> mData;

    public EventDetailGridAdapter(List<ModalSingleEvent> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public class CustomViewHoldeer extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;

        public CustomViewHoldeer(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.mImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public CustomViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_eventviews, parent, false);

        CustomViewHoldeer cvh = new CustomViewHoldeer(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CustomViewHoldeer holder, int position) {
        //holder.imageView.setImageResource(mData.get(position).getPath());

        Picasso.with(mContext)
                .load(mData.get(position).getPath())
                .placeholder(R.drawable.image_holder)
                .fit().centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}