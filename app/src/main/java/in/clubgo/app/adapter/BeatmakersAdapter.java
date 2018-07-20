package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalHome;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jitendra Soam on 22/3/16.
 */
public class BeatmakersAdapter extends RecyclerView.Adapter<BeatmakersAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalHome> mDataList;
    private static MyClickListener myClickListener;

    public BeatmakersAdapter(Context context, List<ModalHome> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private ImageView ivImage;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.view_home_title);
            ivImage = (ImageView) itemView.findViewById(R.id.view_home_image);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(view, mDataList.get(getPosition()).getId());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rv_beatmaker, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.tvTitle.setText(mDataList.get(position).getTitle());

        Picasso.with(context)
                .load(mDataList.get(position).getImage())
                .placeholder(R.drawable.image_holder)
                .fit().centerInside()
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(View view, String ids);
    }
}