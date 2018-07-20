package in.clubgo.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.modal.ModalEvent;
import in.clubgo.app.utility.Constants;

/**
 * Created by Jitendra Soam on 28/3/16.
 */
public class FiltersTagsAdapter extends RecyclerView.Adapter<FiltersTagsAdapter.CustomViewHolder> {

    private List<String> mDataList;
    private SparseBooleanArray selectedItems;

    public FiltersTagsAdapter(List<String> mDataList) {
        this.mDataList = mDataList;
        selectedItems = new SparseBooleanArray();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTags;
        private FrameLayout frameLayout;
        private RelativeLayout myBackground;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvTags = (TextView) itemView.findViewById(R.id.filter_txtTags);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout);
            myBackground = (RelativeLayout) itemView.findViewById(R.id.relativelayout);

        }

        @Override
        public void onClick(View view) {
            // Save the selected positions to the SparseBooleanArray
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getPosition());
                myBackground.setSelected(false);
                tvTags.setTextColor(Color.BLACK);
                frameLayout.setBackgroundResource(R.drawable.back_grays);
            } else {
                selectedItems.put(getAdapterPosition(), true);
                myBackground.setSelected(true);
                tvTags.setTextColor(Color.WHITE);
                frameLayout.setBackgroundResource(R.drawable.back_reds);
            }
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_filter_tags, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.tvTags.setText(mDataList.get(position));
        Log.e("Jitendra Saom", mDataList.get(position));
        holder.myBackground.setSelected(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

}
