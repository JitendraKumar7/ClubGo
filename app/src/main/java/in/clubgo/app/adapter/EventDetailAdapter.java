package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalEventDetails;

import java.util.List;

/**
 * Created by Jitendra Soam on 11/4/16.
 */
public class EventDetailAdapter extends RecyclerView.Adapter<EventDetailAdapter.CustomViewHoldeer> {

    private Context mContext;
    private List<ModalEventDetails> mDataList;

    public EventDetailAdapter(Context mContext, List<ModalEventDetails> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    public class CustomViewHoldeer extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvSubTitle;
        private ImageView ivMain;

        public CustomViewHoldeer(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.event_txtTitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.event_txtSubTitle);
            ivMain = (ImageView) itemView.findViewById(R.id.event_ivMain);
            ;
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public CustomViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_singledetail, parent, false);

        CustomViewHoldeer dataObjectHolder = new CustomViewHoldeer(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHoldeer holder, int position) {

        //holder.imageView.setImageResource(mData.get(position).getId());
        holder.ivMain.setImageResource(mDataList.get(position).getImage());
        holder.tvTitle.setText(mDataList.get(position).getTitle());
        holder.tvSubTitle.setText(mDataList.get(position).getSubTitle());
        ;

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}