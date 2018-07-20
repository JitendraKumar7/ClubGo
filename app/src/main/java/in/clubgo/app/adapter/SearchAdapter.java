package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalList;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalList> mSearchList;
    private static MyClickListener myClickListener;

    public SearchAdapter(Context context, List<ModalList> mSearchList) {
        this.context = context;
        this.mSearchList = mSearchList;

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage;
        private TextView tvTitle, tvName, tvDescription;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.search_txtTitle);
            tvName = (TextView) itemView.findViewById(R.id.search_txtName);
            ivImage = (ImageView) itemView.findViewById(R.id.search_imageView);
            tvDescription = (TextView) itemView.findViewById(R.id.search_txtDescripotion);

        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), mSearchList.get(getPosition()).getItemId(), mSearchList.get(getPosition()).getType());
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_list, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        if (position == 0 && mSearchList.get(position).getTitle().equals("event")) {
            holder.tvTitle.setText("Events");
            holder.tvTitle.setVisibility(View.VISIBLE);
        } else if (mSearchList.get(position).getTitle().equals("venue")) {
            holder.tvTitle.setText("Venues");
            holder.tvTitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        holder.tvName.setText(mSearchList.get(position).getName());
        holder.tvDescription.setText(mSearchList.get(position).getDescription());


        Picasso.with(context)
                .load(mSearchList.get(position).getImage())
                .placeholder(R.drawable.image_holder)
                .fit().centerInside()
                .into(holder.ivImage);

        Log.e("xyz", mSearchList.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, String ids, String type);
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

}


