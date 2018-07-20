package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import in.clubgo.app.modal.ModalLocation;

import java.util.List;

/**
 * Created by Jitendra Soam on 21/3/16.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalLocation> mDataList;
    private static MyClickListener myClickListener;

    public LocationAdapter(Context context, List<ModalLocation> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.location_txtTitle);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), mDataList.get(getPosition()).getTitle());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_location, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        holder.tvTitle.setText(mDataList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, String location);
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

}