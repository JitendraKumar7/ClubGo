package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalEventList;

import java.util.List;

/**
 * Created by Jitendra Soam on 28/3/16.
 */
public class VenueListAdapter extends RecyclerView.Adapter<VenueListAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalEventList> mDataList;

    public VenueListAdapter(Context context, List<ModalEventList> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_txtTitle);
            imageView = (ImageView) itemView.findViewById(R.id.list_imageView);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_list, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        holder.imageView.setImageResource(mDataList.get(position).getImage());
        holder.textView.setText(mDataList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}