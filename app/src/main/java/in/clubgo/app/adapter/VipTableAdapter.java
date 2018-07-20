package in.clubgo.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import in.clubgo.app.modal.ModalVipTable;

import java.util.List;

/**
 * Created by Jitendra Soam on 28/3/16.
 */
public class VipTableAdapter extends RecyclerView.Adapter<VipTableAdapter.CustomViewHolder> {

    private Context context;
    private List<ModalVipTable> mDataList;
    private static MyClickListener myClickListener;

    public VipTableAdapter(Context context, List<ModalVipTable> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout myBackground;
        private ImageView ivCheckBox, ivCheckBoxSelected;
        private TextView tvTitle, tvDescription, tvPrice;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tvPrice = (TextView) itemView.findViewById(R.id.vip_price);
            tvTitle = (TextView) itemView.findViewById(R.id.vip_txtTitle);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.vip_ivCheckBox);
            tvDescription = (TextView) itemView.findViewById(R.id.vip_txtDescription);
            myBackground = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            ivCheckBoxSelected = (ImageView) itemView.findViewById(R.id.vip_ivCheckBoxSelected);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_vip_table, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        holder.tvTitle.setText(mDataList.get(position).getName());
        holder.tvPrice.setText(mDataList.get(position).getPrice());
        holder.tvDescription.setText(mDataList.get(position).getDescription());
        holder.myBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.ivCheckBox.getVisibility() == View.VISIBLE) {
                    holder.ivCheckBox.setVisibility(View.GONE);
                    holder.ivCheckBoxSelected.setVisibility(View.VISIBLE);
                    myClickListener.onItemClick(position, true);
                } else {
                    holder.ivCheckBox.setVisibility(View.VISIBLE);
                    holder.ivCheckBoxSelected.setVisibility(View.GONE);
                    myClickListener.onItemClick(position, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, boolean bools);
    }

    public void setOnClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

}