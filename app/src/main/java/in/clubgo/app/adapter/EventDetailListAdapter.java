package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalSingleEvent;

import java.util.List;

/**
 * Created by Jitendra Soam on 6/4/16.
 */
public class EventDetailListAdapter extends RecyclerView.Adapter<EventDetailListAdapter.CustomViewHoldeer> {

    private Context mContext;
    private List<ModalSingleEvent> mData;

    public EventDetailListAdapter(List<ModalSingleEvent> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public class CustomViewHoldeer extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvOffer, tvTitle;

        public CustomViewHoldeer(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvOffer = (TextView) itemView.findViewById(R.id.event_view_txtOffers);
            tvTitle = (TextView) itemView.findViewById(R.id.event_view_txtTitle);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public CustomViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_eventview, parent, false);

        CustomViewHoldeer cvh = new CustomViewHoldeer(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CustomViewHoldeer holder, int position) {
        holder.tvOffer.setText(mData.get(position).getOffer());
        holder.tvTitle.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}